package com.safetynetalerts.api.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class StationsDto {
    private List<StationsPersonByAddressDto> personsByAddress;
}
