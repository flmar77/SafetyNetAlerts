package com.safetynetalerts.api.web.controller;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.domain.service.SnaService;
import com.safetynetalerts.api.web.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class SnaController {

    @Autowired
    private SnaService snaService;

    @GetMapping("/firestation")
    public FireStationDto getFireStationDto(@RequestParam int stationNumber) {
        log.info("request to get FireStationDto of station : {}", stationNumber);

        FireStationDto fireStationDto = new FireStationDto();

        List<Person> personList = snaService.getPersonsByStation(stationNumber);

        JMapper<FireStationPersonDto, Person> personMapper = new JMapper<>(FireStationPersonDto.class, Person.class);
        List<FireStationPersonDto> fireStationPersons = personList.stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());
        fireStationDto.setFireStationPersons(fireStationPersons);

        fireStationDto.setChildCounter(snaService.getChildCounter(personList));
        fireStationDto.setAdultCounter(snaService.getAdultCounter(personList));

        return fireStationDto;
    }

    @GetMapping("/childAlert")
    public ChildAlertDto getChildAlertDto(@RequestParam String address) {
        log.info("request to get ChildAlertDto of address : {}", address);

        ChildAlertDto childAlertDto = new ChildAlertDto();

        List<Person> personList = snaService.getPersonsByAddress(address);

        JMapper<ChildAlertPersonDto, Person> personMapper = new JMapper<>(ChildAlertPersonDto.class, Person.class);
        List<ChildAlertPersonDto> ChildList = snaService.getChildren(personList).stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());
        List<ChildAlertPersonDto> AdultList = snaService.getAdults(personList).stream()
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

        List<Person> personList = snaService.getPersonsByStation(firestation_number);

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

        List<Person> personList = snaService.getPersonsByAddress(address);

        JMapper<FirePersonDto, Person> personMapper = new JMapper<>(FirePersonDto.class, Person.class);
        List<FirePersonDto> firePersons = personList.stream()
                .map(personMapper::getDestination)
                .collect(Collectors.toList());
        fireDto.setFirePersons(firePersons);

        fireDto.setFireStation(snaService.getFireStation(personList));

        return fireDto;
    }

    // TODO get by List<Station> (personDao.findAllByFireStationIn()) avec flatmap pour écraser les différentes listes récupérées
    @GetMapping("/stations")
    public StationsDto getStationsDto(@RequestParam int stationNumber) {
        log.info("request to get StationsDto of station : {}", stationNumber);

        StationsDto stationsDto = new StationsDto();

        List<Person> personList = snaService.getPersonsByStation(stationNumber);

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

        List<Person> personList = snaService.getPersonsByFirstNameAndLastName(firstName, lastName);

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

        List<Person> personList = snaService.getPersonsByCity(city);

        List<String> emails = personList.stream()
                .map(Person::getEmail)
                .collect(Collectors.toList());
        communityEmailDto.setEmails(emails);

        return communityEmailDto;
    }

    @GetMapping("/persons")
    public List<PersonDto> getAllPersonsDto() {
        log.info("request to get AllPersonsDto");

        List<Person> personList = snaService.getAllPersons();

        return mapPersonsToPersonsDto(personList);
    }

    @GetMapping("/persons/{firstName}&{lastName}")
    public List<PersonDto> getPersonsDto(@PathVariable String firstName, @PathVariable String lastName) {
        log.info("request to get PersonsDto of firstName={} & lastName={}", firstName, lastName);

        List<Person> personList = snaService.getPersonsByFirstNameAndLastName(firstName, lastName);

        return mapPersonsToPersonsDto(personList);
    }

    @PostMapping("/persons")
    public ResponseEntity<?> createPersonDto(@RequestBody PersonDto personDto) {
        log.info("request to post PersonsDto : {}", personDto);

        if (wrongPersonDtoInput(personDto)) {
            String errorMessage = "error while posting PersonDto because of wrong input : " + personDto;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        if (snaService.personAlreadyExists(personDto.getFirstName(), personDto.getLastName())) {
            String errorMessage = "error while posting PersonDto because of existing person with firstName=" + personDto.getFirstName() + " & lastName=" + personDto.getLastName();
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        JMapper<PersonDto, Person> personToPersonDtoMapper = new JMapper<>(PersonDto.class, Person.class);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(personToPersonDtoMapper.getDestination(snaService.savePerson(personDto)));

    }

    @PutMapping("/persons/{firstName}&{lastName}")
    public ResponseEntity<?> updatePersonDto(@PathVariable String firstName, @PathVariable String lastName, @RequestBody PersonDto personDto) {
        log.info("request to put PersonsDto : {}", personDto);

        if (wrongNamesDtoInput(firstName, lastName)) {
            String errorMessage = "error while putting PersonDto because of wrong firstName=" + firstName + " and/or lastName=" + lastName;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            Person person = snaService.updatePerson(firstName, lastName, personDto);
            JMapper<PersonDto, Person> personToPersonDtoMapper = new JMapper<>(PersonDto.class, Person.class);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(personToPersonDtoMapper.getDestination(person));
        } catch (NoSuchElementException e) {
            String errorMessage = "error while putting PersonDto because of non existing person with firstName=" + firstName + " & lastName=" + lastName;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorMessage);
        }
    }

    @DeleteMapping("/persons/{firstName}&{lastName}")
    public ResponseEntity<String> deletePersonDto(@PathVariable String firstName, @PathVariable String lastName) {
        log.info("request to delete PersonsDto");

        if (wrongNamesDtoInput(firstName, lastName)) {
            String errorMessage = "error while deleting PersonDto because of wrong firstName=" + firstName + " and/or lastName=" + lastName;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            snaService.deletePerson(firstName, lastName);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("successfully delete");
        } catch (NoSuchElementException e) {
            String errorMessage = "error while deleting PersonDto because of non existing person with firstName=" + firstName + " & lastName=" + lastName;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorMessage);
        }
    }

    private List<PersonDto> mapPersonsToPersonsDto(List<Person> personList) {
        JMapper<PersonDto, Person> personToPersonDtoMapper = new JMapper<>(PersonDto.class, Person.class);
        return personList.stream()
                .map(personToPersonDtoMapper::getDestination)
                .collect(Collectors.toList());
    }

    private boolean wrongPersonDtoInput(PersonDto personDto) {
        return wrongNamesDtoInput(personDto.getFirstName(), personDto.getLastName());
    }

    private boolean wrongNamesDtoInput(String firstName, String lastName) {
        return firstName == null || firstName.equals("")
                || lastName == null || lastName.equals("");
    }
}
