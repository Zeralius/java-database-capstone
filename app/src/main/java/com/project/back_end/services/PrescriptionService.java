package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    private static final Logger LOGGER = Logger.getLogger(PrescriptionService.class.getName());

    public ResponseEntity<?> savePrescription(Long appointmentId) {
        try {
            if(!prescriptionRepository.findByAppointmentId(appointmentId).isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Prescription already exists for this appointment.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            Prescription prescription = new Prescription();
            prescription.setAppointmentId(appointmentId);

            prescriptionRepository.save(prescription);

            Map<String, String> response = new HashMap<>();
            response.put("meesage", "Prescription created successfully.");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving prescription for appointment ID: " + appointmentId, e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server error occured while saving the Prescription.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getPrescription(Long appointmentId) {
        try {
            List<Prescription> prescriptionList = prescriptionRepository.findByAppointmentId(appointmentId);

            if(!prescriptionList.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("prescription", prescriptionList);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("message", "No prescription found for the given appointment");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching prescription for appointment ID: " + appointmentId, e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error occured while fetching the prescription.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
