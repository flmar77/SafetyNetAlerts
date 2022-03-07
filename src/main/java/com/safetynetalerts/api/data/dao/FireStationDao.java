package com.safetynetalerts.api.data.dao;

import com.safetynetalerts.api.data.entity.FireStation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FireStationDao extends CrudRepository<FireStation, Long> {
}
