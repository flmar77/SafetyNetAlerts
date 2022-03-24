package com.safetynetalerts.api.domain.service;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.data.entity.FireStationEntity;
import com.safetynetalerts.api.data.entity.PersonEntity;
import com.safetynetalerts.api.data.repository.FireStationRepo;
import com.safetynetalerts.api.data.repository.PersonRepo;
import com.safetynetalerts.api.domain.model.FireStation;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.helper.DateHelper;
import com.safetynetalerts.api.web.dto.FireStationsDto;
import com.safetynetalerts.api.web.dto.MedicalRecordsDto;
import com.safetynetalerts.api.web.dto.PersonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

//TODO : à découper en firestationService & personService ?
//TODO : essayer de remonter les exceptions en début de méthodes

@Service
public class SnaService {

    @Autowired
    private PersonRepo personRepo;
    @Autowired
    private FireStationRepo fireStationRepo;
    @Autowired
    private DateHelper dateHelper;

    private final JMapper<Person, PersonEntity> personEntityToPersonMapper = new JMapper<>(Person.class, PersonEntity.class);

    private final int majorityAge = 18;

    public List<Person> getPersonsByStation(int stationNumber) {
        List<PersonEntity> personsByFireStation = personRepo.findAllByFireStation(stationNumber);
        return mapPersonsEntityToPersons(personsByFireStation);
    }

    public List<Person> getPersonsByStations(List<Integer> stationNumbers) {
        List<PersonEntity> personsByFireStation = personRepo.findAllByFireStationIn(stationNumbers);
        return mapPersonsEntityToPersons(personsByFireStation);
    }

    public List<Person> getPersonsByAddress(String address) {
        List<PersonEntity> personsByAddress = personRepo.findAllByAddress(address);
        return mapPersonsEntityToPersons(personsByAddress);
    }

    public List<Person> getPersonsByFirstNameAndLastName(String firstName, String lastName) {
        List<PersonEntity> personsByFirstNameAndLastName = personRepo.findAllByFirstNameAndLastName(firstName, lastName);
        return mapPersonsEntityToPersons(personsByFirstNameAndLastName);
    }

    public List<Person> getPersonsByCity(String city) {
        List<PersonEntity> personsByCity = personRepo.findAllByCity(city);
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
        Optional<PersonEntity> optionalPersonEntity = personRepo.findByFirstNameAndLastName(firstName, lastName);
        return optionalPersonEntity.isPresent();
    }

    public List<Person> getAllPersons() {
        List<PersonEntity> personsEntity = new ArrayList<>();
        personRepo.findAll().forEach(personsEntity::add);
        return mapPersonsEntityToPersons(personsEntity);
    }

    public Person createPerson(PersonDto personDto) {
        return mapPersonEntityToPerson(personRepo.save(mapPersonDtoToPersonEntity(personDto)));
    }

    public Person updatePersonWithoutMedicalRecords(String firstName, String lastName, PersonDto personDto) {
        PersonEntity personEntity = personRepo.findByFirstNameAndLastName(firstName, lastName)
                .orElseThrow(NoSuchElementException::new);

        personEntity.setAddress(personDto.getAddress());
        personEntity.setCity(personDto.getCity());
        personEntity.setZip(personDto.getZip());
        personEntity.setPhone(personDto.getPhone());
        personEntity.setEmail(personDto.getEmail());
        personEntity.setBirthdate(personDto.getBirthdate());

        return mapPersonEntityToPerson(personRepo.save(personEntity));
    }

    public Person updatePersonMedicalRecords(String firstName, String lastName, MedicalRecordsDto medicalRecordsDto) {
        PersonEntity personEntity = personRepo.findByFirstNameAndLastName(firstName, lastName)
                .orElseThrow(NoSuchElementException::new);

        personEntity.setMedications(medicalRecordsDto.getMedications());
        personEntity.setAllergies(medicalRecordsDto.getAllergies());

        return mapPersonEntityToPerson(personRepo.save(personEntity));
    }

    public void deletePersonMedicalRecords(String firstName, String lastName) {
        MedicalRecordsDto emptyMedicalRecords = new MedicalRecordsDto();
        emptyMedicalRecords.setFirstName(firstName);
        emptyMedicalRecords.setLastName(lastName);
        emptyMedicalRecords.setMedications(new ArrayList<>());
        emptyMedicalRecords.setAllergies(new ArrayList<>());

        Person person = updatePersonMedicalRecords(firstName, lastName, emptyMedicalRecords);
    }

