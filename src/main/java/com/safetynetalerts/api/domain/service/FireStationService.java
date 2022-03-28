package com.safetynetalerts.api.domain.service;

import com.safetynetalerts.api.dao.entity.FireStationEntity;
import com.safetynetalerts.api.dao.repository.FireStationRepo;
import com.safetynetalerts.api.domain.model.FireStation;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.web.dto.FireStationsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FireStationService {

    @Autowired
    private FireStationRepo fireStationRepo;

    @Autowired
    private PersonService personService;

    public List<FireStation> getAllFireStations() {

        return fireStationRepo.findAll().stream()
                .map(fireStationEntity -> {
                    FireStation fireStation = new FireStation();
                    fireStation.setStation(fireStationEntity.getStation());
                    fireStation.setAddresses(fireStationEntity.getAddresses());
                    return fireStation;
                })
                .collect(Collectors.toList());
    }

    public FireStation getFireStationByStationAndAddress(int station, String address) {

        FireStation fireStation = new FireStation();

        FireStationEntity fireStationEntity = fireStationRepo.findByStation(station)
                .orElseThrow(NoSuchElementException::new);

        if (fireStationEntity.getAddresses().stream().anyMatch(addressEntity -> addressEntity.equals(address))) {
            fireStation.setStation(station);
            fireStation.setAddresses(Collections.singletonList(address));
        } else {
            throw new NoSuchElementException();
        }

        return fireStation;

    }

    public void createFireStationMapping(FireStationsDto fireStationsDto) {
        Optional<FireStationEntity> optionalFireStationEntity = fireStationRepo.findByStation(fireStationsDto.getStation());

        if (optionalFireStationEntity.isPresent()) {
            // station exists
            FireStationEntity fireStationEntity = optionalFireStationEntity.get();

            if (fireStationEntity.getAddresses().stream().anyMatch(addressEntity -> addressEntity.equals(fireStationsDto.getAddress()))) {
                // station exists and address exists
                throw new EntityExistsException();

            } else {
                // station exists and address doesn't exist
                List<String> newAddresses = new ArrayList<>(fireStationEntity.getAddresses());
                newAddresses.add(fireStationsDto.getAddress());
                fireStationEntity.setAddresses(newAddresses);
                fireStationRepo.save(fireStationEntity);
            }

        } else {
            // station doesn't exist
            FireStationEntity fireStationEntity = new FireStationEntity();
            fireStationEntity.setStation(fireStationsDto.getStation());
            fireStationEntity.setAddresses(Collections.singletonList(fireStationsDto.getAddress()));
            fireStationRepo.save(fireStationEntity);
        }
    }

    public void updateFireStationMapping(FireStationsDto fireStationsDto) {

        Optional<FireStationEntity> optionalFireStationEntity = fireStationRepo.findByAddresses(fireStationsDto.getAddress());

        if (optionalFireStationEntity.isPresent()) {
            // address exists

            FireStationEntity fireStationEntity = optionalFireStationEntity.get();

            if (fireStationEntity.getStation() == (fireStationsDto.getStation())) {
                // address exists and is already assigned to this station
                throw new EntityExistsException();
            } else {
                // address exists so ...
                // address has to be deleted from the actual fireStation
                fireStationEntity.setAddresses(fireStationEntity.getAddresses().stream()
                        .filter(it -> !it.equals(fireStationsDto.getAddress()))
                        .collect(Collectors.toList()));
                fireStationRepo.save(fireStationEntity);

                // and has to be assigned to the new one (almost same as create)
                createFireStationMapping(fireStationsDto);

                // fireStation's persons have also to be updated
                List<Person> personsByAddress = personService.getPersonsByAddress(fireStationsDto.getAddress());
                personService.saveAllPersons(personsByAddress.stream()
                        .peek(it -> it.setFireStation(fireStationsDto.getStation()))
                        .collect(Collectors.toList()));
            }
        } else {
            // address doesn't exist
            throw new NoSuchElementException();
        }
    }

    public void deleteFireStationMapping(int station, String address) {

        Optional<FireStationEntity> optionalFireStationEntity = fireStationRepo.findByAddresses(address);

        if (optionalFireStationEntity.isPresent()) {
            // address exists

            FireStationEntity fireStationEntity = optionalFireStationEntity.get();

            if (fireStationEntity.getStation() == (station)) {
                // address exists and is assigned to this station so...
                // address has to be deleted from this station
                List<String> newAddresses = fireStationEntity.getAddresses().stream()
                        .filter(it -> !it.equals(address))
                        .collect(Collectors.toList());
                fireStationEntity.setAddresses(newAddresses);
                fireStationRepo.save(fireStationEntity);

                // and fireStation's persons has to be initialized
                List<Person> personsByAddress = personService.getPersonsByAddress(address);
                personService.saveAllPersons(personsByAddress.stream()
                        .peek(it -> it.setFireStation(0))
                        .collect(Collectors.toList()));

            } else {
                // address exists but is not assigned to this station
                throw new NoSuchElementException();
            }

        } else {
            //address doesn't exist
            throw new NoSuchElementException();
        }
    }

    public void saveAllFireStationEntities(List<FireStationEntity> fireStationEntities) {
        fireStationRepo.saveAll(fireStationEntities);
    }


}
