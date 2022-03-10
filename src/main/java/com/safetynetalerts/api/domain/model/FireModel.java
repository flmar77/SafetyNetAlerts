package com.safetynetalerts.api.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class FireModel {
    private List<FirePersonModel> firePersons;
    private int fireStation;
}
