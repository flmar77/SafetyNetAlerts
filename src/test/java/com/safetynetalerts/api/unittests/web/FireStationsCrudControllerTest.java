package com.safetynetalerts.api.unittests.web;

import com.safetynetalerts.api.domain.model.FireStation;
import com.safetynetalerts.api.domain.service.FireStationService;
import com.safetynetalerts.api.web.controller.FireStationsCrudController;
import com.safetynetalerts.api.web.dto.FireStationsDto;
import lombok.var;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityExistsException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FireStationsCrudController.class)
public class FireStationsCrudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FireStationService fireStationService;

    private static FireStation fs1;
    private static FireStation fs2;
    private static List<FireStation> fireStationList;

    @BeforeAll
    static void setUp() {
        fs1 = new FireStation();
        fs1.setStation(1);
        fs1.setAddresses(Arrays.asList("adr1", "adr2"));
        fs2 = new FireStation();
        fs2.setStation(2);
        fs2.setAddresses(Collections.singletonList("adr3"));
        fireStationList = Arrays.asList(fs1, fs2);
    }

    @Test
    public void should_returnPopulatedFireStationsDto_whenGetAllFireStationsDto() throws Exception {
        when(fireStationService.getAllFireStations()).thenReturn(fireStationList);

        var expectedJson = "[\n" +
                "{\n" +
                "\"station\": 1,\n" +
                "\"address\": \"adr1\"\n" +
                "},\n" +
                "{\n" +
                "\"station\": 1,\n" +
                "\"address\": \"adr2\"\n" +
                "},\n" +
                "{\n" +
                "\"station\": 2,\n" +
                "\"address\": \"adr3\"\n" +
                "}\n" +
                "]";

        mockMvc.perform(get("/firestations"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnUnprocessableEntity_whenGetWrongFireStationsDto() throws Exception {
        mockMvc.perform(get("/firestations/2&"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_returnPopulatedFireStationDto_whenGetFireStationsDto() throws Exception {
        when(fireStationService.getFireStationByStationAndAddress(anyInt(), anyString())).thenReturn(fs2);

        var expectedJson = "{\n" +
                "\"station\": 2,\n" +
                "\"address\": \"adr3\"\n" +
                "}";

        mockMvc.perform(get("/firestations/2&adr3"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnNotFound_whenGetEmptyFireStationsDto() throws Exception {
        doThrow(new NoSuchElementException()).when(fireStationService).getFireStationByStationAndAddress(anyInt(), anyString());

        mockMvc.perform(get("/firestations/2&adr3"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void should_returnUnprocessableEntity_whenPostWrongFireStationsDto() throws Exception {

        var inputJson = "{\n" +
                "\"station\": 2,\n" +
                "\"address\": \"\"\n" +
                "}";

        mockMvc.perform(post("/firestations")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_returnCreated_whenPostRightFireStationsDto() throws Exception {

        var inputJson = "{\n" +
                "\"station\": 2,\n" +
                "\"address\": \"adr3\"\n" +
                "}";

        mockMvc.perform(post("/firestations")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void should_returnUnprocessableEntity_whenPostExistingFireStationsDto() throws Exception {
        doThrow(new EntityExistsException()).when(fireStationService).createFireStationMapping(any(FireStationsDto.class));

        var inputJson = "{\n" +
                "\"station\": 2,\n" +
                "\"address\": \"adr3\"\n" +
                "}";

        mockMvc.perform(post("/firestations")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_returnUnprocessableEntity_whenPutWrongFireStationsDto() throws Exception {

        var inputJson = "{\n" +
                "\"station\": 2,\n" +
                "\"address\": \"\"\n" +
                "}";

        mockMvc.perform(put("/firestations/2&")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_returnOk_whenPutRightFireStationsDto() throws Exception {

        var inputJson = "{\n" +
                "\"station\": 2,\n" +
                "\"address\": \"adr3\"\n" +
                "}";

        mockMvc.perform(put("/firestations/2&adr3")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnUnprocessableEntity_whenPutExistingFireStationsDto() throws Exception {
        doThrow(new EntityExistsException()).when(fireStationService).updateFireStationMapping(any(FireStationsDto.class));

        var inputJson = "{\n" +
                "\"station\": 2,\n" +
                "\"address\": \"adr3\"\n" +
                "}";

        mockMvc.perform(put("/firestations/2&adr3")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_returnNotFound_whenPutFireStationsDtoOfNewAddress() throws Exception {
        doThrow(new NoSuchElementException()).when(fireStationService).updateFireStationMapping(any(FireStationsDto.class));

        var inputJson = "{\n" +
                "\"station\": 2,\n" +
                "\"address\": \"adr3\"\n" +
                "}";

        mockMvc.perform(put("/firestations/2&adr3")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_returnUnprocessableEntity_whenDeleteWrongFireStationsDto() throws Exception {
        mockMvc.perform(delete("/firestations/2&"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_returnOk_whenDeleteRightFireStationsDto() throws Exception {
        mockMvc.perform(delete("/firestations/2&adr3"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnNotFound_whenDeleteNewFireStationsDto() throws Exception {
        doThrow(new NoSuchElementException()).when(fireStationService).deleteFireStationMapping(anyInt(), anyString());

        mockMvc.perform(delete("/firestations/2&adr3"))
                .andExpect(status().isNotFound());
    }

}
