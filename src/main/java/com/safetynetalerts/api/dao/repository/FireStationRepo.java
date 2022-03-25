package com.safetynetalerts.api.dao.repository;

import com.safetynetalerts.api.dao.entity.FireStationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FireStationRepo extends CrudRepository<FireStationEntity, Long> {
    List<FireStationEntity> findAll();

    Optional<FireStationEntity> findByAddresses(String address);

    Optional<FireStationEntity> findByStation(int station);
}
