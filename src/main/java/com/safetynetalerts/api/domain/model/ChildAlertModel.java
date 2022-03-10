package com.safetynetalerts.api.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class ChildAlertModel {
    private List<ChildAlertPersonModel> alertedChildren;
    private List<ChildAlertPersonModel> alertedAdults;
}
