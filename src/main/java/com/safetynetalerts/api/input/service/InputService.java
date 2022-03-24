package com.safetynetalerts.api.input.service;

import com.google.gson.Gson;
import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.data.entity.FireStationEntity;
import com.safetynetalerts.api.data.entity.PersonEntity;
import com.safetynetalerts.api.domain.service.SnaService;
import com.safetynetalerts.api.input.model.AggregatedInputModel;
import com.safetynetalerts.api.input.model.FireStationInputModel;
import com.safetynetalerts.api.input.model.MedicalRecordInputModel;
import com.safetynetalerts.api.input.model.PersonInputModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

//TODO : add test

@Slf4j
@Service
public class InputService {

    @Autowired
    private SnaService snaService;

    private List<PersonEntity> personEntities;
    private List<FireStationEntity> fireStationEntities;

    public void loadInMemoryDbFromInput() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/static/input.json");

        if (inputStream == null) {
            throw new IOException("input file not found");
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        AggregatedInputModel aggregatedInputModel = new Gson().fromJson(inputStreamReader, AggregatedInputModel.class);

        extractFireStation(aggregatedInputModel.getFireStationInputModels());
        snaService.saveAllFireStationEntities(fireStationEntities);

        extractPersonEntityFromPersonInputModel(aggregatedInputModel.getPersonInputModels());
        enrichPersonEntityFromMedicalRecordInputModel(aggregatedInputModel.getMedicalRecordInputModels());
        enrichPersonEntityFromFireStationEntity(fireStationEntities);
        snaService.saveAllPersonEntities(personEntities);

    }

    private void extractFireStation(final List<FireStationInputModel> fireStationInputModels) {

        fireStationEntities = fireStationInputModels.stream()
                .collect(Collectors.groupingBy(FireStationInputModel::getStation
                        , Collectors.mapping(FireStationInputModel::getAddress,
                                Collectors.toList())))
                .entrySet().stream()
                .map(mapEntry -> {
                    FireStationEntity fireStationEntity = new FireStationEntity();
                    fireStationEntity.setStation((mapEntry.getKey()));
                    fireStationEntity.setAddresses((mapEntry.getValue()));
                    return fireStationEntity;
                })
                .collect(Collectors.toList());
    }

    private void extractPersonEntityFromPersonInputModel(List<PersonInputModel> personInputModels) {
        JMapper<PersonEntity, PersonInputModel> personMapper = new JMapper<>(PersonEntity.class, PersonInputModel.class);
        personEntities = personInputModels.stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());
    }

    private void enrichPersonEntityFromMedicalRecordInputModel(List<MedicalRecordInputModel> medicalRecordInputModels) {
        personEntities = personEntities.stream()
                .peek(personEntity -> medicalRecordInputModels.stream()
                        .filter(medicalRecordFromStream -> personEntity.getFirstName().equals(medicalRecordFromStream.getFirstName())
                                && personEntity.getLastName().equals(medicalRecordFromStream.getLastName()))
                        .findFirst()
                        .ifPresent(medicalRecordMatch -> {
                            personEntity.setBirthdate(LocalDate.parse(medicalRecordMatch.getBirthdate(), DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                            personEntity.setMedications(medicalRecordMatch.getMedications());
                            personEntity.setAllergies(medicalRecordMatch.getAllergies());
                        }))
                .collect(Collectors.toList());
    }

    private void enrichPersonEntityFromFireStationEntity(List<FireStationEntity> fireStationEntities) {
        personEntities = personEntities.stream()
                .peek(personEntity -> fireStationEntities.stream()
                        .filter(fireStationEntityFromStream -> fireStationEntityFromStream.getAddresses().contains(personEntity.getAddress()))
                        .findFirst()
                        .ifPresent(fireStationEntityMatch -> personEntity.setFireStation(fireStationEntityMatch.getStation())))
                .collect(Collectors.toList());
    }

}
