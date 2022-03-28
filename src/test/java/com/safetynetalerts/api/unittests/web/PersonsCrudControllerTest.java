package com.safetynetalerts.api.unittests.web;

import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.domain.service.PersonService;
import com.safetynetalerts.api.web.controller.PersonsCrudController;
import lombok.var;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityExistsException;
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

@WebMvcTest(controllers = PersonsCrudController.class)
public class PersonsCrudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

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
    public void should_returnPopulatedPersonsDto_whenGetPersons() throws Exception {
        when(personService.getAllPersons()).thenReturn(personList);

        var expectedJson = "[\n" +
                "{\n" +
                "\"firstName\": \"p1FirstName\",\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"address\": \"p1Address\",\n" +
                "\"city\": \"p1City\",\n" +
                "\"zip\": \"00001\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"email\": \"p1@email.com\",\n" +
                "\"birthdate\": \"2002-01-01\"\n" +
                "},\n" +
                "{\n" +
                "\"firstName\": \"p2FirstName\",\n" +
                "\"lastName\": \"p2LastName\",\n" +
                "\"address\": \"p2Address\",\n" +
                "\"city\": \"p2City\",\n" +
                "\"zip\": \"00002\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"email\": \"p2@email.com\",\n" +
                "\"birthdate\": \"2001-01-02\"\n" +
                "}\n" +
                "]";

        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnPopulatedPersonDto_whenGetPersonsByFirstNameAndLastName() throws Exception {
        when(personService.getPersonsByFirstNameAndLastName(anyString(), anyString())).thenReturn(Collections.singletonList(p1));

        var expectedJson = "[\n" +
                "{\n" +
                "\"firstName\": \"p1FirstName\",\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"address\": \"p1Address\",\n" +
                "\"city\": \"p1City\",\n" +
                "\"zip\": \"00001\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"email\": \"p1@email.com\",\n" +
                "\"birthdate\": \"2002-01-01\"\n" +
                "}\n" +
                "]";

        mockMvc.perform(get("/persons/x&y"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnPopulatedPersonDto_whenPostNewPersonDto() throws Exception {
        when(personService.createPerson(any())).thenReturn(p1);

        var inputJson = "{\n" +
                "\"firstName\": \"p1FirstName\",\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"address\": \"p1Address\",\n" +
                "\"city\": \"p1City\",\n" +
                "\"zip\": \"00001\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"email\": \"p1@email.com\",\n" +
                "\"birthdate\": \"2002-01-01\"\n" +
                "}\n";

        mockMvc.perform(post("/persons")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(inputJson));
    }

    @Test
    public void should_returnUnprocessableEntity_whenPostWrongPerson() throws Exception {

        var inputJson = "{\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"address\": \"p1Address\",\n" +
                "\"city\": \"p1City\",\n" +
                "\"zip\": \"00001\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"email\": \"p1@email.com\",\n" +
                "\"birthdate\": \"2002-01-01\"\n" +
                "}\n";

        mockMvc.perform(post("/persons")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_returnUnprocessableEntity_whenPostExistingPerson() throws Exception {
        when(personService.createPerson(any())).thenThrow(EntityExistsException.class);

        var inputJson = "{\n" +
                "\"firstName\": \"p1FirstName\",\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"address\": \"p1Address\",\n" +
                "\"city\": \"p1City\",\n" +
                "\"zip\": \"00001\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"email\": \"p1@email.com\",\n" +
                "\"birthdate\": \"2002-01-01\"\n" +
                "}\n";

        mockMvc.perform(post("/persons")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_returnPopulatedPersonDto_whenPutExistingPersonDto() throws Exception {
        when(personService.updatePersonWithoutMedicalRecords(anyString(), anyString(), any())).thenReturn(p1);

        var inputJson = "{\n" +
                "\"firstName\": \"p1FirstName\",\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"address\": \"p1Address\",\n" +
                "\"city\": \"p1City\",\n" +
                "\"zip\": \"00001\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"email\": \"p1@email.com\",\n" +
                "\"birthdate\": \"2002-01-01\"\n" +
                "}\n";

        mockMvc.perform(put("/persons/p1FirstName&p1LastName")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(inputJson));
    }

    @Test
    public void should_returnUnprocessableEntity_whenPutWrongPersonDto() throws Exception {

        var inputJson = "{\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"address\": \"p1Address\",\n" +
                "\"city\": \"p1City\",\n" +
                "\"zip\": \"00001\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"email\": \"p1@email.com\",\n" +
                "\"birthdate\": \"2002-01-01\"\n" +
                "}\n";

        mockMvc.perform(put("/persons/&y")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_returnNotFound_whenPutNewPersonDto() throws Exception {
        when(personService.updatePersonWithoutMedicalRecords(anyString(), anyString(), any())).thenThrow(NoSuchElementException.class);

        var inputJson = "{\n" +
                "\"firstName\": \"p1FirstName\",\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"address\": \"p1Address\",\n" +
                "\"city\": \"p1City\",\n" +
                "\"zip\": \"00001\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"email\": \"p1@email.com\",\n" +
                "\"birthdate\": \"2002-01-01\"\n" +
                "}\n";

        mockMvc.perform(put("/persons/p1FirstName&p1LastName")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_returnSuccess_whenDeleteExistingPerson() throws Exception {
        mockMvc.perform(delete("/persons/x&y"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnUnprocessableEntity_whenDeleteWrongPerson() throws Exception {
        mockMvc.perform(delete("/persons/&y"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_returnNotFound_whenDeleteNewPerson() throws Exception {
        doThrow(new NoSuchElementException()).when(personService).deletePerson(anyString(), anyString());

        mockMvc.perform(delete("/persons/x&y"))
                .andExpect(status().isNotFound());
    }

}
