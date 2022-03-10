package com.safetynetalerts.api.unittests;

import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.Person;
import com.safetynetalerts.api.domain.model.*;
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

    private static List<Person> personList;

    @BeforeAll
    static void setUp() {
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
        personList = Arrays.asList(p1, p2, p3);
    }

    @Test
    public void should_returnValidData_whenGetFireStationModel() {
        //given
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));
        when(personDao.findAllByFireStation(anyInt())).thenReturn(personList);

        FireStationPersonModel cp1 = new FireStationPersonModel();
        cp1.setFirstName("p1FirstName");
        cp1.setLastName("p1LastName");
        cp1.setAddress("p1Address");
        cp1.setPhone("p1Phone");
        FireStationPersonModel cp2 = new FireStationPersonModel();
        cp2.setFirstName("p2FirstName");
        cp2.setLastName("p2LastName");
        cp2.setAddress("p2Address");
        cp2.setPhone("p2Phone");
        FireStationPersonModel cp3 = new FireStationPersonModel();
        cp3.setFirstName("p3FirstName");
        cp3.setLastName("p3LastName");
        cp3.setAddress("p3Address");
        cp3.setPhone("p3Phone");
        List<FireStationPersonModel> fireStationPersonModelList = Arrays.asList(cp1, cp2, cp3);
        FireStationModel expectedFireStationModel = new FireStationModel();
        expectedFireStationModel.setFireStationPersonModels(fireStationPersonModelList);
        expectedFireStationModel.setAdultCounter(1);
        expectedFireStationModel.setChildCounter(2);

        //when
        FireStationModel actualFireStationModel = snaService.getFireStationModel(1);

        //then
        assertThat(actualFireStationModel).isEqualTo(expectedFireStationModel);
    }

    //TODO : wrong test ?

    @Test
    public void should_returnValidData_whenGetChildAlertModel() {
        //given
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));
        when(personDao.findAllByAddress(anyString())).thenReturn(personList);

        ChildAlertPersonModel ap1 = new ChildAlertPersonModel();
        ap1.setFirstName("p1FirstName");
        ap1.setLastName("p1LastName");
        ap1.setAge(18);
        ChildAlertPersonModel ap2 = new ChildAlertPersonModel();
        ap2.setFirstName("p2FirstName");
        ap2.setLastName("p2LastName");
        ap2.setAge(18);
        ChildAlertPersonModel ap3 = new ChildAlertPersonModel();
        ap3.setFirstName("p3FirstName");
        ap3.setLastName("p3LastName");
        ap3.setAge(19);
        List<ChildAlertPersonModel> alertedChildrenList = Arrays.asList(ap1, ap2);
        List<ChildAlertPersonModel> alertedAdultsList = Collections.singletonList(ap3);
        ChildAlertModel expectedChildAlertModel = new ChildAlertModel();
        expectedChildAlertModel.setAlertedChildren(alertedChildrenList);
        expectedChildAlertModel.setAlertedAdults(alertedAdultsList);

        //when
        ChildAlertModel actualChildAlertModel = snaService.getChildAlertModel("abc");

        //then
        assertThat(actualChildAlertModel).isEqualTo(expectedChildAlertModel);
    }

    //TODO : wrong test ?

    @Test
    public void should_returnValidData_whenGetPhoneAlertModel() {
        //given
        List<Person> morePersonList = new ArrayList<>(personList);
        Person p4 = new Person();
        p4.setPhone("p3Phone");
        morePersonList.add(p4);
        when(personDao.findAllByFireStation(anyInt())).thenReturn(morePersonList);

        PhoneAlertModel expectedPhoneAlertModel = new PhoneAlertModel();
        expectedPhoneAlertModel.setPhones(Arrays.asList("p1Phone", "p2Phone", "p3Phone"));

        //when
        PhoneAlertModel actualPhoneAlertModel = snaService.getPhoneAlertModel(1);

        //then
        assertThat(actualPhoneAlertModel).isEqualTo(expectedPhoneAlertModel);
    }

    //TODO : wrong test ?


}
