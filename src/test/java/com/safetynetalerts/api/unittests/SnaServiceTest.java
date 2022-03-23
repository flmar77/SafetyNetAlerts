package com.safetynetalerts.api.unittests;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.data.dao.FireStationDao;
import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.FireStationEntity;
import com.safetynetalerts.api.data.entity.PersonEntity;
import com.safetynetalerts.api.domain.model.FireStation;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.domain.service.SnaService;
import com.safetynetalerts.api.helper.DateHelper;
import com.safetynetalerts.api.web.dto.FireStationsDto;
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
public class SnaServiceTest {

    @InjectMocks
    private SnaService snaService;

    @Mock
    private PersonDao personDao;

    @Mock
    private FireStationDao fireStationDao;

    @Mock
    private DateHelper dateHelper;

    private static final PersonEntity pe1 = new PersonEntity();
    private static final PersonEntity pe2 = new PersonEntity();
    private static final PersonEntity pe3 = new PersonEntity();
    private static FireStationEntity fs1;
    private static FireStationEntity fs2;
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
        pe1.setFireStation(1L);
        pe2.setFirstName("p2FirstName");
        pe2.setLastName("p2LastName");
        pe2.setAddress("p2Address");
        pe2.setPhone("p2Phone");
        pe2.setBirthdate(LocalDate.of(2002, 1, 2));
        pe2.setMedications(Arrays.asList("p2Med1:1mg", "p2Med2:2mg"));
        pe2.setAllergies(Arrays.asList("p2All1", "p2All2"));
        pe2.setFireStation(1L);
        pe3.setFirstName("p3FirstName");
        pe3.setLastName("p3LastName");
        pe3.setAddress("p3Address");
        pe3.setPhone("p2Phone");
        pe3.setBirthdate(LocalDate.of(2001, 1, 1));
        pe3.setMedications(Arrays.asList("p3Med1:1mg", "p3Med2:2mg"));
        pe3.setAllergies(Arrays.asList("p3All1", "p3All2"));
        pe3.setFireStation(1L);
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

