package com.safetynetalerts.api.web.dto;

import com.googlecode.jmapper.annotations.JGlobalMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JGlobalMap
public class FireStationPersonDto {
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
}