    public void deletePerson(String firstName, String lastName) {
        PersonEntity personEntity = personRepo.findByFirstNameAndLastName(firstName, lastName)
                .orElseThrow(NoSuchElementException::new);

        personRepo.delete(personEntity);
    }

    public List<FireStation> getAllFireStations() {

        return fireStationRepo.findAll().stream()
                .map(fireStationEntity -> {
                    FireStation fireStation = new FireStation();
                    fireStation.setStation(fireStationEntity.getStation());
                    fireStation.setAddresses(fireStationEntity.getAddresses());
                    return fireStation;
                })
                .collect(Collectors.toList());
    }

    public FireStation getFireStationByStationAndAddress(int station, String address) {

        FireStation fireStation = new FireStation();

        FireStationEntity fireStationEntity = fireStationRepo.findByStation(station)
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
        Optional<FireStationEntity> optionalFireStationEntity = fireStationRepo.findByStation(fireStationsDto.getStation());

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
                fireStationRepo.save(fireStationEntity);
            }

        } else {
            // station doesn't exist
            FireStationEntity fireStationEntity = new FireStationEntity();
            fireStationEntity.setStation(fireStationsDto.getStation());
            fireStationEntity.setAddresses(Collections.singletonList(fireStationsDto.getAddress()));
            fireStationRepo.save(fireStationEntity);
        }
    }

    //TODO : 2 saves sur un contexte non secure
    public void updateFireStationMapping(FireStationsDto fireStationsDto) {

        Optional<FireStationEntity> optionalFireStationEntity = fireStationRepo.findByAddresses(fireStationsDto.getAddress());

        if (optionalFireStationEntity.isPresent()) {
            // address exists

            FireStationEntity fireStationEntity = optionalFireStationEntity.get();

            if (fireStationEntity.getStation() == (fireStationsDto.getStation())) {
                // address exists and is already assigned to this station
                throw new EntityExistsException();
            } else {
                // address exists so ...
                // address has to be deleted from the actual fireStation
                fireStationEntity.setAddresses(fireStationEntity.getAddresses().stream()
                        .filter(it -> !it.equals(fireStationsDto.getAddress()))
                        .collect(Collectors.toList()));
                fireStationRepo.save(fireStationEntity);

                // and has to be assigned to the new one (almost same as create)
                createFireStationMapping(fireStationsDto);

                // fireStation's persons have also to be updated
                List<PersonEntity> personsByAddress = personRepo.findAllByAddress(fireStationsDto.getAddress());
                personRepo.saveAll(personsByAddress.stream()
                        .peek(it -> it.setFireStation(fireStationsDto.getStation()))
                        .collect(Collectors.toList()));
            }
        } else {
            // address doesn't exist
            throw new NoSuchElementException();
        }
    }

    public void deleteFireStationMapping(int station, String address) {

        Optional<FireStationEntity> optionalFireStationEntity = fireStationRepo.findByAddresses(address);

        if (optionalFireStationEntity.isPresent()) {
            // address exists

            FireStationEntity fireStationEntity = optionalFireStationEntity.get();

            if (fireStationEntity.getStation() == (station)) {
                // address exists and is assigned to this station so...
                // address has to be deleted from this station
                List<String> newAddresses = fireStationEntity.getAddresses().stream()
                        .filter(it -> !it.equals(address))
                        .collect(Collectors.toList());
                fireStationEntity.setAddresses(newAddresses);
                fireStationRepo.save(fireStationEntity);

                // and fireStation's persons has to be initialized
                List<PersonEntity> personsByAddress = personRepo.findAllByAddress(address);
                personRepo.saveAll(personsByAddress.stream()
                        .peek(it -> it.setFireStation(0))
                        .collect(Collectors.toList()));

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
        personEntityToSave.setFireStation(0);
        return personEntityToSave;
    }

    public void saveAllFireStationEntities(List<FireStationEntity> fireStationEntities) {
        fireStationRepo.saveAll(fireStationEntities);
    }

    public void saveAllPersonEntities(List<PersonEntity> personEntities) {
        personRepo.saveAll(personEntities);
    }
}
