package com.safetynetalerts.api.web.controller;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.domain.service.PersonService;
import com.safetynetalerts.api.web.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class FunctionalController {

    @Autowired
    private PersonService personService;

    @GetMapping("/firestation")
    public FireStationDto getFireStationDto(@RequestParam int stationNumber) {
        log.info("request to get FireStationDto of station : {}", stationNumber);

        FireStationDto fireStationDto = new FireStationDto();

        List<Person> personList = personService.getPersonsByStation(stationNumber);

        JMapper<FireStationPersonDto, Person> personMapper = new JMapper<>(FireStationPersonDto.class, Person.class);
        List<FireStationPersonDto> fireStationPersons = personList.stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());
        fireStationDto.setFireStationPersons(fireStationPersons);

        fireStationDto.setChildCounter(personService.getChildCounter(personList));
        fireStationDto.setAdultCounter(personService.getAdultCounter(personList));

        return fireStationDto;
    }

    @GetMapping("/childAlert")
    public ChildAlertDto getChildAlertDto(@RequestParam String address) {
        log.info("request to get ChildAlertDto of address : {}", address);

        ChildAlertDto childAlertDto = new ChildAlertDto();

        List<Person> personList = personService.getPersonsByAddress(address);

        JMapper<ChildAlertPersonDto, Person> personMapper = new JMapper<>(ChildAlertPersonDto.class, Person.class);
        List<ChildAlertPersonDto> ChildList = personService.getChildren(personList).stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());
        List<ChildAlertPersonDto> AdultList = personService.getAdults(personList).stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());

        childAlertDto.setAlertedChildren(ChildList);
        childAlertDto.setAlertedAdults(AdultList);

        return childAlertDto;
    }

    @GetMapping("/phoneAlert")
    public PhoneAlertDto getPhoneAlertDto(@RequestParam int firestation_number) {
        log.info("request to get PhoneAlertDto of station : {}", firestation_number);

        PhoneAlertDto phoneAlertDto = new PhoneAlertDto();

        List<Person> personList = personService.getPersonsByStation(firestation_number);

        List<String> phones = personList.stream()
                .map(Person::getPhone)
                .distinct()
                .collect(Collectors.toList());

        phoneAlertDto.setPhones(phones);

        return phoneAlertDto;
    }

    @GetMapping("/fire")
    public FireDto getFireDto(@RequestParam String address) {
        log.info("request to get FireDto of address : {}", address);

        FireDto fireDto = new FireDto();

        List<Person> personList = personService.getPersonsByAddress(address);

        JMapper<FirePersonDto, Person> personMapper = new JMapper<>(FirePersonDto.class, Person.class);
        List<FirePersonDto> firePersons = personList.stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());
        fireDto.setFirePersons(firePersons);

        fireDto.setFireStation(personService.getFireStation(personList));

        return fireDto;
    }

    @GetMapping("/flood/stations")
    public StationsDto getStationsDto(@RequestParam List<Integer> stationNumbers) {
        log.info("request to get StationsDto of stations : {}", stationNumbers);

        StationsDto stationsDto = new StationsDto();

        List<Person> personList = personService.getPersonsByStations(stationNumbers);

        JMapper<StationsPersonDto, Person> personMapper = new JMapper<>(StationsPersonDto.class, Person.class);
        List<StationsPersonByAddressDto> personsByAddress = personList.stream()
                .collect(Collectors.groupingBy(Person::getAddress
                        , Collectors.mapping(personMapper::getDestination,
                                Collectors.toList())))
                .entrySet().stream()
                .map(it -> {
                    StationsPersonByAddressDto stationsPersonByAddressDto = new StationsPersonByAddressDto();
                    stationsPersonByAddressDto.setAddress((it.getKey()));
                    stationsPersonByAddressDto.setStationsPersons((it.getValue()));
                    return stationsPersonByAddressDto;
                })
                .collect(Collectors.toList());

        stationsDto.setPersonsByAddress(personsByAddress);

        return stationsDto;
    }

    @GetMapping("/personInfo")
    public PersonInfoDto getPersonInfoDto(@RequestParam String firstName, @RequestParam String lastName) {
        log.info("request to get PersonInfoDto of firstName={} & lastName={}", firstName, lastName);

        PersonInfoDto personInfoDto = new PersonInfoDto();

        List<Person> personList = personService.getPersonsByFirstNameAndLastName(firstName, lastName);

        JMapper<PersonInfoPersonDto, Person> personMapper = new JMapper<>(PersonInfoPersonDto.class, Person.class);
        List<PersonInfoPersonDto> personsInfo = personList.stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());
        personInfoDto.setPersonsInfo(personsInfo);

        return personInfoDto;
    }

    @GetMapping("/communityEmail")
    public CommunityEmailDto getCommunityEmailDto(@RequestParam String city) {
        log.info("request to get CommunityEmailDto of city : {}", city);

        CommunityEmailDto communityEmailDto = new CommunityEmailDto();

        List<Person> personList = personService.getPersonsByCity(city);

        List<String> emails = personList.stream()
                .map(Person::getEmail)
                .collect(Collectors.toList());
        communityEmailDto.setEmails(emails);

        return communityEmailDto;
    }

}
