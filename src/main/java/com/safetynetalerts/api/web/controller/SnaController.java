package com.safetynetalerts.api.web.controller;

import com.safetynetalerts.api.domain.model.AlertedChildrenAndAdults;
import com.safetynetalerts.api.domain.model.CoveredPersonsAndStats;
import com.safetynetalerts.api.domain.service.SnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnaController {

    Logger LOGGER = LoggerFactory.getLogger(SnaController.class);

    @Autowired
    private SnaService snaService;

    @GetMapping("/firestation")
    public CoveredPersonsAndStats getCoveredPersonsAndStatsByFireStation(@RequestParam int stationNumber) {
        LOGGER.info("request to get Covered Persons And Stats By FireStation : " + stationNumber);
        return snaService.getCoveredPersonsAndStatsByFireStation(stationNumber);
        //TODO : add wrong cases and log ?
    }

    @GetMapping("/childAlert")
    public AlertedChildrenAndAdults getAlertedChildrenAndAdultsByAddress(@RequestParam String address) {
        LOGGER.info("request to get Alerted Children And Adults By Address : " + address);
        return snaService.getAlertedChildrenAndAdultsByAddress(address);
        //TODO : add wrong cases and log ?
    }

}
