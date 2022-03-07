package com.safetynetalerts.api.data.inputservice;

import com.google.gson.Gson;
import com.safetynetalerts.api.data.dao.FireStationDao;
import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.FireStation;
import com.safetynetalerts.api.data.entity.Person;
import com.safetynetalerts.api.data.inputmodel.AggregatedInputModel;
import com.safetynetalerts.api.data.inputmodel.FireStationInputModel;
import com.safetynetalerts.api.data.inputmodel.MedicalRecordInputModel;
import com.safetynetalerts.api.data.inputmodel.PersonInputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class InputService {

    @Autowired
    private PersonDao personDao;
    @Autowired
    private FireStationDao fireStationDao;

    public void loadInMemoryDbFromInput() throws FileNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/static/input.json"));

        AggregatedInputModel aggregatedInputModel = new Gson().fromJson(bufferedReader, AggregatedInputModel.class);

        ArrayList<Person> personEntities = convertPersonFromInputModelToEntity(aggregatedInputModel.getPersonInputModels(), aggregatedInputModel.getMedicalRecordInputModels());
        personDao.saveAll(personEntities);

        ArrayList<FireStation> fireStationEntities = convertFireStationFromInputModelToEntity(aggregatedInputModel.getFireStationInputModels());
        fireStationDao.saveAll(fireStationEntities);
    }

    private ArrayList<Person> convertPersonFromInputModelToEntity(ArrayList<PersonInputModel> personInputModels, ArrayList<MedicalRecordInputModel> medicalRecordInputModels) {

        ArrayList<Person> personEntities = new ArrayList<>();

        for (PersonInputModel personInputModel : personInputModels) {

            Person person = new Person();
            person.setFirstName(personInputModel.getFirstName());
            person.setLastName(personInputModel.getLastName());
            person.setAddress(personInputModel.getAddress());
            person.setCity(personInputModel.getCity());
            person.setZip(personInputModel.getZip());
            person.setPhone(personInputModel.getPhone());
            person.setEmail(personInputModel.getEmail());

            for (MedicalRecordInputModel medicalRecordInputModel : medicalRecordInputModels) {
                if (personInputModel.getFirstName().equals(medicalRecordInputModel.getFirstName())
                        && personInputModel.getLastName().equals(medicalRecordInputModel.getLastName())) {
                    person.setBirthdate(LocalDate.parse(medicalRecordInputModel.getBirthdate(), DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                    person.setMedications(medicalRecordInputModel.getMedications());
                    person.setAllergies(medicalRecordInputModel.getAllergies());
                    break;
                }
            }
            personEntities.add(person);
        }
        return personEntities;
    }

    private ArrayList<FireStation> convertFireStationFromInputModelToEntity(ArrayList<FireStationInputModel> fireStationInputModels) {

        ArrayList<FireStation> fireStationEntities = new ArrayList<>();

        for (FireStationInputModel fireStationInputModel : fireStationInputModels) {
            FireStation fireStation = new FireStation();
            fireStation.setAddress(fireStationInputModel.getAddress());
            fireStation.setStation(fireStationInputModel.getStation());
            fireStationEntities.add(fireStation);
        }
        return fireStationEntities;
    }
}
