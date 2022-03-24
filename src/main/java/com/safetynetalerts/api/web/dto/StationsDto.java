package com.safetynetalerts.api.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StationsDto {
    private List<StationsPersonByAddressDto> personsByAddress;
}
