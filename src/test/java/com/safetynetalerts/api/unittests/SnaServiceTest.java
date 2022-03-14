package com.safetynetalerts.api.unittests;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.PersonEntity;
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

    private static List<PersonEntity> personEntityList;

    @BeforeAll
    static void setUp() {
        PersonEntity p1 = new PersonEntity();
        p1.setFirstName("p1FirstName");
        p1.setLastName("p1LastName");
        p1.setAddress("p1Address");
        p1.setPhone("p1Phone");
        p1.setBirthdate(LocalDate.of(2002, 1, 1));
        p1.setMedications(Arrays.asList("p1Med1:1mg", "p1Med2:2mg"));
        p1.setAllergies(Arrays.asList("p1All1", "p1All2"));
        p1.setFireStation(1);
        PersonEntity p2 = new PersonEntity();
        p2.setFirstName("p2FirstName");
        p2.setLastName("p2LastName");
        p2.setAddress("p2Address");
        p2.setPhone("p2Phone");
        p2.setBirthdate(LocalDate.of(2002, 1, 2));
        p2.setMedications(Arrays.asList("p2Med1:1mg", "p2Med2:2mg"));
        p2.setAllergies(Arrays.asList("p2All1", "p2All2"));
        p2.setFireStation(1);
        PersonEntity p3 = new PersonEntity();
        p3.setFirstName("p3FirstName");
        p3.setLastName("p3LastName");
        p3.setAddress("p3Address");
        p3.setPhone("p3Phone");
        p3.setBirthdate(LocalDate.of(2001, 1, 1));
        p3.setMedications(Arrays.asList("p3Med1:1mg", "p3Med2:2mg"));
        p3.setAllergies(Arrays.asList("p3All1", "p3All2"));
        p3.setFireStation(1);
        personEntityList = Arrays.asList(p1, p2, p3);
    }

    @Test
    public void should_returnValidData_whenGetFireStationModel() {
        //given
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));
        when(personDao.findAllByFireStation(anyInt())).thenReturn(personEntityList);

        JMapper<FireStationPersonModel, PersonEntity> personMapper = new JMapper<>(FireStationPersonModel.class, PersonEntity.class);
        FireStationPersonModel fsp1 = personMapper.getDestination(personEntityList.get(0));
        FireStationPersonModel fsp2 = personMapper.getDestination(personEntityList.get(1));
        FireStationPersonModel fsp3 = personMapper.getDestination(personEntityList.get(2));
        FireStationModel expectedFireStationModel = new FireStationModel();
        expectedFireStationModel.setFireStationPersonModels(Arrays.asList(fsp1, fsp2, fsp3));
        expectedFireStationModel.setAdultCounter(1);
        expectedFireStationModel.setChildCounter(2);

        //when
        FireStationModel actualFireStationModel = snaService.getFireStationModel(1);

        //then
        assertThat(actualFireStationModel).isEqualTo(expectedFireStationModel);
    }

    @Test
    public void should_returnValidData_whenGetChildAlertModel() {
        //given
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));
        when(personDao.findAllByAddress(anyString())).thenReturn(personEntityList);

        JMapper<ChildAlertPersonModel, PersonEntity> personMapper = new JMapper<>(ChildAlertPersonModel.class, PersonEntity.class);
        ChildAlertPersonModel cap1 = personMapper.getDestination(personEntityList.get(0));
        cap1.setAge(18);
        ChildAlertPersonModel cap2 = personMapper.getDestination(personEntityList.get(1));
        cap2.setAge(18);
        ChildAlertPersonModel cap3 = personMapper.getDestination(personEntityList.get(2));
        cap3.setAge(19);
        ChildAlertModel expectedChildAlertModel = new ChildAlertModel();
        expectedChildAlertModel.setAlertedChildren(Arrays.asList(cap1, cap2));
        expectedChildAlertModel.setAlertedAdults(Collections.singletonList(cap3));

        //when
        ChildAlertModel actualChildAlertModel = snaService.getChildAlertModel("abc");

        //then
        assertThat(actualChildAlertModel).isEqualTo(expectedChildAlertModel);
    }

    @Test
    public void should_returnValidData_whenGetPhoneAlertModel() {
        //given
        List<PersonEntity> morePersonListEntity = new ArrayList<>(personEntityList);
        PersonEntity p4 = new PersonEntity();
        p4.setPhone("p3Phone");
        morePersonListEntity.add(p4);
        when(personDao.findAllByFireStation(anyInt())).thenReturn(morePersonListEntity);

        PhoneAlertModel expectedPhoneAlertModel = new PhoneAlertModel();
        expectedPhoneAlertModel.setPhones(Arrays.asList("p1Phone", "p2Phone", "p3Phone"));

        //when
        PhoneAlertModel actualPhoneAlertModel = snaService.getPhoneAlertModel(1);

        //then
        assertThat(actualPhoneAlertModel).isEqualTo(expectedPhoneAlertModel);
    }

    @Test
    public void should_returnValidData_whenGetFireModel() {
        //given
        when(dateHelper.now()).thenReturn(LocalDate.of(2020, 2, 1));
        when(personDao.findAllByAddress(anyString())).thenReturn(personEntityList);

        JMapper<FirePersonModel, PersonEntity> personMapper = new JMapper<>(FirePersonModel.class, PersonEntity.class);
        FirePersonModel fp1 = personMapper.getDestination(personEntityList.get(0));
        fp1.setAge(18);
        FirePersonModel fp2 = personMapper.getDestination(personEntityList.get(1));
        fp2.setAge(18);
        FirePersonModel fp3 = personMapper.getDestination(personEntityList.get(2));
        fp3.setAge(19);
        FireModel expectedFireModel = new FireModel();
        expectedFireModel.setFirePersons(Arrays.asList(fp1, fp2, fp3));
        expectedFireModel.setFireStation(1);

        //when
        FireModel actualFireModel = snaService.getFireModel("abc");

        //then
        assertThat(actualFireModel).isEqualTo(expectedFireModel);
    }

}
