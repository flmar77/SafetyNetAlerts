package com.safetynetalerts.api.unittests;

import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.Person;
import com.safetynetalerts.api.domain.model.AlertedChildrenAndAdults;
import com.safetynetalerts.api.domain.model.AlertedPerson;
import com.safetynetalerts.api.domain.model.CoveredPerson;
import com.safetynetalerts.api.domain.model.CoveredPersonsAndStats;
import com.safetynetalerts.api.domain.service.SnaService;
import com.safetynetalerts.api.helper.DateHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    public void should_returnValidData_whenGetCoveredPersonsAndStatsByFireStation() {
        //given
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        Person p1 = new Person();
        p1.setFirstName("p1FirstName");
        p1.setLastName("p1LastName");
        p1.setAddress("p1Address");
        p1.setPhone("p1Phone");
        p1.setBirthdate(LocalDate.of(2002, 1, 1));
        Person p2 = new Person();
        p2.setFirstName("p2FirstName");
        p2.setLastName("p2LastName");
        p2.setAddress("p2Address");
        p2.setPhone("p2Phone");
        p2.setBirthdate(LocalDate.of(2002, 1, 2));
        Person p3 = new Person();
        p3.setFirstName("p3FirstName");
        p3.setLastName("p3LastName");
        p3.setAddress("p3Address");
        p3.setPhone("p3Phone");
        p3.setBirthdate(LocalDate.of(2001, 1, 1));
        List<Person> personList = Arrays.asList(p1, p2, p3);
        when(personDao.findAllByFireStation(anyInt())).thenReturn(personList);

        CoveredPerson cp1 = new CoveredPerson();
        cp1.setFirstName("p1FirstName");
        cp1.setLastName("p1LastName");
        cp1.setAddress("p1Address");
        cp1.setPhone("p1Phone");
        CoveredPerson cp2 = new CoveredPerson();
        cp2.setFirstName("p2FirstName");
        cp2.setLastName("p2LastName");
        cp2.setAddress("p2Address");
        cp2.setPhone("p2Phone");
        CoveredPerson cp3 = new CoveredPerson();
        cp3.setFirstName("p3FirstName");
        cp3.setLastName("p3LastName");
        cp3.setAddress("p3Address");
        cp3.setPhone("p3Phone");
        List<CoveredPerson> coveredPersonList = Arrays.asList(cp1, cp2, cp3);
        CoveredPersonsAndStats expectedCoveredPersonsAndStats = new CoveredPersonsAndStats();
        expectedCoveredPersonsAndStats.setCoveredPersons(coveredPersonList);
        expectedCoveredPersonsAndStats.setAdultCounter(1);
        expectedCoveredPersonsAndStats.setChildCounter(2);

        //when
        CoveredPersonsAndStats actualCoveredPersonsAndStats = snaService.getCoveredPersonsAndStatsByFireStation(1);

        //then
        assertThat(actualCoveredPersonsAndStats).isEqualTo(expectedCoveredPersonsAndStats);
    }

    //TODO : wrong test ?

    @Test
    public void should_returnValidData_whenGetAlertedChildrenAndAdultsByAddress() {
        //given
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));

        Person p1 = new Person();
        p1.setFirstName("p1FirstName");
        p1.setLastName("p1LastName");
        p1.setBirthdate(LocalDate.of(2002, 1, 1));
        Person p2 = new Person();
        p2.setFirstName("p2FirstName");
        p2.setLastName("p2LastName");
        p2.setBirthdate(LocalDate.of(2002, 1, 2));
        Person p3 = new Person();
        p3.setFirstName("p3FirstName");
        p3.setLastName("p3LastName");
        p3.setBirthdate(LocalDate.of(2001, 1, 1));
        List<Person> personList = Arrays.asList(p1, p2, p3);
        when(personDao.findAllByAddress(anyString())).thenReturn(personList);

        AlertedPerson ap1 = new AlertedPerson();
        ap1.setFirstName("p1FirstName");
        ap1.setLastName("p1LastName");
        ap1.setAge(18);
        AlertedPerson ap2 = new AlertedPerson();
        ap2.setFirstName("p2FirstName");
        ap2.setLastName("p2LastName");
        ap2.setAge(18);
        AlertedPerson ap3 = new AlertedPerson();
        ap3.setFirstName("p3FirstName");
        ap3.setLastName("p3LastName");
        ap3.setAge(19);
        List<AlertedPerson> alertedChildrenList = Arrays.asList(ap1, ap2);
        List<AlertedPerson> alertedAdultsList = Arrays.asList(ap3);
        AlertedChildrenAndAdults expectedAlertedChildrenAndAdults = new AlertedChildrenAndAdults();
        expectedAlertedChildrenAndAdults.setAlertedChildren(alertedChildrenList);
        expectedAlertedChildrenAndAdults.setAlertedAdults(alertedAdultsList);

        //when
        AlertedChildrenAndAdults actualAlertedChildrenAndAdults = snaService.getAlertedChildrenAndAdultsByAddress("abc");

        //then
        assertThat(actualAlertedChildrenAndAdults).isEqualTo(expectedAlertedChildrenAndAdults);
    }

    //TODO : wrong test ?

}
