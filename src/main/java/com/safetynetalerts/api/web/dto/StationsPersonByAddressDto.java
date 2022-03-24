package com.safetynetalerts.api.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StationsPersonByAddressDto {
    private String address;
    private List<StationsPersonDto> stationsPersons;
}
