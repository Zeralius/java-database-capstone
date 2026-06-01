package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    private static final Logger LOGGER = Logger.getLogger(PatientService.class.getName());

    public PatientService(PatientRepository patientRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while saving Patient: ", e);
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<?> getPatientAppointment(Long patientId) {
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            List<AppointmentDTO> dtos = appointments.stream()
                    .map(appt -> new AppointmentDTO(
                            appt.getId(),
                            appt.getDoctor().getId(),
                            appt.getDoctor().getName(),
                            appt.getPatient().getId(),
                            appt.getPatient().getName(),
                            appt.getPatient().getEmail(),
                            appt.getPatient().getPhone(),
                            appt.getPatient().getAddress(),
                            appt.getAppointmentTime(),
                            appt.getStatus()
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body("fetching appointments");
        }
    }

    @Transactional
    public ResponseEntity<?> filterByCondition(Long patientId, String condition) {
        try {
            Integer status;

            if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else {
                return ResponseEntity.internalServerError().body("Invalid condition. Use 'past' or 'future'");
            }

            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            List<AppointmentDTO> dtos = appointments.stream()
                    .filter(app -> app.getStatus() == status)
                    .map(appt -> new AppointmentDTO(
                            appt.getId(),
                            appt.getDoctor().getId(),
                            appt.getDoctor().getName(),
                            appt.getPatient().getId(),
                            appt.getPatient().getName(),
                            appt.getPatient().getEmail(),
                            appt.getPatient().getPhone(),
                            appt.getPatient().getAddress(),
                            appt.getAppointmentTime(),
                            appt.getStatus()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("filtering appointments by condition");
        }
    }

    @Transactional
    public ResponseEntity<?> filterByDoctor(Long patientId, String doctorName) {
        try {
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(doctorName, patientId);
            List<AppointmentDTO> dtos = appointments.stream()
                    .map(appt -> new AppointmentDTO(
                            appt.getId(),
                            appt.getDoctor().getId(),
                            appt.getDoctor().getName(),
                            appt.getPatient().getId(),
                            appt.getPatient().getName(),
                            appt.getPatient().getEmail(),
                            appt.getPatient().getPhone(),
                            appt.getPatient().getAddress(),
                            appt.getAppointmentTime(),
                            appt.getStatus()
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("filtering appointments by doctor");
        }
    }

    @Transactional
    public ResponseEntity<?> filterByDoctorAndCondition(Long patientId, String doctorName, String condition) {

        int status;
        try {
            if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else {
                return ResponseEntity.internalServerError().body("Invalid condition. Use 'past' or 'future'");
            }
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status);
            List<AppointmentDTO> dtos = appointments.stream()
                    .map(appt -> new AppointmentDTO(
                            appt.getId(),
                            appt.getDoctor().getId(),
                            appt.getDoctor().getName(),
                            appt.getPatient().getId(),
                            appt.getPatient().getName(),
                            appt.getPatient().getEmail(),
                            appt.getPatient().getPhone(),
                            appt.getPatient().getAddress(),
                            appt.getAppointmentTime(),
                            appt.getStatus()
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("filtering appointments by doctor and condition");
        }
    }

    public ResponseEntity<?> getPatientDetails(String token) {
        try {
            String email = tokenService.extractEmail(token);
            if(email == null || email.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }

            Optional<Patient> patientOptional = Optional.ofNullable(patientRepository.findByEmail(email));
            if(patientOptional.isPresent()) {
                return ResponseEntity.ok(patientOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("retrieving patient details via token");
        }
    }


}
