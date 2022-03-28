package com.safetynetalerts.api.unittests.domain;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.dao.entity.PersonEntity;
import com.safetynetalerts.api.dao.repository.PersonRepo;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.domain.service.PersonService;
import com.safetynetalerts.api.helper.DateHelper;
import com.safetynetalerts.api.web.dto.MedicalRecordsDto;
import com.safetynetalerts.api.web.dto.PersonDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepo personRepo;

    @Mock
    private DateHelper dateHelper;

    private static final PersonEntity pe1 = new PersonEntity();
    private static final PersonEntity pe2 = new PersonEntity();
    private static final PersonEntity pe3 = new PersonEntity();
    private static Person p1;
    private static Person p2;
    private static Person p3;
    private static PersonDto pd1;
    private static List<PersonEntity> personEntityList;
    private static List<Person> personList;
    private static final List<PersonEntity> personEntityEmptyList = new ArrayList<>();
    private static final List<Person> personEmptyList = new ArrayList<>();

    @BeforeAll
    static void setUp() {
        pe1.setFirstName("p1FirstName");
        pe1.setLastName("p1LastName");
        pe1.setAddress("p1Address");
        pe1.setPhone("p1Phone");
        pe1.setBirthdate(LocalDate.of(2002, 1, 1));
        pe1.setMedications(Arrays.asList("p1Med1:1mg", "p1Med2:2mg"));
        pe1.setAllergies(Arrays.asList("p1All1", "p1All2"));
        pe1.setFireStation(1);
        pe2.setFirstName("p2FirstName");
        pe2.setLastName("p2LastName");
        pe2.setAddress("p2Address");
        pe2.setPhone("p2Phone");
        pe2.setBirthdate(LocalDate.of(2002, 1, 2));
        pe2.setMedications(Arrays.asList("p2Med1:1mg", "p2Med2:2mg"));
        pe2.setAllergies(Arrays.asList("p2All1", "p2All2"));
        pe2.setFireStation(1);
        pe3.setFirstName("p3FirstName");
        pe3.setLastName("p3LastName");
        pe3.setAddress("p3Address");
        pe3.setPhone("p2Phone");
        pe3.setBirthdate(LocalDate.of(2001, 1, 1));
        pe3.setMedications(Arrays.asList("p3Med1:1mg", "p3Med2:2mg"));
        pe3.setAllergies(Arrays.asList("p3All1", "p3All2"));
        pe3.setFireStation(1);
        personEntityList = Arrays.asList(pe1, pe2, pe3);

        JMapper<Person, PersonEntity> personEntityToPersonMapper = new JMapper<>(Person.class, PersonEntity.class);
        p1 = personEntityToPersonMapper.getDestination(pe1);
        p1.setAge(18);
        p2 = personEntityToPersonMapper.getDestination(pe2);
        p2.setAge(18);
        p3 = personEntityToPersonMapper.getDestination(pe3);
        p3.setAge(19);
        personList = Arrays.asList(p1, p2, p3);

        JMapper<PersonDto, PersonEntity> personEntityToPersonDtoMapper = new JMapper<>(PersonDto.class, PersonEntity.class);
        pd1 = personEntityToPersonDtoMapper.getDestination(pe1);

    }

    @Test
    public void should_returnPopulatedPersonList_whenGetPersonsByPopulatedStation() {
        when(personRepo.findAllByFireStation(anyInt())).thenReturn(personEntityList);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(personService.getPersonsByStation(anyInt())).isEqualTo(personList);
    }

    @Test
    public void should_returnEmptyPersonList_whenGetPersonsByWrongStation() {
        when(personRepo.findAllByFireStation(anyInt())).thenReturn(personEntityEmptyList);

        assertThat(personService.getPersonsByStation(anyInt())).isEqualTo(personEmptyList);
    }

    @Test
    public void should_returnPopulatedPersonList_whenGetPersonsByPopulatedStations() {
        when(personRepo.findAllByFireStationIn(anyList())).thenReturn(personEntityList);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(personService.getPersonsByStations(anyList())).isEqualTo(personList);
    }


    @Test
    public void should_returnPopulatedPersonList_whenGetPersonsByPopulatedAddress() {
        when(personRepo.findAllByAddress(anyString())).thenReturn(personEntityList);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(personService.getPersonsByAddress(anyString())).isEqualTo(personList);
    }

    @Test
    public void should_returnEmptyPersonList_whenGetPersonsByWrongAddress() {
        when(personRepo.findAllByAddress(anyString())).thenReturn(personEntityEmptyList);

        assertThat(personService.getPersonsByAddress(anyString())).isEqualTo(personEmptyList);
    }

    @Test
    public void should_countChildren_whenGetChildCounter() {
        assertThat(personService.getChildCounter(personList)).isEqualTo(2);
    }

    @Test
    public void should_countAdults_whenGetAdultCounter() {
        assertThat(personService.getAdultCounter(personList)).isEqualTo(1);
    }

    @Test
    public void should_returnChildrenList_whenGetChildren() {
        assertThat(personService.getChildren(personList)).isEqualTo(Arrays.asList(p1, p2));
    }

    @Test
    public void should_returnAdultsList_whenGetAdults() {
        assertThat(personService.getAdults(personList)).isEqualTo(Collections.singletonList(p3));
    }

    @Test
    public void should_returnPopulatedPersonList_whenGetPersonsByFirstNameAndLastNameOfExistingPerson() {
        when(personRepo.findAllByFirstNameAndLastName(anyString(), anyString())).thenReturn(Collections.singletonList(pe1));
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(personService.getPersonsByFirstNameAndLastName(anyString(), anyString())).isEqualTo(Collections.singletonList(p1));
    }

    @Test
    public void should_returnPopulatedPersonList_whenGetPersonsByCityOfPopulatedCity() {
        when(personRepo.findAllByCity(anyString())).thenReturn(personEntityList);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(personService.getPersonsByCity(anyString())).isEqualTo(personList);
    }

    @Test
    public void should_returnPopulatedPersonList_whenGetAllPersons() {
        when(personRepo.findAll()).thenReturn(personEntityList);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(personService.getAllPersons()).isEqualTo(personList);
    }

    @Test
    public void should_returnValidPerson_whenCreateNewPerson() {
        when(personRepo.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.empty());
        when(personRepo.save(any())).thenReturn(pe1);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(personService.createPerson(pd1)).isEqualTo(p1);
    }

    @Test
    public void should_throwEntityExistsException_whenCreateExistingPerson() {
        when(personRepo.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.of(pe1));

        assertThrows(EntityExistsException.class, () -> personService.createPerson(pd1));
    }

    @Test
    public void should_returnValidPerson_whenUpdateExistingPerson() {
        when(personRepo.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.of(pe1));
        when(personRepo.save(any())).thenReturn(pe1);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(personService.updatePersonWithoutMedicalRecords(pd1.getFirstName(), pd1.getLastName(), pd1)).isEqualTo(p1);
    }

    @Test
    public void should_throwNoSuchElementException_whenUpdateNonExistingPerson() {
        when(personRepo.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> personService.updatePersonWithoutMedicalRecords(anyString(), anyString(), pd1));
    }

    @Test
    public void should_delete_whenDeleteExistingPerson() {
        when(personRepo.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.of(pe1));

        personService.deletePerson(anyString(), anyString());

        verify(personRepo, times(1)).delete(pe1);
    }

    @Test
    public void should_throwNoSuchElementException_whenDeleteNonExistingPerson() {
        when(personRepo.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> personService.deletePerson(anyString(), anyString()));
    }

    @Test
    public void should_updatePersonMedicalRecords_whenUpdateExistingPersonMedicalRecords() {
        PersonEntity peTestMedical = new PersonEntity();
        peTestMedical.setFirstName("peFirstName");
        peTestMedical.setLastName("peLastName");
        peTestMedical.setAddress("p1Address");
        peTestMedical.setPhone("p1Phone");
        peTestMedical.setBirthdate(LocalDate.of(2002, 1, 1));
        peTestMedical.setMedications(Arrays.asList("p1Med1:1mg", "p1Med2:2mg"));
        peTestMedical.setAllergies(Arrays.asList("p1All1", "p1All2"));
        peTestMedical.setFireStation(1);
        MedicalRecordsDto mrTestMedical = new MedicalRecordsDto();
        mrTestMedical.setFirstName("peFirstName");
        mrTestMedical.setLastName("peLastName");
        mrTestMedical.setMedications(Arrays.asList("a", "b"));
        mrTestMedical.setAllergies(Collections.singletonList("c"));

        when(personRepo.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.of(peTestMedical));
        when(personRepo.save(any(PersonEntity.class))).thenReturn(peTestMedical);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        personService.updatePersonMedicalRecords("peFirstName", "peLastName", mrTestMedical);

        verify(personRepo, times(1)).save(argThat(it -> {
            assertThat(it.getMedications().get(0)).isEqualTo("a");
            return true;
        }));
    }

    @Test
    public void should_updatePersonWithEmptyMedicalRecords_whenDeleteExistingPersonMedicalRecords() {
        PersonEntity peTestMedical = new PersonEntity();
        peTestMedical.setFirstName("peFirstName");
        peTestMedical.setLastName("peLastName");
        peTestMedical.setAddress("p1Address");
        peTestMedical.setPhone("p1Phone");
        peTestMedical.setBirthdate(LocalDate.of(2002, 1, 1));
        peTestMedical.setMedications(Arrays.asList("p1Med1:1mg", "p1Med2:2mg"));
        peTestMedical.setAllergies(Arrays.asList("p1All1", "p1All2"));
        peTestMedical.setFireStation(1);

        when(personRepo.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.of(peTestMedical));
        when(personRepo.save(any(PersonEntity.class))).thenReturn(peTestMedical);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        personService.deletePersonMedicalRecords("peFirstName", "peLastName");

        verify(personRepo, times(1)).save(argThat(it -> {
            assertThat(it.getMedications()).isEqualTo(Collections.emptyList());
            return true;
        }));
    }

    @Test
    public void should_SaveAll_whenSaveAllPersonEntities() {
        personService.saveAllPersonEntities(anyList());

        verify(personRepo, times(1)).saveAll(anyList());
    }

    @Test
    public void should_SaveAll_whenSetNewFireStation() {
        when(personRepo.findAllByAddress(anyString())).thenReturn(personEntityList);

        personService.setNewFireStationOnPersonsByAddress(1, anyString());

        verify(personRepo, times(1)).saveAll(anyList());
    }

    @Test
    public void should_returnFireStation_whenGetFireStationOfPopulatedPersonList() {
        assertThat(personService.getFireStation(personList)).isEqualTo(1);
    }

    @Test
    public void should_returnZero_whenGetFireStationOfEmptyPersonList() {
        assertThat(personService.getFireStation(personEmptyList)).isEqualTo(0);
    }

}