package com.safetynetalerts.api.data.dao;

import com.safetynetalerts.api.data.entity.FireStationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FireStationDao extends CrudRepository<FireStationEntity, Long> {
}
