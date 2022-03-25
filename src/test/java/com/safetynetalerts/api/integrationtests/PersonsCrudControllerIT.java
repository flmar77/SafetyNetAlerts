package com.safetynetalerts.api.integrationtests;

import com.safetynetalerts.api.data.entity.PersonEntity;
import com.safetynetalerts.api.data.repository.PersonRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonsCrudControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepo personRepo;

    @Test
    public void should_getAllPersonsDto() throws Exception {
        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_getPersonsDto() throws Exception {
        mockMvc.perform(get("/persons/abc&abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_createPersonDto() throws Exception {
        String inputJson = "{\"firstName\":\"createFirstName\",\"lastName\":\"createLastName\",\"birthdate\":\"2000-01-01\"}";

        mockMvc.perform(post("/persons")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void should_updatePersonDto() throws Exception {
        PersonEntity personEntity = new PersonEntity();
        personEntity.setFirstName("updateFirstName");
        personEntity.setLastName("updateLastName");
        personEntity.setBirthdate(LocalDate.of(2000, 1, 1));
        personRepo.save(personEntity);
        String inputJson = "{\"firstName\":\"updateFirstName\",\"lastName\":\"updateLastName\",\"birthdate\":\"2020-01-01\"}";

        mockMvc.perform(put("/persons/updateFirstName&updateLastName")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_deletePersonDto() throws Exception {
        PersonEntity personEntity = new PersonEntity();
        personEntity.setFirstName("deleteFirstName");
        personEntity.setLastName("deleteLastName");
        personEntity.setBirthdate(LocalDate.of(2000, 1, 1));
        personRepo.save(personEntity);

        mockMvc.perform(delete("/persons/deleteFirstName&deleteLastName"))
                .andExpect(status().isOk());
    }


}
