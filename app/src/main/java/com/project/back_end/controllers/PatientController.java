package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {


    @Autowired
    private PatientService patientService;

    @Autowired
    private Service service;

    @GetMapping("/{token}")
    public ResponseEntity<?> getPatientDetails(@PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if(tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        return patientService.getPatientDetails(token);
    }

    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody Patient patient) {
        boolean isUnique = service.validatePatient(patient);
        if(!isUnique) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Patient with email id or phone already exists.");
        }

        int result = patientService.createPatient(patient);
        if(result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Signup successful.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    @GetMapping("/appointment/{id}/{role}/{token}")
    public ResponseEntity<?> getPatientAppointment(@PathVariable("id") Long patientId,
                                                   @PathVariable("role") String role,
                                                   @PathVariable("token") String token) {

        // 1. Validates the token using the shared service and matching user role
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, role);
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            // Returns validation error if invalid or expired
            return tokenValidation;
        }

        // 2. If valid, retrieves the patient's appointment data from PatientService
        return patientService.getPatientAppointment(patientId);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointments(@PathVariable("condition") String condition,
                                                       @PathVariable("name") String name,
                                                       @PathVariable("token") String token) {

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        String filterCondition = "null".equalsIgnoreCase(condition) ? null : condition;
        String filterName = "null".equalsIgnoreCase(name) ? null : name;

        return service.filterPatient(filterCondition, filterName, token);
    }

}


