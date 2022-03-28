package com.safetynetalerts.api.unittests.input;

import com.safetynetalerts.api.domain.service.FireStationService;
import com.safetynetalerts.api.domain.service.PersonService;
import com.safetynetalerts.api.input.service.InputService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class InputServiceTest {

    @InjectMocks
    private InputService inputService;

    @Mock
    private PersonService personService;

    @Mock
    private FireStationService fireStationService;

    @Test
    public void should_feedEntities() throws IOException {
        inputService.loadInMemoryDbFromInput();

        verify(fireStationService, times(1)).saveAllFireStationEntities(argThat(fireStationEntities -> {
            assertThat(fireStationEntities.size()).isEqualTo(4);
            return true;
        }));
        verify(personService, times(1)).saveAllPersonEntities(argThat(personEntities -> {
            assertThat(personEntities.size()).isEqualTo(23);
            return true;
        }));
    }
}
