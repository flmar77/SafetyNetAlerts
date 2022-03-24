package com.safetynetalerts.api.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FireDto {
    private List<FirePersonDto> firePersons;
    private int fireStation;
}
