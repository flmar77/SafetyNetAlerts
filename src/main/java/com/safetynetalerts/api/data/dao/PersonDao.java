package com.safetynetalerts.api.data.dao;

import com.safetynetalerts.api.data.entity.PersonEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//TODO rename repo
@Repository
public interface PersonDao extends CrudRepository<PersonEntity, Long> {
    List<PersonEntity> findAllByFireStation(Long fireStation);

    List<PersonEntity> findAllByFireStationIn(List<Long> stationNumbers);

    List<PersonEntity> findAllByAddress(String Address);

    List<PersonEntity> findAllByFirstNameAndLastName(String firstName, String lastName);

    List<PersonEntity> findAllByCity(String city);

    Optional<PersonEntity> findByFirstNameAndLastName(String firstName, String lastName);
}
