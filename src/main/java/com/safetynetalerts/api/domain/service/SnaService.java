package com.safetynetalerts.api.domain.service;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.PersonEntity;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.helper.DateHelper;
import com.safetynetalerts.api.web.dto.PersonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SnaService {

    @Autowired
    private PersonDao personDao;
    @Autowired
    private DateHelper dateHelper;

    private final JMapper<Person, PersonEntity> personEntityToPersonMapper = new JMapper<>(Person.class, PersonEntity.class);

    private final int majorityAge = 18;

    public List<Person> getPersonsByStation(int stationNumber) {
        List<PersonEntity> personsByFireStation = personDao.findAllByFireStation(stationNumber);
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

    public Person savePerson(PersonDto personDto) {
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
