package com.safetynetalerts.api.domain.service;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.PersonEntity;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.helper.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SnaService {

    @Autowired
    private PersonDao personDao;
    @Autowired
    private DateHelper dateHelper;

    private final JMapper<Person, PersonEntity> personMapper = new JMapper<>(Person.class, PersonEntity.class);

    private final int majorityAge = 18;

    public List<Person> getPersonsByStation(int stationNumber) {
        List<PersonEntity> personsByFireStation = personDao.findAllByFireStation(stationNumber);
        return convertPersonsEntityToPersons(personsByFireStation);
    }

    public List<Person> getPersonsByAddress(String address) {
        List<PersonEntity> personsByAddress = personDao.findAllByAddress(address);
        return convertPersonsEntityToPersons(personsByAddress);
    }

    public List<Person> getPersonsByFirstNameAndLastName(String firstName, String lastName) {
        List<PersonEntity> personsByFirstNameAndLastName = personDao.findAllByFirstNameAndLastName(firstName, lastName);
        return convertPersonsEntityToPersons(personsByFirstNameAndLastName);
    }

    public List<Person> getPersonsByCity(String city) {
        List<PersonEntity> personsByCity = personDao.findAllByCity(city);
        return convertPersonsEntityToPersons(personsByCity);
    }

    private List<Person> convertPersonsEntityToPersons(List<PersonEntity> personsByFireStation) {
        return personsByFireStation.stream()
                .map(personEntity -> {
                    Person person = personMapper.getDestination(personEntity);
                    person.setAge(getAgeOfPerson(personEntity));
                    return person;
                })
                .collect(Collectors.toList());
    }

    private int getAgeOfPerson(PersonEntity personEntity) {
        LocalDate localDateNow = dateHelper.now();
        return Period.between(personEntity.getBirthdate(), localDateNow).getYears();
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


}
