package com.safetynetalerts.api.domain.service;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.PersonEntity;
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
        JMapper<FireStationPersonModel, PersonEntity> personMapper = new JMapper<>(FireStationPersonModel.class, PersonEntity.class);

        List<PersonEntity> personsByFireStation = personDao.findAllByFireStation(stationNumber);

        List<FireStationPersonModel> fireStationPersons = new ArrayList<>();
        for (PersonEntity personEntityByFireStation : personsByFireStation) {
            FireStationPersonModel fireStationPerson = personMapper.getDestination(personEntityByFireStation);
            fireStationPersons.add(fireStationPerson);
            if (Period.between(personEntityByFireStation.getBirthdate(), localDateNow).getYears() <= 18) {
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
        JMapper<ChildAlertPersonModel, PersonEntity> personMapper = new JMapper<>(ChildAlertPersonModel.class, PersonEntity.class);

        List<PersonEntity> personsByAddresses = personDao.findAllByAddress(address);

        List<ChildAlertPersonModel> alertedChildrenList = new ArrayList<>();
        List<ChildAlertPersonModel> alertedAdultsList = new ArrayList<>();
        for (PersonEntity personEntityByAddress : personsByAddresses) {
            ChildAlertPersonModel childAlertPersonModel = personMapper.getDestination(personEntityByAddress);
            childAlertPersonModel.setAge(Period.between(personEntityByAddress.getBirthdate(), localDateNow).getYears());
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

        List<PersonEntity> personsByFireStation = personDao.findAllByFireStation(firestation_number);

        List<String> rawPhones = new ArrayList<>();
        for (PersonEntity personEntityByFireStation : personsByFireStation) {
            rawPhones.add(personEntityByFireStation.getPhone());
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
        JMapper<FirePersonModel, PersonEntity> personMapper = new JMapper<>(FirePersonModel.class, PersonEntity.class);

        List<PersonEntity> personsByAddresses = personDao.findAllByAddress(address);

        List<FirePersonModel> firePersons = new ArrayList<>();
        for (PersonEntity personEntityByAddress : personsByAddresses) {
            FirePersonModel firePerson = personMapper.getDestination(personEntityByAddress);
            firePerson.setAge(Period.between(personEntityByAddress.getBirthdate(), localDateNow).getYears());
            firePersons.add(firePerson);
            fireModel.setFireStation(personEntityByAddress.getFireStation());
        }
        fireModel.setFirePersons(firePersons);

        return fireModel;
    }
}
