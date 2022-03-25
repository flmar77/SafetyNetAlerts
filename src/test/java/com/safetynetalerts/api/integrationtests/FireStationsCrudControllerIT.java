package com.safetynetalerts.api.integrationtests;

import com.google.gson.Gson;
import com.safetynetalerts.api.data.entity.FireStationEntity;
import com.safetynetalerts.api.data.repository.FireStationRepo;
import com.safetynetalerts.api.web.dto.FireStationsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FireStationsCrudControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FireStationRepo fireStationRepo;

    @Test
    public void should_getAllFireStationsDto() throws Exception {
        mockMvc.perform(get("/firestations"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_getFireStationsDto() throws Exception {
        FireStationEntity fireStationEntity = new FireStationEntity();
        fireStationEntity.setStation(1000);
        fireStationEntity.setAddresses(Collections.singletonList("address1000"));
        fireStationRepo.save(fireStationEntity);

        mockMvc.perform(get("/firestations/1000&address1000"))
                .andExpect(status().isOk());

    }

    @Test
    public void should_createFireStationsDto() throws Exception {
        FireStationsDto fireStationsDto = new FireStationsDto();
        fireStationsDto.setStation(1001);
        fireStationsDto.setAddress("address1001");
        String inputJson = new Gson().toJson(fireStationsDto);

        mockMvc.perform(post("/firestations")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void should_updateFireStationsDto() throws Exception {
        FireStationEntity fireStationEntity = new FireStationEntity();
        fireStationEntity.setStation(1002);
        fireStationEntity.setAddresses(Collections.singletonList("address1002"));
        fireStationRepo.save(fireStationEntity);

        FireStationsDto fireStationsDto = new FireStationsDto();
        fireStationsDto.setStation(1003);
        fireStationsDto.setAddress("address1002");
        String inputJson = new Gson().toJson(fireStationsDto);

        mockMvc.perform(put("/firestations/1003&address1002")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_deleteFireStationsDto() throws Exception {
        FireStationEntity fireStationEntity = new FireStationEntity();
        fireStationEntity.setStation(1004);
        fireStationEntity.setAddresses(Collections.singletonList("address1004"));
        fireStationRepo.save(fireStationEntity);

        mockMvc.perform(delete("/firestations/1004&address1004"))
                .andExpect(status().isOk());
    }

}
