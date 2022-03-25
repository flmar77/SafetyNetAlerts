package com.safetynetalerts.api.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FireStationDto {
    private List<FireStationPersonDto> fireStationPersons;
    private long adultCounter;
    private long childCounter;
}
