package com.safetynetalerts.api.unittests.web.controller;

import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.domain.service.SnaService;
import com.safetynetalerts.api.web.controller.MedicalRecordsCrudController;
import com.safetynetalerts.api.web.dto.MedicalRecordsDto;
import lombok.var;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MedicalRecordsCrudController.class)
public class MedicalRecordsCrudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SnaService snaService;

    private static final Person p1 = new Person();
    private static final Person p2 = new Person();
    private static List<Person> personList;

    @BeforeAll
    static void setUp() {
        p1.setFirstName("p1FirstName");
        p1.setLastName("p1LastName");
        p1.setAddress("p1Address");
        p1.setCity("p1City");
        p1.setZip("00001");
        p1.setPhone("p1Phone");
        p1.setEmail("p1@email.com");
        p1.setBirthdate(LocalDate.of(2002, 1, 1));
        p1.setMedications(Arrays.asList("p1Med1:1mg", "p1Med2:2mg"));
        p1.setAllergies(Arrays.asList("p1All1", "p1All2"));
        p1.setFireStation(1);
        p1.setAge(18);
        p2.setFirstName("p2FirstName");
        p2.setLastName("p2LastName");
        p2.setAddress("p2Address");
        p2.setCity("p2City");
        p2.setZip("00002");
        p2.setPhone("p1Phone");
        p2.setEmail("p2@email.com");
        p2.setBirthdate(LocalDate.of(2001, 1, 2));
        p2.setMedications(Arrays.asList("p2Med1:1mg", "p2Med2:2mg"));
        p2.setAllergies(Arrays.asList("p2All1", "p2All2"));
        p2.setFireStation(1);
        p2.setAge(19);
        personList = Arrays.asList(p1, p2);
    }

    @Test
    public void should_returnPopulatedMedicalRecordsDto_whenGetMedicalRecords() throws Exception {
        when(snaService.getAllPersons()).thenReturn(personList);

        var expectedJson = "[\n" +
                "{\n" +
                "\"firstName\": \"p1FirstName\",\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"medications\": [\n" +
                "\"p1Med1:1mg\",\n" +
                "\"p1Med2:2mg\"\n" +
                "],\n" +
                "\"allergies\": [\n" +
                "\"p1All1\",\n" +
                "\"p1All2\"\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"firstName\": \"p2FirstName\",\n" +
                "\"lastName\": \"p2LastName\",\n" +
                "\"medications\": [\n" +
                "\"p2Med1:1mg\",\n" +
                "\"p2Med2:2mg\"\n" +
                "],\n" +
                "\"allergies\": [\n" +
                "\"p2All1\",\n" +
                "\"p2All2\"\n" +
                "]\n" +
                "}\n" +
                "]";

        mockMvc.perform(get("/medicalRecords"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnPopulatedMedicalRecordDto_whenGetMedicalRecord() throws Exception {
        when(snaService.getPersonsByFirstNameAndLastName(anyString(), anyString())).thenReturn(Collections.singletonList(p1));

        var expectedJson = "[\n" +
                "{\n" +
                "\"firstName\": \"p1FirstName\",\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"medications\": [\n" +
                "\"p1Med1:1mg\",\n" +
                "\"p1Med2:2mg\"\n" +
                "],\n" +
                "\"allergies\": [\n" +
                "\"p1All1\",\n" +
                "\"p1All2\"\n" +
                "]\n" +
                "}\n" +
                "]";

        mockMvc.perform(get("/medicalRecords/x&y"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnUnprocessableEntity_whenPutWrongMedicalRecordsDto() throws Exception {

        var inputJson = "{\n" +
                "\"firstName\": \"p1FirstName\",\n" +
                "\"lastName\": \"\",\n" +
                "\"medications\": [\n" +
                "\"p1Med1:1mg\",\n" +
                "\"p1Med2:2mg\"\n" +
                "],\n" +
                "\"allergies\": [\n" +
                "\"p1All1\",\n" +
                "\"p1All2\"\n" +
                "]\n" +
                "}";

        mockMvc.perform(put("/medicalRecords/x&")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_returnOk_whenPutRightMedicalRecordsDto() throws Exception {
        when(snaService.updatePersonMedicalRecords(anyString(), anyString(), any(MedicalRecordsDto.class))).thenReturn(p1);

        var inputJson = "{\n" +
                "\"firstName\": \"p1FirstName\",\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"medications\": [\n" +
                "\"p1Med1:1mg\",\n" +
                "\"p1Med2:2mg\"\n" +
                "],\n" +
                "\"allergies\": [\n" +
                "\"p1All1\",\n" +
                "\"p1All2\"\n" +
                "]\n" +
                "}";

        mockMvc.perform(put("/medicalRecords/x&y")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnNotFound_whenPutNewMedicalRecordsDto() throws Exception {
        doThrow(new NoSuchElementException()).when(snaService).updatePersonMedicalRecords(anyString(), anyString(), any(MedicalRecordsDto.class));

        var inputJson = "{\n" +
                "\"firstName\": \"p1FirstName\",\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"medications\": [\n" +
                "\"p1Med1:1mg\",\n" +
                "\"p1Med2:2mg\"\n" +
                "],\n" +
                "\"allergies\": [\n" +
                "\"p1All1\",\n" +
                "\"p1All2\"\n" +
                "]\n" +
                "}";

        mockMvc.perform(put("/medicalRecords/x&y")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_returnUnprocessableEntity_whenDeleteWrongMedicalRecordsDto() throws Exception {
        mockMvc.perform(delete("/medicalRecords/x&"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_returnOk_whenDeleteRightMedicalRecordsDto() throws Exception {
        mockMvc.perform(delete("/medicalRecords/x&y"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnNotFound_whenDeleteNewMedicalRecordsDto() throws Exception {
        doThrow(new NoSuchElementException()).when(snaService).deletePersonMedicalRecords(anyString(), anyString());

        mockMvc.perform(delete("/medicalRecords/x&y"))
                .andExpect(status().isNotFound());
    }
}
