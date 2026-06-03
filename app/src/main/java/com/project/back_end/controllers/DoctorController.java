package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    @Autowired
    DoctorService doctorService;

    @Autowired
    Service service;

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(@PathVariable("user") String user,
                                                   @PathVariable("doctorId") Long doctorId,
                                                   @PathVariable("date") String date,
                                                   @PathVariable("token") String token) {
        // Validate the token first
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, user);
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        // Convert the string date into LocalDate
        LocalDate parsedDate = LocalDate.parse(date);
        List<LocalTime> availableSlots = doctorService.getDoctorAvailability(doctorId, parsedDate);

        Map<String, Object> response = new HashMap<>();
        response.put("availability", availableSlots);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        List<Doctor> doctors = doctorService.getDoctors();
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{token}")
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor,
                                          @PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if(tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        int result = doctorService.saveDoctor(doctor);
        switch(result) {
            case 1:
                return ResponseEntity.status(HttpStatus.CREATED).body("Doctor added to db");
            case -1:
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Doctor already exists");
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some internal error occurred");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login.getIdentifier(), login.getPassword());
    }

    @PutMapping("/{token}")
    public ResponseEntity<?> updateDoctor(@RequestBody Doctor doctor,
                                          @PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if(tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        int result = doctorService.saveDoctor(doctor.getId(), doctor);
        switch (result) {
            case 1:
                return ResponseEntity.ok("Doctor updated");
            case -1:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found");
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some internal error occurred");
        }
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> deleteDoctor(@PathVariable("id") Long id,
                                          @PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "admin");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        int result = doctorService.deleteDoctor(id);
        switch (result) {
            case 1:
                return ResponseEntity.ok("Doctor deleted successfully");
            case -1:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found with id");
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some internal error occurred");
        }
    }

    @GetMapping("/filter/{name}/{time}/{specialty}")
    public ResponseEntity<Map<String, Object>> filterDoctors(@PathVariable("name") String name,
                                                             @PathVariable("time") String time,
                                                             @PathVariable("specialty") String specialty) {
        String filterName = "null".equalsIgnoreCase(name) ? null : name;
        String filterTime = "null".equalsIgnoreCase(time) ? null : time;
        String filterSpecialty = "null".equalsIgnoreCase(specialty) ? null : specialty;

        Map<String, Object> filteredData = service.filterDoctor(filterName, filterSpecialty, filterTime);
        return ResponseEntity.ok(filteredData);
    }

// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.


}
