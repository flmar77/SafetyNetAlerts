package com.safetynetalerts.api.data.repository;

import com.safetynetalerts.api.data.entity.PersonEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepo extends CrudRepository<PersonEntity, Long> {
    List<PersonEntity> findAllByFireStation(int fireStation);

    List<PersonEntity> findAllByFireStationIn(List<Integer> stationNumbers);

    List<PersonEntity> findAllByAddress(String Address);

    List<PersonEntity> findAllByFirstNameAndLastName(String firstName, String lastName);

    List<PersonEntity> findAllByCity(String city);

    Optional<PersonEntity> findByFirstNameAndLastName(String firstName, String lastName);
}
