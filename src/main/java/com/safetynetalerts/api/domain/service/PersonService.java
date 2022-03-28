package com.safetynetalerts.api.domain.service;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.dao.entity.PersonEntity;
import com.safetynetalerts.api.dao.repository.PersonRepo;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.helper.DateHelper;
import com.safetynetalerts.api.web.dto.MedicalRecordsDto;
import com.safetynetalerts.api.web.dto.PersonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PersonService {

    @Autowired
    private PersonRepo personRepo;
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

    public List<Person> getAllPersons() {
        List<PersonEntity> personsEntity = new ArrayList<>();
        personRepo.findAll().forEach(personsEntity::add);
        return mapPersonsEntityToPersons(personsEntity);
    }

    public Person createPerson(PersonDto personDto) {
        if (personRepo.findByFirstNameAndLastName(personDto.getFirstName(), personDto.getLastName()).isPresent()) {
            throw new EntityExistsException();
        }

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

    public void saveAllPersonEntities(List<PersonEntity> personEntities) {
        personRepo.saveAll(personEntities);
    }

    public void saveAllPersons(List<Person> persons) {
        JMapper<PersonEntity, Person> personToEntityMapper = new JMapper<>(PersonEntity.class, Person.class);
        List<PersonEntity> personEntities = persons.stream()
                .map(personToEntityMapper::getDestination)
                .collect(Collectors.toList());
        saveAllPersonEntities(personEntities);
    }

    public int getFireStation(List<Person> persons) {
        if (persons.size() > 0) {
            return persons.get(0).getFireStation();
        } else {
            return 0;
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

}
