package com.safetynetalerts.api.domain.service;

import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.Person;
import com.safetynetalerts.api.domain.model.AlertedChildrenAndAdults;
import com.safetynetalerts.api.domain.model.AlertedPerson;
import com.safetynetalerts.api.domain.model.CoveredPerson;
import com.safetynetalerts.api.domain.model.CoveredPersonsAndStats;
import com.safetynetalerts.api.helper.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
public class SnaService {

    @Autowired
    private PersonDao personDao;
    @Autowired
    private DateHelper dateHelper;

    public CoveredPersonsAndStats getCoveredPersonsAndStatsByFireStation(int stationNumber) {
        CoveredPersonsAndStats coveredPersonsAndStats = new CoveredPersonsAndStats();
        coveredPersonsAndStats.setCoveredPersons(new ArrayList<>());
        coveredPersonsAndStats.setAdultCounter(0);
        coveredPersonsAndStats.setChildCounter(0);
        LocalDate localDateNow = dateHelper.now();

        List<Person> personsByFireStation = personDao.findAllByFireStation(stationNumber);

        List<CoveredPerson> coveredPersons = new ArrayList<>();
        for (Person personByFireStation : personsByFireStation) {
            CoveredPerson coveredPerson = new CoveredPerson();
            coveredPerson.setFirstName(personByFireStation.getFirstName());
            coveredPerson.setLastName(personByFireStation.getLastName());
            coveredPerson.setAddress(personByFireStation.getAddress());
            coveredPerson.setPhone(personByFireStation.getPhone());
            coveredPersons.add(coveredPerson);
            if (Period.between(personByFireStation.getBirthdate(), localDateNow).getYears() <= 18) {
                coveredPersonsAndStats.setChildCounter(coveredPersonsAndStats.getChildCounter() + 1);
            } else {
                coveredPersonsAndStats.setAdultCounter(coveredPersonsAndStats.getAdultCounter() + 1);
            }
        }
        coveredPersonsAndStats.setCoveredPersons(coveredPersons);

        return coveredPersonsAndStats;
    }

    public AlertedChildrenAndAdults getAlertedChildrenAndAdultsByAddress(String address) {
        AlertedChildrenAndAdults alertedChildrenAndAdults = new AlertedChildrenAndAdults();
        alertedChildrenAndAdults.setAlertedChildren(new ArrayList<>());
        alertedChildrenAndAdults.setAlertedAdults(new ArrayList<>());
        LocalDate localDateNow = dateHelper.now();

        List<Person> personsByAddress = personDao.findAllByAddress(address);

        List<AlertedPerson> alertedChildrenList = new ArrayList<>();
        List<AlertedPerson> alertedAdultsList = new ArrayList<>();
        for (Person personByAddress : personsByAddress) {
            AlertedPerson alertedPerson = new AlertedPerson();
            alertedPerson.setFirstName(personByAddress.getFirstName());
            alertedPerson.setLastName(personByAddress.getLastName());
            alertedPerson.setAge(Period.between(personByAddress.getBirthdate(), localDateNow).getYears());
            if (alertedPerson.getAge() <= 18) {
                alertedChildrenList.add(alertedPerson);
            } else {
                alertedAdultsList.add(alertedPerson);
            }
        }
        alertedChildrenAndAdults.setAlertedChildren(alertedChildrenList);
        alertedChildrenAndAdults.setAlertedAdults(alertedAdultsList);

        return alertedChildrenAndAdults;
    }
}
