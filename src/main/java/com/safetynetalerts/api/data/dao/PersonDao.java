package com.safetynetalerts.api.data.dao;

import com.safetynetalerts.api.data.entity.PersonEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonDao extends CrudRepository<PersonEntity, Long> {
    List<PersonEntity> findAllByFireStation(int fireStation);

    List<PersonEntity> findAllByAddress(String Address);

    List<PersonEntity> findAllByFirstNameAndLastName(String firstName, String lastName);
}
