package com.safetynetalerts.api.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class PersonInfoDto {
    private List<PersonInfoPersonDto> personsInfo;
}
