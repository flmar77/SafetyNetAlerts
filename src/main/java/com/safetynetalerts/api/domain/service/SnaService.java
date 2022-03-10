package com.safetynetalerts.api.domain.service;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.Person;
import com.safetynetalerts.api.domain.model.*;
import com.safetynetalerts.api.helper.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SnaService {

    @Autowired
    private PersonDao personDao;
    @Autowired
    private DateHelper dateHelper;

    public FireStationModel getFireStationModel(int stationNumber) {
        FireStationModel firestationModel = new FireStationModel();
        firestationModel.setFireStationPersonModels(new ArrayList<>());
        firestationModel.setAdultCounter(0);
        firestationModel.setChildCounter(0);
        LocalDate localDateNow = dateHelper.now();
        JMapper<FireStationPersonModel, Person> personMapper = new JMapper<>(FireStationPersonModel.class, Person.class);

        List<Person> personsByFireStation = personDao.findAllByFireStation(stationNumber);

        List<FireStationPersonModel> fireStationPersons = new ArrayList<>();
        for (Person personByFireStation : personsByFireStation) {
            FireStationPersonModel fireStationPerson = personMapper.getDestination(personByFireStation);
            fireStationPersons.add(fireStationPerson);
            if (Period.between(personByFireStation.getBirthdate(), localDateNow).getYears() <= 18) {
                firestationModel.setChildCounter(firestationModel.getChildCounter() + 1);
            } else {
                firestationModel.setAdultCounter(firestationModel.getAdultCounter() + 1);
            }
        }
        firestationModel.setFireStationPersonModels(fireStationPersons);

        return firestationModel;
    }

    public ChildAlertModel getChildAlertModel(String address) {
        ChildAlertModel childAlertModel = new ChildAlertModel();
        childAlertModel.setAlertedChildren(new ArrayList<>());
        childAlertModel.setAlertedAdults(new ArrayList<>());
        LocalDate localDateNow = dateHelper.now();
        JMapper<ChildAlertPersonModel, Person> personMapper = new JMapper<>(ChildAlertPersonModel.class, Person.class);

        List<Person> personsByAddress = personDao.findAllByAddress(address);

        List<ChildAlertPersonModel> alertedChildrenList = new ArrayList<>();
        List<ChildAlertPersonModel> alertedAdultsList = new ArrayList<>();
        for (Person personByAddress : personsByAddress) {
            ChildAlertPersonModel childAlertPersonModel = personMapper.getDestination(personByAddress);
            childAlertPersonModel.setAge(Period.between(personByAddress.getBirthdate(), localDateNow).getYears());
            if (childAlertPersonModel.getAge() <= 18) {
                alertedChildrenList.add(childAlertPersonModel);
            } else {
                alertedAdultsList.add(childAlertPersonModel);
            }
        }
        childAlertModel.setAlertedChildren(alertedChildrenList);
        childAlertModel.setAlertedAdults(alertedAdultsList);

        return childAlertModel;
    }

    public PhoneAlertModel getPhoneAlertModel(int firestation_number) {
        PhoneAlertModel phoneAlertModel = new PhoneAlertModel();
        phoneAlertModel.setPhones(new ArrayList<>());

        List<Person> personsByFireStation = personDao.findAllByFireStation(firestation_number);

        List<String> rawPhones = new ArrayList<>();
        for (Person personByFireStation : personsByFireStation) {
            rawPhones.add(personByFireStation.getPhone());
        }
        List<String> phones = rawPhones.stream().distinct().collect(Collectors.toList());
        phoneAlertModel.setPhones(phones);

        return phoneAlertModel;
    }

    public FireModel getFireModel(String address) {
        FireModel fireModel = new FireModel();
        fireModel.setFirePersons(new ArrayList<>());
        fireModel.setFireStation(-1);
        LocalDate localDateNow = dateHelper.now();
        JMapper<FirePersonModel, Person> personMapper = new JMapper<>(FirePersonModel.class, Person.class);

        List<Person> personsByAddress = personDao.findAllByAddress(address);

        List<FirePersonModel> firePersons = new ArrayList<>();
        for (Person personByAddress : personsByAddress) {
            FirePersonModel firePerson = personMapper.getDestination(personByAddress);
            firePerson.setAge(Period.between(personByAddress.getBirthdate(), localDateNow).getYears());
            firePersons.add(firePerson);
            fireModel.setFireStation(personByAddress.getFireStation());
        }
        fireModel.setFirePersons(firePersons);

        return fireModel;
    }
}
