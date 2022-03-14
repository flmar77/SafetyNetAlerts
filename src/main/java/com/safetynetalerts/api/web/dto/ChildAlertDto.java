package com.safetynetalerts.api.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChildAlertDto {
    private List<ChildAlertPersonDto> alertedChildren;
    private List<ChildAlertPersonDto> alertedAdults;
}
