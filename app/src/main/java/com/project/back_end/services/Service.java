package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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



// 7. **validatePatient Method**
// This method checks whether a patient with the same email or phone number already exists in the system.
// - If a match is found, it returns false (indicating the patient is not valid for new registration).
// - If no match is found, it returns true.
// This helps enforce uniqueness constraints on patient records and prevent duplicate entries.

// 8. **validatePatientLogin Method**
// This method handles login validation for patient users.
// - It looks up the patient by email.
// - If found, it checks whether the provided password matches the stored one.
// - On successful validation, it generates a JWT token and returns it with a 200 OK status.
// - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
// - If an exception occurs, it returns a 500 Internal Server Error.
// This method ensures only legitimate patients can log in and access their data securely.

// 9. **filterPatient Method**
// This method filters a patient's appointment history based on condition and doctor name.
// - It extracts the email from the JWT token to identify the patient.
// - Depending on which filters (condition, doctor name) are provided, it delegates the filtering logic to PatientService.
// - If no filters are provided, it retrieves all appointments for the patient.
// This flexible method supports patient-specific querying and enhances user experience on the client side.


}
