package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private Service service;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    //If the save operation fails, it returns 0 otherwise, it returns 1.
    @Transactional
    public int bookAppointment(Appointment appointment) {
        if(appointment == null) {
            return 0;
        }

        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<?> updateAppointment(Long appointmentId, Appointment updatedAppointment) {
        try {
            Optional<Appointment> existingAppt = appointmentRepository.findById(appointmentId);
            if(existingAppt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found.");
            }

            Appointment existingAppointment = existingAppt.get();

            if(!existingAppointment.getPatient().getId().equals(updatedAppointment.getPatient().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized: Patient ID mismatch.");
            }

            LocalDateTime targetTime = updatedAppointment.getAppointmentTime();
            Long doctorId = updatedAppointment.getDoctor().getId();

            boolean isDoctorFree = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId,targetTime, targetTime.plusHours(1)).isEmpty();
            if(!isDoctorFree) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("The doctor is not available at the specific time.");
            }

            existingAppointment.setAppointmentTime(updatedAppointment.getAppointmentTime());
            appointmentRepository.save(existingAppointment);
            return ResponseEntity.ok("Appointment updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error during appointment update.");
        }
    }

    @Transactional
    public ResponseEntity<?> cancelAppointment(Long appointmentId, Long patientId) {
        try {
            Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
            if(optionalAppointment.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found.");
            }
            Appointment existingAppointment = optionalAppointment.get();

            if(!existingAppointment.getPatient().getId().equals(patientId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient ID not matching.");
            }
            appointmentRepository.delete(existingAppointment);
            return ResponseEntity.ok("Appointment successfully deleted.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error during appointment deletion");
        }
    }

    @Transactional
    public ResponseEntity<?> getAppointments(String doctorName, LocalDate dateString, String patientName) {
        try {
            List<Appointment> appointments;
            if(patientName != null && !patientName.trim().isEmpty()) {
                appointments = appointmentRepository.filterByDoctorNameAndPatientId(doctorName, patientRepository.findByEmail(patientName).getId());
            } else {
                appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorRepository.findByEmail(doctorName).getId(), dateString.atStartOfDay(), dateString.atStartOfDay().plusHours(24));
            }
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error while fetching appointments");
        }
    }

    @Transactional
    public ResponseEntity<?> changeStatus(Long appointmentId, Appointment updatedAppointment) {
        try {
            Optional<Appointment> existingAppt = appointmentRepository.findById(appointmentId);
            if(existingAppt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found.");
            }

            Appointment existingAppointment = existingAppt.get();

            if(!existingAppointment.getPatient().getId().equals(updatedAppointment.getPatient().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized: Patient ID mismatch.");
            }


            existingAppointment.setStatus(updatedAppointment.getStatus());
            appointmentRepository.save(existingAppointment);
            return ResponseEntity.ok("Appointment status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error during appointment status update.");
        }
    }

}
