package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService, AdminRepository adminRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, DoctorService doctorService, PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        boolean isValid = tokenService.validateToken(token, user);

        if(!isValid) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Unauthorized: Token is invalid or expired.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMap);
        }

        Map<String, String> successMap = new HashMap<>();
        successMap.put("Status", "Valid");
        return ResponseEntity.ok(successMap);
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> responseMap = new HashMap<>();

        if(receivedAdmin == null) {
            responseMap.put("error", "Invalid admin payload.");
            return ResponseEntity.badRequest().body(responseMap);
        }

        Admin existingAdmin = adminRepository.findByUsername(receivedAdmin.getUsername());
        if(existingAdmin != null && existingAdmin.getPassword().equals(receivedAdmin.getPassword())) {
            String token = tokenService.generateToken(existingAdmin.getUsername());
            responseMap.put("token", token);
            return ResponseEntity.ok(responseMap);
        }

        responseMap.put("error", "Invalid username or password.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> filteredDoctors;

        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasSpecialty = specialty != null && !specialty.trim().isEmpty();
        boolean hasTime = time != null && !time.trim().isEmpty();

        if(hasName && hasSpecialty && hasTime) {
            filteredDoctors = doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } else if(hasName && hasTime) {
            filteredDoctors = doctorService.filterDoctorByNameAndTime(name, time);
        } else if(hasSpecialty && hasTime) {
            filteredDoctors = doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        } else if(hasName && hasSpecialty) {
            filteredDoctors = doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if(hasSpecialty) {
            filteredDoctors = doctorService.filterDoctorBySpecility(specialty);
        } else if(hasTime) {
            filteredDoctors = doctorService.filterDoctorsByTime(time);
        } else if(hasName) {
            filteredDoctors = doctorService.findByDoctorName(name);
        } else {
            filteredDoctors = doctorService.getDoctors();
        }

        result.put("doctors", filteredDoctors);
        return result;
    }

    public int validateAppointment(Appointment appointment) {
        if(appointment == null || appointment.getDoctor().getId() == null || appointment.getAppointmentTime() == null) {
            return 0;
        }

        Optional<Doctor> doctorOptional = doctorRepository.findById(appointment.getDoctor().getId());
        if(doctorOptional.isEmpty()) {
            return -1;
        }

        List<LocalTime> availableSlots = doctorService.getDoctorAvailability(
                appointment.getDoctor().getId(),
                appointment.getAppointmentTime().toLocalDate()
        );

        LocalTime appointmentTimeOnly = appointment.getAppointmentTime().toLocalTime();

        if(availableSlots.contains(appointmentTimeOnly)) {
            return 1;
        }

        return 0;
    }

    public boolean validatePatient(Patient patient) {
        if (patient == null) {
            return false;
        }
        Optional<Patient> existingPatient = Optional.ofNullable(patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()));
        return existingPatient.isEmpty();
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> responseMap = new HashMap<>();

        if(login == null || login.getIdentifier() == null) {
            responseMap.put("error", "Malformed login structure.");
            return ResponseEntity.badRequest().body(responseMap);
        }

        try {
            Optional<Patient> patientOptional = Optional.ofNullable(patientRepository.findByEmail(login.getIdentifier()));
            if(patientOptional.isPresent() && patientOptional.get().getPassword().equals(login.getPassword())) {
                String token = tokenService.generateToken(login.getIdentifier());
                responseMap.put("token", token);
                return ResponseEntity.ok(responseMap);
            }

            responseMap.put("error", "Invalid email or password.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        } catch (Exception e) {
            responseMap.clear();
            responseMap.put("error", "Internal Server Error: An unexpected errror occured while processing user login.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }


    }

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String doctorName, String token) {
        Map<String, Object> response = new HashMap<>();

        if(!tokenService.validateToken(token,"patient")) {
            response.put("error", "Unauthorized token access.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String email = tokenService.extractEmail(token);
        Optional<Patient> patientOptional = Optional.ofNullable(patientRepository.findByEmail(email));
        if(patientOptional.isEmpty()) {
            response.put("error", "Patient identity context not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Long patientId = patientOptional.get().getId();
        ResponseEntity<?> serviceResponse;

        boolean hasCondition = condition != null && !condition.trim().isEmpty();
        boolean hasDoctorName = doctorName != null & !doctorName.trim().isEmpty();

        if(hasCondition && hasDoctorName) {
            serviceResponse = patientService.filterByDoctorAndCondition(patientId, doctorName, condition);
        } else if(hasCondition) {
            serviceResponse = patientService.filterByCondition(patientId, condition);
        } else if(hasDoctorName) {
            serviceResponse = patientService.filterByDoctor(patientId, doctorName);
        } else {
            serviceResponse = patientService.getPatientAppointment(patientId);
        }
        response.put("appointments", serviceResponse.getBody());
        return ResponseEntity.status(serviceResponse.getStatusCode()).body(response);
    }

}
