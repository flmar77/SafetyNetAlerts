package com.safetynetalerts.api.unittests.domain;

import com.safetynetalerts.api.dao.entity.FireStationEntity;
import com.safetynetalerts.api.dao.repository.FireStationRepo;
import com.safetynetalerts.api.domain.model.FireStation;
import com.safetynetalerts.api.domain.service.FireStationService;
import com.safetynetalerts.api.domain.service.PersonService;
import com.safetynetalerts.api.web.dto.FireStationsDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FireStationServiceTest {

    @InjectMocks
    private FireStationService fireStationService;

    @Mock
    private FireStationRepo fireStationRepo;

    @Mock
    private PersonService personService;

    private static FireStationEntity fs1;
    private static FireStationEntity fs2;

    @BeforeAll
    static void setUp() {
        fs1 = new FireStationEntity();
        fs1.setStation(1);
        fs1.setAddresses(Arrays.asList("adr1", "adr2"));
        fs2 = new FireStationEntity();
        fs2.setStation(2);
        fs2.setAddresses(Arrays.asList("adr3", "adr4"));
    }

    @Test
    public void should_returnPopulatedFireStations_whenGetAllFireStations() {
        when(fireStationRepo.findAll()).thenReturn(Arrays.asList(fs1, fs2));

        List<FireStation> fireStationList = fireStationService.getAllFireStations();

        assertThat(fireStationList).isNotNull();
        assertThat(fireStationList.get(0).getAddresses().get(0)).isEqualTo("adr1");
        assertThat(fireStationList.get(1).getAddresses().get(1)).isEqualTo("adr4");
    }

    @Test
    public void should_throwNoSuchElementException_whenGetEmptyFireStationByStationAndAddress() {
        when(fireStationRepo.findByStation(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> fireStationService.getFireStationByStationAndAddress(1, anyString()));
    }

    @Test
    public void should_returnPopulatedFireStation_whenGetPopulatedFireStationByStationAndAddress() {
        when(fireStationRepo.findByStation(anyInt())).thenReturn(Optional.of(fs1));

        FireStation fireStation = fireStationService.getFireStationByStationAndAddress(1, "adr1");

        assertThat(fireStation).isNotNull();
        assertThat(fireStation.getAddresses().get(0)).isEqualTo("adr1");
    }

    @Test
    public void should_throwNoSuchElementException_whenGetPopulatedFireStationByStationAndMismatchAddress() {
        when(fireStationRepo.findByStation(anyInt())).thenReturn(Optional.of(fs1));

        assertThrows(NoSuchElementException.class, () -> fireStationService.getFireStationByStationAndAddress(1, "adrWrong"));
    }

    @Test
    public void should_throwEntityExistsException_whenCreateExistingFireStationMapping() {
        when(fireStationRepo.findByStation(1)).thenReturn(Optional.of(fs1));

        FireStationsDto fireStationsDto = new FireStationsDto();
        fireStationsDto.setStation(1);
        fireStationsDto.setAddress("adr1");

        assertThrows(EntityExistsException.class, () -> fireStationService.createFireStationMapping(fireStationsDto));
    }

    @Test
    public void should_addAddressToFireStation_whenCreateFireStationMappingOfExistingFireStationAndNewAddress() {
        when(fireStationRepo.findByStation(1)).thenReturn(Optional.of(fs1));

        FireStationsDto fireStationsDto = new FireStationsDto();
        fireStationsDto.setStation(1);
        fireStationsDto.setAddress("adrAdd");

        fireStationService.createFireStationMapping(fireStationsDto);

        verify(fireStationRepo, times(1)).save(argThat(it -> {
            assertThat(it.getAddresses().get(2)).isEqualTo("adrAdd");
            return true;
        }));
    }

    @Test
    public void should_createNewFireStation_whenCreateFireStationMappingOfNewFireStation() {
        when(fireStationRepo.findByStation(1)).thenReturn(Optional.empty());

        FireStationsDto fireStationsDto = new FireStationsDto();
        fireStationsDto.setStation(1);
        fireStationsDto.setAddress("adrNew");

        fireStationService.createFireStationMapping(fireStationsDto);

        verify(fireStationRepo, times(1)).save(argThat(it -> {
            assertThat(it.getAddresses().get(0)).isEqualTo("adrNew");
            return true;
        }));

    }

    @Test
    public void should_throwEntityExistsException_whenUpdateExistingFireStationMapping() {
        when(fireStationRepo.findByAddresses(anyString())).thenReturn(Optional.of(fs1));

        FireStationsDto fireStationsDto = new FireStationsDto();
        fireStationsDto.setStation(1);
        fireStationsDto.setAddress("adr1");

        assertThrows(EntityExistsException.class, () -> fireStationService.updateFireStationMapping(fireStationsDto));
    }

    @Test
    public void should_updateFireStationsAndPersons_whenUpdateFireStationMappingOfExistingAddressOnAnotherFireStation() {
        FireStationEntity fsUpdate = new FireStationEntity();
        fsUpdate.setStation(3);
        fsUpdate.setAddresses(Arrays.asList("adr5", "adr6"));
        when(fireStationRepo.findByAddresses(anyString())).thenReturn(Optional.of(fsUpdate));
        when(personService.getPersonsByAddress(anyString())).thenReturn(anyList());

        FireStationsDto fireStationsDto = new FireStationsDto();
        fireStationsDto.setStation(4);
        fireStationsDto.setAddress("adr5");

        fireStationService.updateFireStationMapping(fireStationsDto);

        verify(fireStationRepo, times(2)).save(any(FireStationEntity.class));
    }

    @Test
    public void should_throwNoSuchElementException_whenUpdateFireStationMappingOfNonExistingAddress() {
        when(fireStationRepo.findByAddresses(anyString())).thenReturn(Optional.empty());

        FireStationsDto fireStationsDto = new FireStationsDto();
        fireStationsDto.setStation(0);
        fireStationsDto.setAddress("adrWrong");

        assertThrows(NoSuchElementException.class, () -> fireStationService.updateFireStationMapping(fireStationsDto));
    }

    @Test
    public void should_deleteFireStationMapping_whenDeleteExistingFireStationMapping() {
        FireStationEntity fsDelete = new FireStationEntity();
        fsDelete.setStation(5);
        fsDelete.setAddresses(Arrays.asList("adr7", "adr8"));
        when(fireStationRepo.findByAddresses(anyString())).thenReturn(Optional.of(fsDelete));
        when(personService.getPersonsByAddress(anyString())).thenReturn(anyList());

        fireStationService.deleteFireStationMapping(5, "adr7");

        verify(fireStationRepo, times(1)).save(any(FireStationEntity.class));
    }

    @Test
    public void should_throwNoSuchElementException_whenDeleteFireStationMappingOfExistingAddressOnAnotherFireStation() {
        FireStationEntity fsDelete = new FireStationEntity();
        fsDelete.setStation(5);
        fsDelete.setAddresses(Arrays.asList("adr7", "adr8"));
        when(fireStationRepo.findByAddresses(anyString())).thenReturn(Optional.of(fsDelete));

        assertThrows(NoSuchElementException.class, () -> fireStationService.deleteFireStationMapping(6, "adr8"));
    }

    @Test
    public void should_throwNoSuchElementException_whenDeleteNewFireStationMapping() {
        when(fireStationRepo.findByAddresses(anyString())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> fireStationService.deleteFireStationMapping(6, "adr8"));
    }

    @Test
    public void should_SaveAll_whenSaveAllFireStationEntities() {
        fireStationService.saveAllFireStationEntities(anyList());

        verify(fireStationRepo, times(1)).saveAll(anyList());
    }

}
