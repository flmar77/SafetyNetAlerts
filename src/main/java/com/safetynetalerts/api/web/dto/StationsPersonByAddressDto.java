package com.safetynetalerts.api.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class StationsPersonByAddressDto {
    private String address;
    private List<StationsPersonDto> stationsPersons;
}
