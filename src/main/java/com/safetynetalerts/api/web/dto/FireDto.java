package com.safetynetalerts.api.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class FireDto {
    private List<FirePersonDto> firePersons;
    private int fireStation;
}
