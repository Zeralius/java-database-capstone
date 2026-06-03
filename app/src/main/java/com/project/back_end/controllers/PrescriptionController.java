package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {
    

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private Service service;


    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(@PathVariable("token") String token,
                                              @RequestBody Prescription prescription) {

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        return prescriptionService.savePrescription(prescription.getAppointmentId());
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescriptionByAppointmentId(@PathVariable("appointmentId") Long appointmentId,
                                                            @PathVariable("token") String token) {

        ResponseEntity<Map<String, String>> tokenValidation = service.validateToken(token, "doctor");
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }


        return prescriptionService.getPrescription(appointmentId);
    }

}
