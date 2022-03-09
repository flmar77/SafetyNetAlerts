package com.safetynetalerts.api.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class CoveredPersonsAndStats {
    private List<CoveredPerson> coveredPersons;
    private int adultCounter;
    private int childCounter;
}
