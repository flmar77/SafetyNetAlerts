package com.safetynetalerts.api.integrationtests;

import com.google.gson.Gson;
import com.safetynetalerts.api.data.entity.PersonEntity;
import com.safetynetalerts.api.data.repository.PersonRepo;
import com.safetynetalerts.api.web.dto.MedicalRecordsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MedicalRecordsCrudControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepo personRepo;

    @Test
    public void should_getAllMedicalRecordsDto() throws Exception {
        mockMvc.perform(get("/medicalRecords"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_getMedicalRecordsDto() throws Exception {
        mockMvc.perform(get("/medicalRecords/abc&abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_updateMedicalRecordsDto() throws Exception {
        PersonEntity personEntity = new PersonEntity();
        personEntity.setFirstName("updateMrFirstName");
        personEntity.setLastName("updateMrLastName");
        personEntity.setBirthdate(LocalDate.of(2000, 1, 1));
        personRepo.save(personEntity);

        MedicalRecordsDto medicalRecordsDto = new MedicalRecordsDto();
        medicalRecordsDto.setFirstName(personEntity.getFirstName());
        medicalRecordsDto.setLastName(personEntity.getLastName());
        medicalRecordsDto.setMedications(Arrays.asList("m1", "m2"));
        medicalRecordsDto.setAllergies(Arrays.asList("a1", "a2"));
        String inputJson = new Gson().toJson(medicalRecordsDto);

        mockMvc.perform(put("/medicalRecords/updateMrFirstName&updateMrLastName")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_deleteMedicalRecordsDto() throws Exception {
        PersonEntity personEntity = new PersonEntity();
        personEntity.setFirstName("deleteMrFirstName");
        personEntity.setLastName("deleteMrLastName");
        personEntity.setBirthdate(LocalDate.of(2000, 1, 1));
        personRepo.save(personEntity);

        mockMvc.perform(delete("/medicalRecords/deleteMrFirstName&deleteMrLastName"))
                .andExpect(status().isOk());
    }
}
