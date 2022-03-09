package com.safetynetalerts.api.data.input.service;

import com.google.gson.Gson;
import com.safetynetalerts.api.data.dao.FireStationDao;
import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.FireStation;
import com.safetynetalerts.api.data.entity.Person;
import com.safetynetalerts.api.data.input.model.AggregatedInputModel;
import com.safetynetalerts.api.data.input.model.FireStationInputModel;
import com.safetynetalerts.api.data.input.model.MedicalRecordInputModel;
import com.safetynetalerts.api.data.input.model.PersonInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class InputService {

    @Autowired
    private PersonDao personDao;
    @Autowired
    private FireStationDao fireStationDao;

    public void loadInMemoryDbFromInput() throws FileNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/static/input.json"));

        AggregatedInputModel aggregatedInputModel = new Gson().fromJson(bufferedReader, AggregatedInputModel.class);

        // TODO : don't use raw List
        List entities = convertFromInputModelToEntity(aggregatedInputModel);

        personDao.saveAll((ArrayList<Person>) entities.get(0));
        fireStationDao.saveAll((ArrayList<FireStation>) entities.get(1));
    }

    private List convertFromInputModelToEntity(AggregatedInputModel aggregatedInputModel) {

        ArrayList<Person> personEntities = new ArrayList<>();
        ArrayList<FireStation> fireStationEntities = new ArrayList<>();

        for (FireStationInputModel fireStationInputModel : aggregatedInputModel.getFireStationInputModels()) {
            FireStation fireStation = new FireStation();
            fireStation.setAddress(fireStationInputModel.getAddress());
            fireStation.setStation(fireStationInputModel.getStation());
            fireStationEntities.add(fireStation);
        }

        for (PersonInputModel personInputModel : aggregatedInputModel.getPersonInputModels()) {

            Person person = new Person();
            person.setFirstName(personInputModel.getFirstName());
            person.setLastName(personInputModel.getLastName());
            person.setAddress(personInputModel.getAddress());
            person.setCity(personInputModel.getCity());
            person.setZip(personInputModel.getZip());
            person.setPhone(personInputModel.getPhone());
            person.setEmail(personInputModel.getEmail());

            for (MedicalRecordInputModel medicalRecordInputModel : aggregatedInputModel.getMedicalRecordInputModels()) {
                if (personInputModel.getFirstName().equals(medicalRecordInputModel.getFirstName())
                        && personInputModel.getLastName().equals(medicalRecordInputModel.getLastName())) {
                    person.setBirthdate(LocalDate.parse(medicalRecordInputModel.getBirthdate(), DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                    person.setMedications(medicalRecordInputModel.getMedications());
                    person.setAllergies(medicalRecordInputModel.getAllergies());
                    break;
                }
            }

            for (FireStationInputModel fireStationInputModel : aggregatedInputModel.getFireStationInputModels()) {
                if (personInputModel.getAddress().equals(fireStationInputModel.getAddress())) {
                    person.setFireStation(fireStationInputModel.getStation());
                    break;
                }
            }

            personEntities.add(person);
        }
        return Arrays.asList(personEntities, fireStationEntities);
    }
}
