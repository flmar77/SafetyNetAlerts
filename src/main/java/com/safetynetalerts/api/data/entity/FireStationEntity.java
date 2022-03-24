package com.safetynetalerts.api.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Getter
@Setter
@Entity
public class FireStationEntity {

    //TODO : use ID auto-généré

    @Id
    private Long station;

    @ElementCollection
    private List<String> addresses;

}
