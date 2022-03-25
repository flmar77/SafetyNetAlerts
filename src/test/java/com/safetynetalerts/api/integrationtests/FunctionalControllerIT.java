package com.safetynetalerts.api.integrationtests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FunctionalControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_getFireStationDto() throws Exception {
        mockMvc.perform(get("/firestation?stationNumber=0"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_getChildAlertDto() throws Exception {
        mockMvc.perform(get("/childAlert?address=abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_getPhoneAlertDto() throws Exception {
        mockMvc.perform(get("/phoneAlert?firestation_number=0"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_getFireDto() throws Exception {
        mockMvc.perform(get("/fire?address=abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_getStationsDto() throws Exception {
        mockMvc.perform(get("/flood/stations?stationNumbers=1,2"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_getPersonInfoDto() throws Exception {
        mockMvc.perform(get("/personInfo?firstName=abc&lastName=abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_getCommunityEmailDto() throws Exception {
        mockMvc.perform(get("/communityEmail?city=abc"))
                .andExpect(status().isOk());
    }

}
