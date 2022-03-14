package com.safetynetalerts.api.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class FireStationDto {
    private List<FireStationPersonDto> fireStationPersons;
    private long adultCounter;
    private long childCounter;
}
