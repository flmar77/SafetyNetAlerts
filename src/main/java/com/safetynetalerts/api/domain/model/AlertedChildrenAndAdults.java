package com.safetynetalerts.api.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class AlertedChildrenAndAdults {
    private List<AlertedPerson> alertedChildren;
    private List<AlertedPerson> alertedAdults;
}
