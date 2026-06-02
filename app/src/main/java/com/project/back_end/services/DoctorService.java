package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        try {
            List<LocalTime> slots = new ArrayList<>();
            for(int hour = 9; hour <= 17; hour++) {
                slots.add(LocalTime.of(hour,0));
            }

            List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, date.atStartOfDay(), date.atStartOfDay().plusHours(24));

            Set<String> bookedTimes = bookedAppointments.stream()
                    .map(appt -> appt.getAppointmentTime().toLocalTime().toString())
                    .collect(Collectors.toSet());

            return slots.stream()
                    .filter(slot -> !bookedTimes.contains(slot))
                    .map(slot -> slot.toString())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if(doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public int saveDoctor(Long doctorId, Doctor updatedDoctor) {
        try {
            Optional<Doctor> existingDoctor = doctorRepository.findById(doctorId);
            if(existingDoctor.isEmpty()) {
                return -1;
            }
            Doctor doctor = existingDoctor.get();
            doctor.setName(updatedDoctor.getName());
            doctor.setEmail(updatedDoctor.getEmail());
            doctor.setSpecialty(updatedDoctor.getSpecialty());

            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        doctors.forEach(doc -> {
            if(doc.getAvailableTimes() != null) doc.getAvailableTimes().size();
        });
        return doctors;
    }

    @Transactional
    public int deleteDoctor(Long doctorId) {
        try {
            if(!doctorRepository.existsById(doctorId)) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(doctorId);
            doctorRepository.deleteById(doctorId);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<?> validateDoctor(String email, String password) {
        try {
            Doctor doctor = doctorRepository.findByEmail(email);
            if(doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor profile not found.");
            }
            // Encryption/Decryption in production or real projects most likely happening here.
            if(!doctor.getPassword().equals(password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Password.");
            }

            String token = tokenService.generateToken(email);
            Map<String, String> payload = new HashMap<>();
            payload.put("token", token);
            payload.put("message", "Login successful. ");
            return ResponseEntity.ok(payload);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server error while validating doctor.");
        }
    }

    @Transactional
    public List<Doctor> findByDoctorName(String name) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        doctors.forEach(doc -> {
            if(doc.getAvailableTimes()!=null) doc.getAvailableTimes().size();
        });
        return doctors;
    }

    @Transactional
    public List<Doctor> filterDoctorsByNameSpecilityandTime(String name, String specialty, String timePeriod) {
        List<Doctor> matchedDoctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        return filterDoctorByTime(matchedDoctors, timePeriod);
    }

    @Transactional
    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String timePeriod) {
        if(timePeriod == null || (!timePeriod.equalsIgnoreCase("AM") && !timePeriod.equalsIgnoreCase("PM"))) {
            return Collections.emptyList();
        }

        return doctors.stream()
                .filter(doc -> doc.getAvailableTimes().stream().anyMatch(time -> isTimeInPeriod(LocalTime.parse(time), timePeriod)))
                .collect(Collectors.toList());
    }

    private boolean isTimeInPeriod(LocalTime time, String period) {
        if ("AM".equalsIgnoreCase(period)) {
            return time.isBefore(LocalTime.NOON);
        } else {
            return !time.isBefore(LocalTime.NOON); // Noon or later is PM
        }
    }

    @Transactional
    public List<Doctor> filterDoctorByNameAndTime(String name, String timePeriod) {
        List<Doctor> matched = doctorRepository.findByNameLike(name);
        return filterDoctorByTime(matched, timePeriod);
    }

    @Transactional
    public List<Doctor> filterDoctorByNameAndSpecility(String name, String specialty) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
    }

    @Transactional
    public List<Doctor> filterDoctorByTimeAndSpecility(String specialty, String timePeriod) {
        List<Doctor> matched = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        return filterDoctorByTime(matched, timePeriod);
    }

    @Transactional
    public List<Doctor> filterDoctorBySpecility(String specialty) {
        return doctorRepository.findBySpecialtyIgnoreCase(specialty);
    }

    @Transactional
    public List<Doctor> filterDoctorsByTime(String timePeriod) {
        return filterDoctorByTime(getDoctors(), timePeriod);
    }
   
}
