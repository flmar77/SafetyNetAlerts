package com.safetynetalerts.api.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChildAlertDto {
    private List<ChildAlertPersonDto> alertedChildren;
    private List<ChildAlertPersonDto> alertedAdults;
}
