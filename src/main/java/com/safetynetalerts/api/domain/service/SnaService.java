package com.safetynetalerts.api.domain.service;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.data.dao.FireStationDao;
import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.FireStationEntity;
import com.safetynetalerts.api.data.entity.PersonEntity;
import com.safetynetalerts.api.domain.model.FireStation;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.helper.DateHelper;
import com.safetynetalerts.api.web.dto.FireStationsDto;
import com.safetynetalerts.api.web.dto.PersonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SnaService {

    @Autowired
    private PersonDao personDao;
    @Autowired
    private FireStationDao fireStationDao;
    @Autowired
    private DateHelper dateHelper;

    private final JMapper<Person, PersonEntity> personEntityToPersonMapper = new JMapper<>(Person.class, PersonEntity.class);

    private final int majorityAge = 18;

    public List<Person> getPersonsByStation(Long stationNumber) {
        List<PersonEntity> personsByFireStation = personDao.findAllByFireStation(stationNumber);
        return mapPersonsEntityToPersons(personsByFireStation);
    }

    public List<Person> getPersonsByStations(List<Long> stationNumbers) {
        List<PersonEntity> personsByFireStation = personDao.findAllByFireStationIn(stationNumbers);
        return mapPersonsEntityToPersons(personsByFireStation);
    }

    public List<Person> getPersonsByAddress(String address) {
        List<PersonEntity> personsByAddress = personDao.findAllByAddress(address);
        return mapPersonsEntityToPersons(personsByAddress);
    }

    public List<Person> getPersonsByFirstNameAndLastName(String firstName, String lastName) {
        List<PersonEntity> personsByFirstNameAndLastName = personDao.findAllByFirstNameAndLastName(firstName, lastName);
        return mapPersonsEntityToPersons(personsByFirstNameAndLastName);
    }

    public List<Person> getPersonsByCity(String city) {
        List<PersonEntity> personsByCity = personDao.findAllByCity(city);
        return mapPersonsEntityToPersons(personsByCity);
    }

    public long getChildCounter(List<Person> persons) {
        return persons.stream()
                .filter(person -> person.getAge() <= majorityAge)
                .count();
    }

    public long getAdultCounter(List<Person> persons) {
        return persons.stream()
                .filter(person -> person.getAge() > majorityAge)
                .count();
    }

    public List<Person> getChildren(List<Person> persons) {
        return persons.stream()
                .filter(person -> person.getAge() <= majorityAge)
                .collect(Collectors.toList());
    }

    public List<Person> getAdults(List<Person> persons) {
        return persons.stream()
                .filter(person -> person.getAge() > majorityAge)
                .collect(Collectors.toList());
    }

    public int getFireStation(List<Person> persons) {
        if (persons.size() > 0) {
            return persons.get(0).getFireStation();
        } else {
            return 0;
        }
    }

    public boolean personAlreadyExists(String firstName, String lastName) {
        Optional<PersonEntity> optionalPersonEntity = personDao.findByFirstNameAndLastName(firstName, lastName);
        return optionalPersonEntity.isPresent();
    }

    public List<Person> getAllPersons() {
        List<PersonEntity> personsEntity = new ArrayList<>();
        personDao.findAll().forEach(personsEntity::add);
        return mapPersonsEntityToPersons(personsEntity);
    }

    public Person createPerson(PersonDto personDto) {
        return mapPersonEntityToPerson(personDao.save(mapPersonDtoToPersonEntity(personDto)));
    }

    public Person updatePerson(String firstName, String lastName, PersonDto personDto) {
        PersonEntity personEntity = personDao.findByFirstNameAndLastName(firstName, lastName)
                .orElseThrow(NoSuchElementException::new);

        personEntity.setAddress(personDto.getAddress());
        personEntity.setCity(personDto.getCity());
        personEntity.setZip(personDto.getZip());
        personEntity.setPhone(personDto.getPhone());
        personEntity.setEmail(personDto.getEmail());
        personEntity.setBirthdate(personDto.getBirthdate());

        return mapPersonEntityToPerson(personDao.save(personEntity));
    }

    public void deletePerson(String firstName, String lastName) {
        PersonEntity personEntity = personDao.findByFirstNameAndLastName(firstName, lastName)
                .orElseThrow(NoSuchElementException::new);

        personDao.delete(personEntity);
    }

    public List<FireStation> getAllFireStations() {

        return fireStationDao.findAll().stream()
                .map(fireStationEntity -> {
                    FireStation fireStation = new FireStation();
                    fireStation.setStation(fireStationEntity.getStation());
                    fireStation.setAddresses(fireStationEntity.getAddresses());
                    return fireStation;
                })
                .collect(Collectors.toList());
    }

    public FireStation getFireStationByStationAndAddress(Long station, String address) {

        FireStation fireStation = new FireStation();

        FireStationEntity fireStationEntity = fireStationDao.findById(station)
                .orElseThrow(NoSuchElementException::new);

        if (fireStationEntity.getAddresses().stream().anyMatch(addressEntity -> addressEntity.equals(address))) {
            fireStation.setStation(station);
            fireStation.setAddresses(Collections.singletonList(address));
        } else {
            throw new NoSuchElementException();
        }

        return fireStation;

    }

    public void createFireStationMapping(FireStationsDto fireStationsDto) {
        Optional<FireStationEntity> optionalFireStationEntity = fireStationDao.findById(fireStationsDto.getStation());

        if (optionalFireStationEntity.isPresent()) {
            // station exists
            FireStationEntity fireStationEntity = optionalFireStationEntity.get();

            if (fireStationEntity.getAddresses().stream().anyMatch(addressEntity -> addressEntity.equals(fireStationsDto.getAddress()))) {
                // station exists and address exists
                throw new EntityExistsException();

            } else {
                // station exists and address doesn't exist
                List<String> newAddresses = new ArrayList<>(fireStationEntity.getAddresses());
                newAddresses.add(fireStationsDto.getAddress());
                fireStationEntity.setAddresses(newAddresses);
                fireStationDao.save(fireStationEntity);
            }

        } else {
            // station doesn't exist
            FireStationEntity fireStationEntity = new FireStationEntity();
            fireStationEntity.setStation(fireStationsDto.getStation());
            fireStationEntity.setAddresses(Collections.singletonList(fireStationsDto.getAddress()));
            fireStationDao.save(fireStationEntity);
        }
    }

    public void updateFireStationMapping(FireStationsDto fireStationsDto) {

        Optional<FireStationEntity> optionalFireStationEntity = fireStationDao.findByAddresses(fireStationsDto.getAddress());

        if (optionalFireStationEntity.isPresent()) {
            // address exists

            FireStationEntity fireStationEntity = optionalFireStationEntity.get();

            if (fireStationEntity.getStation().equals(fireStationsDto.getStation())) {
                // address exists and is already assigned to this station
                throw new EntityExistsException();
            } else {
                // address exists and ...
                // has to be deleted from the actual fireStation
                fireStationEntity.setAddresses(fireStationEntity.getAddresses().stream()
                        .filter(it -> !it.equals(fireStationsDto.getAddress()))
                        .collect(Collectors.toList()));
                fireStationDao.save(fireStationEntity);

                //  has to be assigned to the new one (almost same as create)
                createFireStationMapping(fireStationsDto);

                // fireStation's persons have also to be updated
                List<PersonEntity> personsByAddress = personDao.findAllByAddress(fireStationsDto.getAddress());
                personDao.saveAll(personsByAddress.stream()
                        .peek(it -> it.setFireStation(fireStationsDto.getStation()))
                        .collect(Collectors.toList()));
            }
        } else {
            // address doesn't exist
            throw new NoSuchElementException();
        }
    }

    public void deleteFireStationMapping(Long station, String address) {

        Optional<FireStationEntity> optionalFireStationEntity = fireStationDao.findByAddresses(address);

        if (optionalFireStationEntity.isPresent()) {
            // address exists

            FireStationEntity fireStationEntity = optionalFireStationEntity.get();

            if (fireStationEntity.getStation().equals(station)) {
                // address exists and is assigned to this station
                List<String> newAddresses = fireStationEntity.getAddresses().stream()
                        .filter(it -> !it.equals(address))
                        .collect(Collectors.toList());
                fireStationEntity.setAddresses(newAddresses);
                fireStationDao.save(fireStationEntity);

            } else {
                // address exists but is not assigned to this station
                throw new NoSuchElementException();
            }

        } else {
            //address doesn't exist
            throw new NoSuchElementException();
        }
    }

    private List<Person> mapPersonsEntityToPersons(List<PersonEntity> personsEntity) {
        return personsEntity.stream()
                .map(personEntity -> {
                    Person person = personEntityToPersonMapper.getDestination(personEntity);
                    person.setAge(getAgeOfPerson(personEntity));
                    return person;
                })
                .collect(Collectors.toList());
    }

    private Person mapPersonEntityToPerson(PersonEntity personEntity) {
        Person person = personEntityToPersonMapper.getDestination(personEntity);
        person.setAge(getAgeOfPerson(personEntity));
        return person;
    }

    private int getAgeOfPerson(PersonEntity personEntity) {
        LocalDate localDateNow = dateHelper.now();
        return Period.between(personEntity.getBirthdate(), localDateNow).getYears();
    }

    private PersonEntity mapPersonDtoToPersonEntity(PersonDto personDto) {
        JMapper<PersonEntity, PersonDto> personDtoToEntityMapper = new JMapper<>(PersonEntity.class, PersonDto.class);
        PersonEntity personEntityToSave = personDtoToEntityMapper.getDestination(personDto);
        personEntityToSave.setBirthdate(personDto.getBirthdate());
        return personEntityToSave;
    }

}