        fs1 = new FireStationEntity();
        fs1.setStation(1L);
        fs1.setAddresses(Arrays.asList("adr1", "adr2"));
        fs2 = new FireStationEntity();
        fs2.setStation(2L);
        fs2.setAddresses(Arrays.asList("adr3", "adr4"));

    }

    @Test
    public void should_returnPopulatedPersonList_whenGetPersonsByPopulatedStation() {
        when(personDao.findAllByFireStation(anyLong())).thenReturn(personEntityList);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(snaService.getPersonsByStation(anyLong())).isEqualTo(personList);
    }

    @Test
    public void should_returnEmptyPersonList_whenGetPersonsByWrongStation() {
        when(personDao.findAllByFireStation(anyLong())).thenReturn(personEntityEmptyList);

        assertThat(snaService.getPersonsByStation(anyLong())).isEqualTo(personEmptyList);
    }

    @Test
    public void should_returnPopulatedPersonList_whenGetPersonsByPopulatedStations() {
        when(personDao.findAllByFireStationIn(anyList())).thenReturn(personEntityList);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(snaService.getPersonsByStations(anyList())).isEqualTo(personList);
    }


    @Test
    public void should_returnPopulatedPersonList_whenGetPersonsByPopulatedAddress() {
        when(personDao.findAllByAddress(anyString())).thenReturn(personEntityList);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(snaService.getPersonsByAddress(anyString())).isEqualTo(personList);
    }

    @Test
    public void should_returnEmptyPersonList_whenGetPersonsByWrongAddress() {
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

    @Test
    public void should_returnPopulatedPersonList_whenGetPersonsByCityOfPopulatedCity() {
        when(personDao.findAllByCity(anyString())).thenReturn(personEntityList);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(snaService.getPersonsByCity(anyString())).isEqualTo(personList);
    }

    @Test
    public void should_returnTrue_whenPersonAlreadyExists() {
        when(personDao.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.of(pe1));

        assertThat(snaService.personAlreadyExists(anyString(), anyString())).isTrue();
    }

    @Test
    public void should_returnFalse_whenPersonNotAlreadyExists() {
        when(personDao.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.empty());

        assertThat(snaService.personAlreadyExists(anyString(), anyString())).isFalse();
    }

    @Test
    public void should_returnPopulatedPersonList_whenGetAllPersons() {
        when(personDao.findAll()).thenReturn(personEntityList);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(snaService.getAllPersons()).isEqualTo(personList);
    }

    @Test
    public void should_returnValidPerson_whenSavePerson() {
        when(personDao.save(any())).thenReturn(pe1);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(snaService.createPerson(pd1)).isEqualTo(p1);
    }

    @Test
    public void should_returnValidPerson_whenUpdateExistingPerson() {
        when(personDao.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.of(pe1));
        when(personDao.save(any())).thenReturn(pe1);
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        assertThat(snaService.updatePerson(pd1.getFirstName(), pd1.getLastName(), pd1)).isEqualTo(p1);
    }

    @Test
    public void should_throwNoSuchElementException_whenUpdateNonExistingPerson() {
        when(personDao.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> snaService.updatePerson(anyString(), anyString(), pd1));
    }

    @Test
    public void should_delete_whenDeleteExistingPerson() {
        when(personDao.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.of(pe1));

        snaService.deletePerson(anyString(), anyString());

        verify(personDao, times(1)).delete(pe1);
    }

    @Test
    public void should_throwNoSuchElementException_whenDeleteNonExistingPerson() {
        when(personDao.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> snaService.deletePerson(anyString(), anyString()));
    }

    @Test
    public void should_returnPopulatedFireStations_whenGetAllFireStations() {
        when(fireStationDao.findAll()).thenReturn(Arrays.asList(fs1, fs2));

        List<FireStation> fireStationList = snaService.getAllFireStations();

        assertThat(fireStationList).isNotNull();
        assertThat(fireStationList.get(0).getAddresses().get(0)).isEqualTo("adr1");
        assertThat(fireStationList.get(1).getAddresses().get(1)).isEqualTo("adr4");
    }

    @Test
    public void should_throwNoSuchElementException_whenGetEmptyFireStationByStationAndAddress() {
        when(fireStationDao.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> snaService.getFireStationByStationAndAddress(1L, anyString()));
    }

    @Test
    public void should_returnPopulatedFireStation_whenGetPopulatedFireStationByStationAndAddress() {
        when(fireStationDao.findById(anyLong())).thenReturn(Optional.of(fs1));

        FireStation fireStation = snaService.getFireStationByStationAndAddress(1L, "adr1");

        assertThat(fireStation).isNotNull();
        assertThat(fireStation.getAddresses().get(0)).isEqualTo("adr1");
    }

    @Test
    public void should_throwNoSuchElementException_whenGetPopulatedFireStationByStationAndMismatchAddress() {
        when(fireStationDao.findById(anyLong())).thenReturn(Optional.of(fs1));

        assertThrows(NoSuchElementException.class, () -> snaService.getFireStationByStationAndAddress(1L, "adrX"));
    }

    @Test
    public void should_throwEntityExistsException_whenCreateExistingFireStationMapping() {
        when(fireStationDao.findById(1L)).thenReturn(Optional.of(fs1));

        FireStationsDto fireStationsDto = new FireStationsDto();
        fireStationsDto.setStation(1L);
        fireStationsDto.setAddress("adr1");

        assertThrows(EntityExistsException.class, () -> snaService.createFireStationMapping(fireStationsDto));
    }
/*
    @Test
    public void should_addAddressToFireStation_whenCreateFireStationMappingOfExistingFireStationAndNewAddress() {
        when(fireStationDao.findById(1L)).thenReturn(Optional.of(fs1));

        FireStationsDto fireStationsDto = new FireStationsDto();
        fireStationsDto.setStation(1L);
        fireStationsDto.setAddress("adrX");

        snaService.createFireStationMapping(fireStationsDto);

        verify(fireStationDao, times(1)).save(fs1);

    }

 */


}