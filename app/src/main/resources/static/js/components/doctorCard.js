/**
 * Component: Doctor Card
 * Dynamically builds DOM components for doctor profiles with role-specific actions.
 */

// 1. Import statements for required dependencies from your services layer
import { showBookingOverlay } from "../loggedPatient.js";
import { deleteDoctor } from "../services/doctorServices.js";
import { fetchPatientDetails } from "../services/patientServices.js";

/**
 * Creates and returns a complete DOM element for a single doctor card
 * @param {Object} doctor - The doctor object data from the backend/API
 * @returns {HTMLElement} The configured doctor card element
 */
export function createDoctorCard(doctor) {
    // Step 2: Create the main container for the doctor card
    const cardElement = document.createElement("div");
    cardElement.className = "doctor-card";
    cardElement.setAttribute("data-id", doctor.id);

    // Step 3: Retrieve the current user role from localStorage
    const role = localStorage.getItem("userRole");

    // Step 4: Create a div to hold doctor information
    const docInfoContainer = document.createElement("div");
    docInfoContainer.className = "doctor-info-container";

    // Step 5: Create and set the doctor’s name
    const docName = document.createElement("h3");
    docName.className = "doctor-name";
    docName.textContent = `Dr. ${doctor.name}`;

    // Step 6: Create and set the doctor's specialization
    const docSpecialty = document.createElement("p");
    docSpecialty.className = "doctor-specialty";
    docSpecialty.textContent = doctor.specialty || "General Medicine";

    // Step 7: Create and set the doctor's email
    const docEmail = document.createElement("p");
    docEmail.className = "doctor-email";
    docEmail.textContent = doctor.email;

    // Step 8: Create and list available appointment times
    const docAvailability = document.createElement("div");
    docAvailability.className = "doctor-availability";
    
    const availabilityTitle = document.createElement("span");
    availabilityTitle.textContent = "Available Times: ";
    docAvailability.appendChild(availabilityTitle);

    if (doctor.availableTimes && doctor.availableTimes.length > 0) {
        doctor.availableTimes.forEach(time => {
            const timeTag = document.createElement("span");
            timeTag.className = "time-slot-tag";
            timeTag.textContent = time;
            docAvailability.appendChild(timeTag);
        });
    } else {
        const noTimeTag = document.createElement("span");
        noTimeTag.className = "no-time-tag";
        noTimeTag.textContent = "No open slots today";
        docAvailability.appendChild(noTimeTag);
    }

    // Step 9: Append all info elements to the doctor info container
    docInfoContainer.appendChild(docName);
    docInfoContainer.appendChild(docSpecialty);
    docInfoContainer.appendChild(docEmail);
    docInfoContainer.appendChild(docAvailability);

    // Step 10: Create a container for card action buttons
    const actionsContainer = document.createElement("div");
    actionsContainer.className = "doctor-card-actions";

    // Step 11: === ADMIN ROLE ACTIONS ===
    if (role === "admin") {
        // Create a delete button
        const btnDelete = document.createElement("button");
        btnDelete.className = "button btn-danger";
        btnDelete.textContent = "Delete Profile";

        // Add click handler for delete button
        btnDelete.addEventListener("click", async () => {
            if (confirm(`Are you sure you want to delete the profile for Dr. ${doctor.name}?`)) {
                // Get the admin token from localStorage
                const token = localStorage.getItem("token");
                
                try {
                    // Call API to delete the doctor
                    const success = await deleteDoctor(doctor.id, token);
                    
                    // Show result and remove card if successful
                    if (success) {
                        alert("Doctor profile removed successfully.");
                        cardElement.remove(); // Removes the node straight from the DOM tree
                    } else {
                        alert("Failed to delete doctor profile. Please try again.");
                    }
                } catch (error) {
                    console.error("Error deleting doctor:", error);
                    alert("An error occurred while attempting to delete this profile.");
                }
            }
        });

        // Add delete button to actions container
        actionsContainer.appendChild(btnDelete);
    }

    // Step 12: === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
    else if (role === "patient" || !role) {
        // Create a book now button
        const btnBookGuest = document.createElement("button");
        btnBookGuest.className = "button role-btn";
        btnBookGuest.textContent = "Book Now";

        // Alert patient to log in before booking
        btnBookGuest.addEventListener("click", () => {
            alert("You must be logged in to schedule appointments. Please use the Login button in the header.");
        });

        // Add button to actions container
        actionsContainer.appendChild(btnBookGuest);
    }

    // Step 13: === LOGGED-IN PATIENT ROLE ACTIONS ===
    else if (role === "loggedPatient") {
        // Create a book now button
        const btnBookLogged = document.createElement("button");
        btnBookLogged.className = "button role-btn";
        btnBookLogged.textContent = "Book Now";

        // Handle booking logic for logged-in patient
        btnBookLogged.addEventListener("click", async () => {
            const token = localStorage.getItem("token");

            // Redirect if token not available
            if (!token) {
                alert("Session invalid or expired. Returning to login.");
                localStorage.setItem("userRole", "patient");
                window.location.href = "/";
                return;
            }

            try {
                // Fetch patient data with token
                const patientData = await fetchPatientDetails(token);

                if (patientData) {
                    // Show booking overlay UI with doctor and patient info
                    showBookingOverlay(doctor, patientData);
                } else {
                    alert("Unable to load your profile details. Please try again.");
                }
            } catch (error) {
                console.error("Error launching scheduling sequence:", error);
                alert("An error occurred while loading the booking interface.");
            }
        });

        // Add button to actions container
        actionsContainer.appendChild(btnBookLogged);
    }

    // Step 14: Append doctor info and action buttons to the card
    cardElement.appendChild(docInfoContainer);
    cardElement.appendChild(actionsContainer);

    // Step 15: Return the complete doctor card element
    return cardElement;
}