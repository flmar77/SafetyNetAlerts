package com.safetynetalerts.api.domain.model;

import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Data;

@Data
@JGlobalMap
public class FireStationPersonModel {
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
}
