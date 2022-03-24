package com.safetynetalerts.api.data.input.service;

import com.google.gson.Gson;
import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.data.entity.FireStationEntity;
import com.safetynetalerts.api.data.entity.PersonEntity;
import com.safetynetalerts.api.data.input.model.AggregatedInputModel;
import com.safetynetalerts.api.data.input.model.FireStationInputModel;
import com.safetynetalerts.api.data.input.model.MedicalRecordInputModel;
import com.safetynetalerts.api.data.input.model.PersonInputModel;
import com.safetynetalerts.api.domain.service.SnaService;
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

@Slf4j
@Service
public class InputService {

    @Autowired
    private SnaService snaService;

    public void loadInMemoryDbFromInput() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/static/input.json");

        if (inputStream == null) {
            throw new IOException("input file not found");
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        AggregatedInputModel aggregatedInputModel = new Gson().fromJson(inputStreamReader, AggregatedInputModel.class);

        List<FireStationEntity> fireStationEntities = extractFireStation(aggregatedInputModel.getFireStationInputModels());
        snaService.saveAllFireStationEntities(fireStationEntities);

        List<PersonEntity> personEntities = extractPersonEntityFromPersonInputModel(aggregatedInputModel.getPersonInputModels());
        // TODO : passer en void
        personEntities = enrichPersonEntityFromMedicalRecordInputModel(personEntities, aggregatedInputModel.getMedicalRecordInputModels());
        personEntities = enrichPersonEntityFromFireStationEntity(personEntities, fireStationEntities);
        snaService.saveAllPersonEntities(personEntities);

    }

    private List<FireStationEntity> extractFireStation(final List<FireStationInputModel> fireStationInputModels) {

        return fireStationInputModels.stream()
                .collect(Collectors.groupingBy(FireStationInputModel::getStation
                        , Collectors.mapping(FireStationInputModel::getAddress,
                                Collectors.toList())))
                .entrySet().stream()
                .map(it -> {
                    FireStationEntity fireStationEntity = new FireStationEntity();
                    fireStationEntity.setStation((it.getKey()));
                    fireStationEntity.setAddresses((it.getValue()));
                    return fireStationEntity;
                })
                .collect(Collectors.toList());
    }

    private List<PersonEntity> extractPersonEntityFromPersonInputModel(List<PersonInputModel> personInputModels) {
        JMapper<PersonEntity, PersonInputModel> personMapper = new JMapper<>(PersonEntity.class, PersonInputModel.class);
        return personInputModels.stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());
    }

    private List<PersonEntity> enrichPersonEntityFromMedicalRecordInputModel(List<PersonEntity> personEntities, List<MedicalRecordInputModel> medicalRecordInputModels) {
        return personEntities.stream()
                .peek(personEntity -> {
                            MedicalRecordInputModel medicalRecord = medicalRecordInputModels.stream()
                                    .filter(medicalRecord1 -> personEntity.getFirstName().equals(medicalRecord1.getFirstName())
                                            && personEntity.getLastName().equals(medicalRecord1.getLastName()))
                                    .findFirst().orElse(null);
                            if (medicalRecord != null) {
                                personEntity.setBirthdate(LocalDate.parse(medicalRecord.getBirthdate(), DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                                personEntity.setMedications(medicalRecord.getMedications());
                                personEntity.setAllergies(medicalRecord.getAllergies());
                            }
                        }
                )
                .collect(Collectors.toList());
    }

    private List<PersonEntity> enrichPersonEntityFromFireStationEntity(List<PersonEntity> personEntities, List<FireStationEntity> fireStationEntities) {
        return personEntities.stream()
                .peek(personEntity -> fireStationEntities.stream()
                        .filter(fireStationEntity1 -> fireStationEntity1.getAddresses().contains(personEntity.getAddress()))
                        .findFirst()
                        .ifPresent(fireStationEntity -> personEntity.setFireStation(fireStationEntity.getStation())))
                .collect(Collectors.toList());
    }

}
