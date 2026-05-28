# Clinic Management System - Database Schema Design

This document outlines the hybrid database architecture for the Smart Clinic Management System, utilizing **MySQL** for structured, transactional operational data and **MongoDB** for flexible, semi-structured data.

---

## MySQL Database Design

For the core operational data, a relational structure ensures data integrity, strict typing, and enforceable constraints. 

### Referential Integrity & Business Logic Rules:
* **On Delete Cascade vs. Restrict:** If a patient or doctor profile is deleted, their associated appointments are **not** immediately hard-deleted (`ON DELETE RESTRICT` or handled via soft-delete fields) to preserve historical medical and financial records.
* **Overlapping Appointments:** Enforced via application-level logic (e.g., checking `appointment_time` against the doctor's existing schedule before inserting).
* **Data Validation:** Email formats and phone number structures are given appropriate `VARCHAR` lengths and will be strictly validated via application code regex before persistence.

---

### Table: admin
Stores administrative personnel responsible for managing schedules, clinic locations, and system configurations.
- `id`: INT, Primary Key, Auto Increment
- `username`: VARCHAR(50), Not Null, Unique
- `password_hash`: VARCHAR(255), Not Null (Handled via BCrypt/Argon2)
- `email`: VARCHAR(100), Not Null, Unique
- `created_at`: TIMESTAMP, Default Current_Timestamp

### Table: patients
Stores core personal and contact info for registered individuals receiving care.
- `id`: INT, Primary Key, Auto Increment
- `first_name`: VARCHAR(50), Not Null
- `last_name`: VARCHAR(50), Not Null
- `date_of_birth`: DATE, Not Null
- `gender`: VARCHAR(15), Not Null
- `email`: VARCHAR(100), Not Null, Unique
- `phone`: VARCHAR(20), Not Null
- `emergency_contact_phone`: VARCHAR(20), Null
- `created_at`: TIMESTAMP, Default Current_Timestamp

### Table: doctors
Stores clinical staff data, professional focus, and linking points for availability schedules.
- `id`: INT, Primary Key, Auto Increment
- `first_name`: VARCHAR(50), Not Null
- `last_name`: VARCHAR(50), Not Null
- `specialization`: VARCHAR(100), Not Null
- `email`: VARCHAR(100), Not Null, Unique
- `phone`: VARCHAR(20), Not Null
- `license_number`: VARCHAR(50), Not Null, Unique
- `is_active`: BOOLEAN, Default True

### Table: appointments
Manages the scheduled slots linking patients to clinicians.
- `id`: INT, Primary Key, Auto Increment
- `doctor_id`: INT, Foreign Key → `doctors(id)`, On Delete Restrict
- `patient_id`: INT, Foreign Key → `patients(id)`, On Delete Restrict
- `appointment_time`: DATETIME, Not Null
- `status`: INT, Default 0 (0 = Scheduled, 1 = Completed, 2 = Cancelled, 3 = No Show)
- `reason_for_visit`: VARCHAR(255), Null
- `created_at`: TIMESTAMP, Default Current_Timestamp

### Table: doctor_availability
Tracks specific regular shifts and working hour configurations to prevent double-booking.
- `id`: INT, Primary Key, Auto Increment
- `doctor_id`: INT, Foreign Key → `doctors(id)`, On Delete Cascade
- `day_of_week`: INT, Not Null (0 = Sunday, 1 = Monday, etc.)
- `start_time`: TIME, Not Null
- `end_time`: TIME, Not Null

---

## MongoDB Collection Design

Unstructured data like clinical intake logs, timeline-based notes, and complex multi-item prescriptions evolve over time. Utilizing MongoDB allows for nested arrays (such as individual medications within a prescription order) without complex multi-table SQL joins.

### Schema Evolution & Design Decisions:
* **Referential Strategy:** We store the relational keys (`patientId`, `appointmentId`, `doctorId`) as standard IDs rather than nesting full user objects. This keeps documents lightweight and prevents data stale-out if a patient changes their last name or phone number in MySQL.
* **Extensibility:** The `metadata` and `tags` structures support future expansion (e.g., adding insurance pre-authorization flags later without altering database tables).

---

### Collection: prescriptions

```json
{
  "_id": "6655f1a234fbcde567890abc",
  "appointmentId": 1402,
  "patientId": 452,
  "doctorId": 89,
  "issuedDate": "2026-05-28T10:45:00Z",
  "diagnosis": "Acute Bronchitis",
  "medications": [
    {
      "name": "Amoxicillin",
      "dosage": "500mg",
      "frequency": "Three times daily",
      "durationDays": 10,
      "refillsAllowed": 0
    },
    {
      "name": "Albuterol Inhaler",
      "dosage": "90 mcg/actuation",
      "frequency": "1-2 puffs every 4 hours as needed for wheezing",
      "durationDays": 30,
      "refillsAllowed": 1
    }
  ],
  "clinicalNotes": "Patient instructed to complete the entire course of antibiotics despite symptom resolution. Follow up if fever spikes.",
  "pharmacyRouting": {
    "preferredPharmacyId": "PHARM-9981",
    "name": "Central Care Pharmacy",
    "address": "102 Main St, New City"
  },
  "tags": ["Antibiotic", "Respiratory"],
  "metadata": {
    "schemaVersion": "1.0",
    "digitallySignedBy": "Doc_ID_89_SignKey_xyz"
  }
}