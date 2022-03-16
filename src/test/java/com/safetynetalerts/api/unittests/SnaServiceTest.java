package com.safetynetalerts.api.unittests;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.PersonEntity;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.domain.service.SnaService;
import com.safetynetalerts.api.helper.DateHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SnaServiceTest {

    @InjectMocks
    private SnaService snaService;

    @Mock
    private PersonDao personDao;

    @Mock
    private DateHelper dateHelper;

    private static final PersonEntity pe1 = new PersonEntity();
    private static final PersonEntity pe2 = new PersonEntity();
    private static final PersonEntity pe3 = new PersonEntity();
    private static Person p1;
    private static Person p2;
    private static Person p3;
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

        JMapper<Person, PersonEntity> personMapper = new JMapper<>(Person.class, PersonEntity.class);
        p1 = personMapper.getDestination(pe1);
        p1.setAge(18);
        p2 = personMapper.getDestination(pe2);
        p2.setAge(18);
        p3 = personMapper.getDestination(pe3);
        p3.setAge(19);
        personList = Arrays.asList(p1, p2, p3);


    }

    @Test
    public void should_returnPopulatedPersonList_whenGetPersonsByStationOfPopulatedAddress() {
        when(personDao.findAllByFireStation(anyInt())).thenReturn(personEntityList);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(snaService.getPersonsByStation(anyInt())).isEqualTo(personList);
    }

    @Test
    public void should_returnEmptyPersonList_whenGetPersonsByStationOfWrongAddress() {
        when(personDao.findAllByFireStation(anyInt())).thenReturn(personEntityEmptyList);

        assertThat(snaService.getPersonsByStation(anyInt())).isEqualTo(personEmptyList);
    }


    @Test
    public void should_returnPopulatedPersonList_whenGetPersonsByAddressOfPopulatedAddress() {
        when(personDao.findAllByAddress(anyString())).thenReturn(personEntityList);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(snaService.getPersonsByAddress(anyString())).isEqualTo(personList);
    }

    @Test
    public void should_returnEmptyPersonList_whenGetPersonsByAddressOfWrongAddress() {
        when(personDao.findAllByAddress(anyString())).thenReturn(personEntityEmptyList);

        assertThat(snaService.getPersonsByAddress(anyString())).isEqualTo(personEmptyList);
    }

    @Test
    public void should_countChildren_whenGetChildCounter() {
        assertThat(snaService.getChildCounter(personList)).isEqualTo(2);
    }

    @Test
    public void should_countAdults_whenGetAdultCounter() {
        assertThat(snaService.getAdultCounter(personList)).isEqualTo(1);
    }

    @Test
    public void should_returnChildrenList_whenGetChildren() {
        assertThat(snaService.getChildren(personList)).isEqualTo(Arrays.asList(p1, p2));
    }

    @Test
    public void should_returnAdultsList_whenGetAdults() {
        assertThat(snaService.getAdults(personList)).isEqualTo(Collections.singletonList(p3));
    }

    @Test
    public void should_returnFireStation_whenGetFireStationOfPopulatedPersonList() {
        assertThat(snaService.getFireStation(personList)).isEqualTo(1);
    }

    @Test
    public void should_returnZero_whenGetFireStationOfEmptyPersonList() {
        assertThat(snaService.getFireStation(personEmptyList)).isEqualTo(0);
    }

    @Test
    public void should_returnPopulatedPersonList_whenGetPersonsByFirstNameAndLastNameOfExistingPerson() {
        when(personDao.findAllByFirstNameAndLastName(anyString(), anyString())).thenReturn(Collections.singletonList(pe1));
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(snaService.getPersonsByFirstNameAndLastName(anyString(), anyString())).isEqualTo(Collections.singletonList(p1));
    }

}