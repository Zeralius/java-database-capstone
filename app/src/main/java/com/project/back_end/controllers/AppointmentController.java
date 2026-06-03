package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private Service service;

    @Autowired
    private TokenService tokenService;

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable("date") String date,
                                             @PathVariable("patientName") String patientName,
                                             @PathVariable("token") String token) {

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if(tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }
        return appointmentService.getAppointments(tokenService.extractEmail(token), LocalDate.parse(date), patientName);
    }


    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(@RequestBody Appointment appointment,
                                             @PathVariable("token") String token) {
        ResponseEntity<Map<String,String>> tokenValidation = service.validateToken(token, "patient");
        if(tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        int validationCode = service.validateAppointment(appointment);
        if(validationCode == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor doesn't exist.");
        } else if(validationCode == 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The requested time slot is unavailable or already taken.");
        }

        int result = appointmentService.bookAppointment(appointment);
        if(result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Appointment booked successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process booking.");
        }
    }


    @PutMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> updateAppointment(@PathVariable("appointmentId") Long appointmentId,
                                               @RequestBody Appointment appointment,
                                               @PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if(tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        return appointmentService.updateAppointment(appointmentId, appointment);
    }

    @DeleteMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> cancelAppointment(@PathVariable("appointmentId") Long appointmentId,
                                               @PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "patient");
        if(tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        return appointmentService.cancelAppointment(appointmentId, null);
    }

// 6. Define the `cancelAppointment` Method:
//    - Handles HTTP DELETE requests to cancel a specific appointment.
//    - Accepts the appointment ID and a token as path variables.
//    - Validates the token for `"patient"` role to ensure the user is authorized to cancel the appointment.
//    - Calls `AppointmentService` to handle the cancellation process and returns the result.


}
