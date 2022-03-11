package com.safetynetalerts.api.data.entity;

import com.googlecode.jmapper.annotations.JMap;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class FireStationEntity {

    @Id
    @GeneratedValue
    private long Id;

    //TODO encapsuler list<address> dans station ?
    @JMap
    private int station;
    @JMap
    private String address;

}
