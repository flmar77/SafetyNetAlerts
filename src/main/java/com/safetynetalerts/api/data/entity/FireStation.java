package com.safetynetalerts.api.data.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class FireStation {

    @Id
    @GeneratedValue
    private Long id;

    private String address;
    private int station;
}
