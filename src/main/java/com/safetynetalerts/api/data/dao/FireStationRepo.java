package com.safetynetalerts.api.data.dao;

import com.safetynetalerts.api.data.entity.FireStationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FireStationRepo extends CrudRepository<FireStationEntity, Long> {
    List<FireStationEntity> findAll();

    Optional<FireStationEntity> findByAddresses(String address);
}
