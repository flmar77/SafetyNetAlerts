package com.safetynetalerts.api.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Getter
@Setter
@Entity
public class FireStationEntity {

    @Id
    @GeneratedValue
    private Long id;

    private int station;

    @ElementCollection
    private List<String> addresses;

}
