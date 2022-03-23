package com.safetynetalerts.api.data.entity;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Data
@Entity
public class FireStationEntity {

    @Id
    private Long station;

    @ElementCollection
    private List<String> addresses;

}
