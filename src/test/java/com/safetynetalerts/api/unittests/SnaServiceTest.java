package com.safetynetalerts.api.unittests;

import com.safetynetalerts.api.data.dao.PersonDao;
import com.safetynetalerts.api.data.entity.PersonEntity;
import com.safetynetalerts.api.domain.service.SnaService;
import com.safetynetalerts.api.helper.DateHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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

}