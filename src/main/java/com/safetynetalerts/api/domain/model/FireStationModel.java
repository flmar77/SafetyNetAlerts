package com.safetynetalerts.api.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class FireStationModel {
    private List<FireStationPersonModel> fireStationPersonModels;
    private int adultCounter;
    private int childCounter;
}
