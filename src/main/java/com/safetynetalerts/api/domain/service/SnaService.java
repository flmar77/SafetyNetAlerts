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

    public List<Person> getPersonsByStation(int stationNumber) {

        List<PersonEntity> personsByFireStation = personDao.findAllByFireStation(stationNumber);

        return personsByFireStation.stream()
                .map(personEntity -> {
                    Person person = personMapper.getDestination(personEntity);
                    person.setAge(getAge(personEntity));
                    return person;
                })
                .collect(Collectors.toList());
    }

    public List<Person> getPersonsByAddress(String address) {

        List<PersonEntity> personsByAddress = personDao.findAllByAddress(address);

        return personsByAddress.stream()
                .map(personEntity -> {
                    Person person = personMapper.getDestination(personEntity);
                    person.setAge(getAge(personEntity));
                    return person;
                })
                .collect(Collectors.toList());
    }

    private int getAge(PersonEntity personEntity) {
        LocalDate localDateNow = dateHelper.now();
        return Period.between(personEntity.getBirthdate(), localDateNow).getYears();
    }

    public long getChildCounter(List<Person> persons) {
        return persons.stream()
                .filter(person -> person.getAge() <= 18)
                .count();
    }

    public long getAdultCounter(List<Person> persons) {
        return persons.stream()
                .filter(person -> person.getAge() > 18)
                .count();
    }

    public List<Person> getChildren(List<Person> persons) {
        return persons.stream()
                .filter(person -> person.getAge() <= 18)
                .collect(Collectors.toList());
    }

    public List<Person> getAdults(List<Person> persons) {
        return persons.stream()
                .filter(person -> person.getAge() > 18)
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
