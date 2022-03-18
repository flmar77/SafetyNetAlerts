package com.safetynetalerts.api.unittests;

import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.domain.service.SnaService;
import com.safetynetalerts.api.web.controller.SnaController;
import lombok.var;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SnaController.class)
public class SnaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SnaService snaService;

    private static final Person p1 = new Person();
    private static final Person p2 = new Person();
    private static List<Person> personList;
    private static final List<Person> personEmptyList = new ArrayList<>();

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
    public void should_returnPopulatedFireStationDto_whenGetFireStationDtoOfPopulatedStation() throws Exception {
        when(snaService.getPersonsByStation(anyInt())).thenReturn(personList);
        when(snaService.getChildCounter(any())).thenReturn(Long.valueOf(1));
        when(snaService.getAdultCounter(any())).thenReturn(Long.valueOf(1));

        var expectedJson = "{\n" +
                "\"fireStationPersons\": [\n" +
                "{\n" +
                "\"firstName\": \"p1FirstName\",\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"address\": \"p1Address\",\n" +
                "\"phone\": \"p1Phone\"\n" +
                "},\n" +
                "{\n" +
                "\"firstName\": \"p2FirstName\",\n" +
                "\"lastName\": \"p2LastName\",\n" +
                "\"address\": \"p2Address\",\n" +
                "\"phone\": \"p1Phone\"\n" +
                "}\n" +
                "],\n" +
                "\"adultCounter\": 1,\n" +
                "\"childCounter\": 1\n" +
                "}";

        mockMvc.perform(get("/firestation?stationNumber=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnEmptyFireStationDto_whenGetFireStationDtoOfEmptyStation() throws Exception {
        when(snaService.getPersonsByStation(anyInt())).thenReturn(personEmptyList);
        when(snaService.getChildCounter(any())).thenReturn(Long.valueOf(0));
        when(snaService.getAdultCounter(any())).thenReturn(Long.valueOf(0));

        var expectedJson = "{\n" +
                "\"fireStationPersons\": [],\n" +
                "\"adultCounter\": 0,\n" +
                "\"childCounter\": 0\n" +
                "}";

        mockMvc.perform(get("/firestation?stationNumber=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnPopulatedChildAlertDto_whenGetChildAlertDtoOfPopulatedAddress() throws Exception {
        when(snaService.getPersonsByAddress(anyString())).thenReturn(personList);
        when(snaService.getChildren(any())).thenReturn(Collections.singletonList(p1));
        when(snaService.getAdults(any())).thenReturn(Collections.singletonList(p2));

        var expectedJson = "{\n" +
                "\"alertedChildren\": [\n" +
                "{\n" +
                "\"firstName\": \"p1FirstName\",\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"age\": 18\n" +
                "}\n" +
                "],\n" +
                "\"alertedAdults\": [\n" +
                "{\n" +
                "\"firstName\": \"p2FirstName\",\n" +
                "\"lastName\": \"p2LastName\",\n" +
                "\"age\": 19\n" +
                "}\n" +
                "]\n" +
                "}";

        mockMvc.perform(get("/childAlert?address=abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnEmptyChildAlertDto_whenGetChildAlertDtoOfEmptyAddress() throws Exception {
        when(snaService.getPersonsByAddress(anyString())).thenReturn(personEmptyList);
        when(snaService.getChildren(any())).thenReturn(personEmptyList);
        when(snaService.getAdults(any())).thenReturn(personEmptyList);

        var expectedJson = "{\n" +
                "\"alertedChildren\": [],\n" +
                "\"alertedAdults\": []\n" +
                "}";

        mockMvc.perform(get("/childAlert?address=abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnPopulatedPhoneAlertDto_whenGetPhoneAlertDtoOfPopulatedStation() throws Exception {
        when(snaService.getPersonsByStation(anyInt())).thenReturn(personList);

        var expectedJson = "{\n" +
                "\"phones\": [\n" +
                "\"p1Phone\"\n" +
                "]\n" +
                "}";

        mockMvc.perform(get("/phoneAlert?firestation_number=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnEmptyPhoneAlertDto_whenGetPhoneAlertDtoOfEmptyStation() throws Exception {
        when(snaService.getPersonsByStation(anyInt())).thenReturn(personEmptyList);

        var expectedJson = "{\n" +
                "\"phones\": []\n" +
                "}";

        mockMvc.perform(get("/phoneAlert?firestation_number=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnPopulatedFireDto_whenGetFireDtoOfPopulatedAddress() throws Exception {
        when(snaService.getPersonsByAddress(anyString())).thenReturn(personList);
        when(snaService.getFireStation(any())).thenReturn(1);

        var expectedJson = "{\n" +
                "\"firePersons\": [\n" +
                "{\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"age\": 18,\n" +
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
                "\"lastName\": \"p2LastName\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"age\": 19,\n" +
                "\"medications\": [\n" +
                "\"p2Med1:1mg\",\n" +
                "\"p2Med2:2mg\"\n" +
                "],\n" +
                "\"allergies\": [\n" +
                "\"p2All1\",\n" +
                "\"p2All2\"\n" +
                "]\n" +
                "}\n" +
                "],\n" +
                "\"fireStation\": 1\n" +
                "}";

        mockMvc.perform(get("/fire?address=abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnEmptyFireDto_whenGetFireDtoOfEmptyAddress() throws Exception {
        when(snaService.getPersonsByAddress(anyString())).thenReturn(personEmptyList);
        when(snaService.getFireStation(any())).thenReturn(0);

        var expectedJson = "{\n" +
                "\"firePersons\": [],\n" +
                "\"fireStation\": 0\n" +
                "}";

        mockMvc.perform(get("/fire?address=abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnPopulatedStationsDto_whenGetStationsDtoOfPopulatedStation() throws Exception {
        when(snaService.getPersonsByStation(anyInt())).thenReturn(personList);

        var expectedJson = "{\n" +
                "\"personsByAddress\": [\n" +
                "{\n" +
                "\"address\": \"p2Address\",\n" +
                "\"stationsPersons\": [\n" +
                "{\n" +
                "\"lastName\": \"p2LastName\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"age\": 19,\n" +
                "\"medications\": [\n" +
                "\"p2Med1:1mg\",\n" +
                "\"p2Med2:2mg\"\n" +
                "],\n" +
                "\"allergies\": [\n" +
                "\"p2All1\",\n" +
                "\"p2All2\"\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"address\": \"p1Address\",\n" +
                "\"stationsPersons\": [\n" +
                "{\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"age\": 18,\n" +
                "\"medications\": [\n" +
                "\"p1Med1:1mg\",\n" +
                "\"p1Med2:2mg\"\n" +
                "],\n" +
                "\"allergies\": [\n" +
                "\"p1All1\",\n" +
                "\"p1All2\"\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "}";

        mockMvc.perform(get("/stations?stationNumber=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnEmptyStationsDto_whenGetStationsDtoOfEmptyStation() throws Exception {
        when(snaService.getPersonsByStation(anyInt())).thenReturn(personEmptyList);

        var expectedJson = "{\n" +
                "\"personsByAddress\": []\n" +
                "}";

        mockMvc.perform(get("/stations?stationNumber=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnPopulatedPersonInfoDto_whenGetPersonInfoDtoOfExistingPerson() throws Exception {
        when(snaService.getPersonsByFirstNameAndLastName(anyString(), anyString())).thenReturn(Collections.singletonList(p1));

        var expectedJson = "{\n" +
                "\"personsInfo\": [\n" +
                "{\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"address\": \"p1Address\",\n" +
                "\"age\": 18,\n" +
                "\"email\": \"p1@email.com\",\n" +
                "\"medications\": [\n" +
                "\"p1Med1:1mg\",\n" +
                "\"p1Med2:2mg\"\n" +
                "],\n" +
                "\"allergies\": [\n" +
                "\"p1All1\",\n" +
                "\"p1All2\"\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "}";

        mockMvc.perform(get("/personInfo?firstName=a&lastName=b"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnPopulatedPersonsInfoDto_whenGetPersonInfoDtoOfNamesakesPersons() throws Exception {
        Person p3 = new Person();
        p3.setFirstName("p1FirstName");
        p3.setLastName("p1LastName");
        p3.setAddress("p3Address");
        p3.setMedications(Arrays.asList("p3Med1:1mg", "p3Med2:2mg"));
        p3.setAllergies(Arrays.asList("p3All1", "p3All2"));
        p3.setAge(30);
        p3.setEmail("p3@email.com");

        when(snaService.getPersonsByFirstNameAndLastName(anyString(), anyString())).thenReturn(Arrays.asList(p1, p3));

        var expectedJson = "{\n" +
                "\"personsInfo\": [\n" +
                "{\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"address\": \"p1Address\",\n" +
                "\"age\": 18,\n" +
                "\"email\": \"p1@email.com\",\n" +
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
                "\"lastName\": \"p1LastName\",\n" +
                "\"address\": \"p3Address\",\n" +
                "\"age\": 30,\n" +
                "\"email\": \"p3@email.com\",\n" +
                "\"medications\": [\n" +
                "\"p3Med1:1mg\",\n" +
                "\"p3Med2:2mg\"\n" +
                "],\n" +
                "\"allergies\": [\n" +
                "\"p3All1\",\n" +
                "\"p3All2\"\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "}";

        mockMvc.perform(get("/personInfo?firstName=a&lastName=b"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnEmptyPersonInfoDto_whenGetPersonInfoDtoOfNonExistingPerson() throws Exception {
        when(snaService.getPersonsByFirstNameAndLastName(anyString(), anyString())).thenReturn(personEmptyList);

        var expectedJson = "{\n" +
                "\"personsInfo\": []\n" +
                "}";

        mockMvc.perform(get("/personInfo?firstName=a&lastName=b"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnPopulatedCommunityEmailDto_whenGetCommunityEmailDtoOfPopulatedCity() throws Exception {
        when(snaService.getPersonsByCity(anyString())).thenReturn(personList);

        var expectedJson = "{\n" +
                "\"emails\": [\n" +
                "\"p1@email.com\",\n" +
                "\"p2@email.com\"\n" +
                "]\n" +
                "}";

        mockMvc.perform(get("/communityEmail?city=x"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnEmptyCommunityEmailDto_whenGetCommunityEmailDtoOfEmptyCity() throws Exception {
        when(snaService.getPersonsByCity(anyString())).thenReturn(personEmptyList);

        var expectedJson = "{\n" +
                "\"emails\": []\n" +
                "}";

        mockMvc.perform(get("/communityEmail?city=x"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void should_returnPopulatedPersonsDto_whenGetPersons() throws Exception {
        when(snaService.getAllPersons()).thenReturn(personList);

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
        when(snaService.getPersonsByFirstNameAndLastName(anyString(), anyString())).thenReturn(Collections.singletonList(p1));

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
        when(snaService.personAlreadyExists(anyString(), anyString())).thenReturn(false);
        when(snaService.savePerson(any())).thenReturn(p1);

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
    public void should_returnUnprocessableEntity_whenPostExistingPersonDto() throws Exception {
        when(snaService.personAlreadyExists(anyString(), anyString())).thenReturn(true);

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
        when(snaService.updatePerson(anyString(), anyString(), any())).thenReturn(p1);

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

        mockMvc.perform(put("/persons/x&y")
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
        when(snaService.updatePerson(anyString(), anyString(), any())).thenThrow(NoSuchElementException.class);

        var inputJson = "{\n" +
                "\"lastName\": \"p1LastName\",\n" +
                "\"address\": \"p1Address\",\n" +
                "\"city\": \"p1City\",\n" +
                "\"zip\": \"00001\",\n" +
                "\"phone\": \"p1Phone\",\n" +
                "\"email\": \"p1@email.com\",\n" +
                "\"birthdate\": \"2002-01-01\"\n" +
                "}\n";

        mockMvc.perform(put("/persons/x&y")
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
        doThrow(new NoSuchElementException()).when(snaService).deletePerson(anyString(), anyString());

        mockMvc.perform(delete("/persons/x&y"))
                .andExpect(status().isNotFound());
    }

}
