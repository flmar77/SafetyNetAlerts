package com.safetynetalerts.api.web.controller;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.domain.model.FireStation;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.domain.service.SnaService;
import com.safetynetalerts.api.web.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class SnaController {

    @Autowired
    private SnaService snaService;

    @GetMapping("/firestation")
    public FireStationDto getFireStationDto(@RequestParam Long stationNumber) {
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
    public PhoneAlertDto getPhoneAlertDto(@RequestParam Long firestation_number) {
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

    @GetMapping("/stations")
    public StationsDto getStationsDto(@RequestParam List<Long> stationNumbers) {
        log.info("request to get StationsDto of stations : {}", stationNumbers);

        StationsDto stationsDto = new StationsDto();

        List<Person> personList = snaService.getPersonsByStations(stationNumbers);

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
                .body(personToPersonDtoMapper.getDestination(snaService.createPerson(personDto)));

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

    @GetMapping("/firestations")
    public List<FireStationsDto> getAllFireStationsDto() {
        log.info("request to get AllFireStationsDto");

        List<FireStation> fireStationList = snaService.getAllFireStations();

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
    public ResponseEntity<?> getFireStationsDto(@PathVariable Long station, @PathVariable String address) {
        log.info("request to get FireStationsDto of station={} & address={}", station, address);

        FireStationsDto fireStationsDto = new FireStationsDto();

        if (station == null || station == 0
                || address == null || address.equals("")) {
            String errorMessage = "error while getting FireStationsDto because of wrong station=" + station + " and/or address=" + address;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            FireStation fireStation = snaService.getFireStationByStationAndAddress(station, address);

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

        if (fireStationsDto.getStation() == null || fireStationsDto.getStation() == 0
                || fireStationsDto.getAddress() == null || fireStationsDto.getAddress().equals("")) {
            String errorMessage = "error while posting FireStationsDto because of wrong input : " + fireStationsDto;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            snaService.createFireStationMapping(fireStationsDto);
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
    public ResponseEntity<?> updateFireStationsDto(@PathVariable Long station, @PathVariable String address, @RequestBody FireStationsDto fireStationsDto) {
        log.info("request to put FireStationsDto : {}", fireStationsDto);

        if (station == null || station == 0
                || address == null || address.equals("")) {
            String errorMessage = "error while putting FireStationsDto because of wrong station=" + station + " and/or address" + address;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            snaService.updateFireStationMapping(fireStationsDto);
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
    public ResponseEntity<?> deleteFireStationsDto(@PathVariable Long station, @PathVariable String address) {
        log.info("request to delete mapping of address={} with station={}", address, station);

        if (station == null || station == 0
                || address == null || address.equals("")) {
            String errorMessage = "error while deleting FireStationsDto because of wrong station=" + station + " and/or address=" + address;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            snaService.deleteFireStationMapping(station, address);
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
