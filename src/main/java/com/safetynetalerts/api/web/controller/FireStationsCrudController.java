package com.safetynetalerts.api.web.controller;

import com.safetynetalerts.api.domain.model.FireStation;
import com.safetynetalerts.api.domain.service.FireStationService;
import com.safetynetalerts.api.web.dto.FireStationsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
public class FireStationsCrudController {

    @Autowired
    private FireStationService fireStationService;

    @GetMapping("/firestations")
    public List<FireStationsDto> getAllFireStationsDto() {
        log.info("request to get AllFireStationsDto");

        List<FireStation> fireStationList = fireStationService.getAllFireStations();

        List<FireStationsDto> fireStationsDtoList = new ArrayList<>();

        for (FireStation fireStation : fireStationList) {
            for (String address : fireStation.getAddresses()) {
                FireStationsDto fireStationsDto = new FireStationsDto();
                fireStationsDto.setStation(fireStation.getStation());
                fireStationsDto.setAddress(address);
                fireStationsDtoList.add(fireStationsDto);
            }
        }
        return fireStationsDtoList;
    }

    @GetMapping("/firestations/{station}&{address}")
    public ResponseEntity<?> getFireStationsDto(@PathVariable int station, @PathVariable String address) {
        log.info("request to get FireStationsDto of station={} & address={}", station, address);

        FireStationsDto fireStationsDto = new FireStationsDto();

        if (station == 0
                || address == null || address.equals("")) {
            String errorMessage = "error while getting FireStationsDto because of wrong station=" + station + " and/or address=" + address;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            FireStation fireStation = fireStationService.getFireStationByStationAndAddress(station, address);

            fireStationsDto.setStation(fireStation.getStation());
            fireStationsDto.setAddress(fireStation.getAddresses().get(0));

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(fireStationsDto);

        } catch (NoSuchElementException e) {
            String errorMessage = "error while getting FireStationsDto because of non existing fireStation with station=" + station + " & address=" + address;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorMessage);
        }
    }

    @PostMapping("/firestations")
    public ResponseEntity<?> createFireStationsDto(@RequestBody FireStationsDto fireStationsDto) {
        log.info("request to post FireStationsDto : {}", fireStationsDto);

        if (fireStationsDto.getStation() == 0
                || fireStationsDto.getAddress() == null || fireStationsDto.getAddress().equals("")) {
            String errorMessage = "error while posting FireStationsDto because of wrong input : " + fireStationsDto;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            fireStationService.createFireStationMapping(fireStationsDto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(fireStationsDto);
        } catch (EntityExistsException e) {
            String errorMessage = "error while posting FireStationsDto because of existing fireStationsDto : " + fireStationsDto;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

    }

    @PutMapping("/firestations/{station}&{address}")
    public ResponseEntity<?> updateFireStationsDto(@PathVariable int station, @PathVariable String address, @RequestBody FireStationsDto fireStationsDto) {
        log.info("request to put FireStationsDto : {}", fireStationsDto);

        if (station == 0
                || address == null || address.equals("")) {
            String errorMessage = "error while putting FireStationsDto because of wrong station=" + station + " and/or address" + address;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            fireStationService.updateFireStationMapping(fireStationsDto);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(fireStationsDto);
        } catch (EntityExistsException e) {
            String errorMessage = "error while putting FireStationsDto because address=" + address + " already assigned to station=" + station;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        } catch (NoSuchElementException e) {
            String errorMessage = "error while putting FireStationsDto because of non existing address : " + address;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorMessage);
        }
    }

    @DeleteMapping("/firestations/{station}&{address}")
    public ResponseEntity<?> deleteFireStationsDto(@PathVariable int station, @PathVariable String address) {
        log.info("request to delete mapping of address={} with station={}", address, station);

        if (station == 0
                || address == null || address.equals("")) {
            String errorMessage = "error while deleting FireStationsDto because of wrong station=" + station + " and/or address=" + address;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            fireStationService.deleteFireStationMapping(station, address);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("successfully delete");
        } catch (NoSuchElementException e) {
            String errorMessage = "error while deleting FireStationsDto because of non existing mapping of station=" + station + " and address=" + address;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorMessage);
        }
    }


}
