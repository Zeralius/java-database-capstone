/**
 * Service: Doctor Services API Integration Gateway
 * Handles backend HTTP network requests for managing and querying doctor data models.
 */

// Import the base API server URL configuration rule
import { BASE_URL } from "../config.js";

// Define base API endpoint string
const DOCTOR_API = `${BASE_URL}/api/doctors`;

/**
 * Function: getDoctors
 * Purpose: Fetch the list of all doctors from the API
 * @returns {Promise<Array>} List of doctor objects or empty array if failure occurs
 */
export async function getDoctors() {
    try {
        const response = await fetch(DOCTOR_API, {
            method: "GET"
        });

        if (!response.ok) {
            throw new Error(`HTTP error. Status: ${response.status}`);
        }

        const data = await response.json();
        // Return the 'doctors' data array if it exists, otherwise default fallback to empty list
        return data.doctors || data || [];

    } catch (error) {
        console.error("Error executing getDoctors service request pipeline:", error);
        return [];
    }
}

/**
 * Function: deleteDoctor
 * Purpose: Delete a specific doctor using their ID and an authentication token
 * @param {string|number} doctorId - Unique identifier of the target doctor
 * @param {string} token - Administrative authorization token string
 * @returns {Promise<boolean>} True if deletion transaction succeeded, otherwise false
 */
export async function deleteDoctor(doctorId, token) {
    // Construct path parameter routing: /api/doctors/{id}/{token}
    const deleteUrl = `${DOCTOR_API}/${doctorId}/${token}`;

    try {
        const response = await fetch(deleteUrl, {
            method: "DELETE"
        });

        const data = await response.json().catch(() => ({}));

        if (response.ok) {
            return true; 
        } else {
            console.warn("Server side deletion rejected:", data.message || "Unknown cause");
            return false;
        }

    } catch (error) {
        console.error(`Critical network transport error while deleting doctor ID ${doctorId}:`, error);
        return false;
    }
}

/**
 * Function: saveDoctor
 * Purpose: Save (create) a new doctor profile record using a POST request
 * @param {Object} doctorData - Complete entity data mapping for the new doctor profile
 * @param {string} token - Security credential validation string
 * @returns {Promise<Object>} Object container detailing success flag and server messages
 */
export async function saveDoctor(doctorData, token) {
    // Build path matching deployment syntax pattern: /api/doctors/{token}
    const saveUrl = `${DOCTOR_API}/${token}`;

    try {
        const response = await fetch(saveUrl, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctorData)
        });

        const data = await response.json().catch(() => ({}));

        return {
            success: response.ok,
            message: data.message || (response.ok ? "Doctor profile created." : "Validation error.")
        };

    } catch (error) {
        console.error("Error executing saveDoctor service pipeline transaction:", error);
        return {
            success: false,
            message: "Unable to complete creation pipeline. Connection to gateway lost."
        };
    }
}

/**
 * Function: filterDoctors
 * Purpose: Fetch subset array of doctors based on precise text filtering parameters
 * @param {string} name - Doctor text search name string parameter filter
 * @param {string} time - Availability slot window match condition rule
 * @param {string} specialty - Specialty field categorization key parameter filter
 * @returns {Promise<Array|Object>} Array list matching conditions or specialized data block object
 */
export async function filterDoctors(name, time, specialty) {
    // Encapsulate empty entries safely into string values to avoid path breakage drops
    const searchName = name.trim() || "all";
    const searchTime = time || "all";
    const searchSpecialty = specialty || "all";

    // Formulate endpoint criteria url mapping string path matching: /api/doctors/{name}/{time}/{specialty}
    const filterUrl = `${DOCTOR_API}/${encodeURIComponent(searchName)}/${encodeURIComponent(searchTime)}/${encodeURIComponent(searchSpecialty)}`;

    try {
        const response = await fetch(filterUrl, {
            method: "GET"
        });

        // Check if the response status is OK
        if (response.ok) {
            const data = await response.json();
            // Parse and return the matching doctor array matrix
            return data.doctors || data || [];
        } else {
            // Log target response errors gracefully
            console.error(`Filter processing failed on endpoint status code layout: ${response.status}`);
            return { doctors: [] };
        }

    } catch (error) {
        // Catch any other pipeline connection transport layer faults, alert user, and pass empty arrays 
        console.error("Critical connection failure within filter Doctors processing chain:", error);
        alert("A background processing query error interrupted search result updates. Please try again.");
        return { doctors: [] };
    }
}