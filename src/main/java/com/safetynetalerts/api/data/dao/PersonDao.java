package com.safetynetalerts.api.data.dao;

import com.safetynetalerts.api.data.entity.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonDao extends CrudRepository<Person, Long> {
    List<Person> findAllByFireStation(int fireStation);
}
