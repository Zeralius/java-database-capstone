/**
 * Core Service: Index Controller
 * Handles homepage authentication dispatching, API communications, and role routing mapping.
 */

// Import required modal controls and environment configuration settings
import { openModal } from "./components/modals.js";
import { BASE_URL } from "./config.js";

// Define strict endpoint routes using the system base context configuration rule
const ADMIN_API = `${BASE_URL}/api/auth/admin/login`;
const DOCTOR_API = `${BASE_URL}/api/auth/doctor/login`;

/**
 * Window Initialization Event
 * Binds structural element event listeners securely after DOM tree completion paint
 */
window.onload = () => {
    // Select login landing action targets
    const btnAdminLogin = document.getElementById("adminLogin");
    const btnDoctorLogin = document.getElementById("doctorLogin");

    // Bind Admin routing triggers if structural nodes are visible in viewport
    if (btnAdminLogin) {
        btnAdminLogin.addEventListener("click", () => {
            openModal("adminLogin");
        });
    }

    // Bind Doctor routing triggers if structural nodes are visible in viewport
    if (btnDoctorLogin) {
        btnDoctorLogin.addEventListener("click", () => {
            openModal("doctorLogin");
        });
    }
};

/**
 * Global Admin Authentication Submission Routine Handler
 */
window.adminLoginHandler = async () => {
    // Step 1: Extract real-time credential input node text field contents
    const usernameInput = document.getElementById("adminUsername")?.value.trim();
    const passwordInput = document.getElementById("adminPassword")?.value;

    if (!usernameInput || !passwordInput) {
        alert("Please provide both your administrative username and account password.");
        return;
    }

    // Step 2: Formulate payload encapsulation block
    const adminCredentials = {
        username: usernameInput,
        password: passwordInput
    };

    // Step 6: Execute payload post deployment wrapped under security handling block
    try {
        // Step 3: Dispatch async network payload transaction block to endpoint
        const response = await fetch(ADMIN_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(adminCredentials)
        });

        // Step 4: Evaluate compliance outcome statuses
        if (response.ok) {
            const data = await response.json();
            
            // Extract and commit authorization token strings straight to hardware storage
            localStorage.setItem("token", data.token);
            localStorage.setItem("userRole", "admin");

            // Dispatch dashboard context routing changes sequence execution
            if (typeof window.selectRole === "function") {
                window.selectRole("admin");
            } else {
                // System fallback routing path rule execution if helper engine layer isn't globally active
                window.location.href = "/templates/admin/adminDashboard.html";
            }
        } else {
            // Step 5: Handle bad response status code validation failures (e.g. 401, 403)
            const errorData = await response.json().catch(() => ({}));
            alert(errorData.message || "Authentication failed. Invalid admin credentials provided.");
        }

    } catch (error) {
        // Handle server down drops or system hardware transport network fault errors
        console.error("Critical error during administrative validation lifecycle:", error);
        alert("Unable to reach the authentication gateway. Please confirm connection layer status or try again later.");
    }
};

/**
 * Global Clinical Doctor Authentication Submission Routine Handler
 */
window.doctorLoginHandler = async () => {
    // Step 1: Extract validation text values from form input elements
    const emailInput = document.getElementById("doctorEmail")?.value.trim();
    const passwordInput = document.getElementById("doctorPassword")?.value;

    if (!emailInput || !passwordInput) {
        alert("Please input your registered clinical email identity and password profile.");
        return;
    }

    // Step 2: Build operational credential validation schema
    const doctorCredentials = {
        email: emailInput,
        password: passwordInput
    };

    // Step 6: Initialize processing lifecycle beneath try-catch environment shield block
    try {
        // Step 3: Forward asynchronous registration payload data to remote endpoint
        const response = await fetch(DOCTOR_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctorCredentials)
        });

        // Step 4: Intercept response context mapping returns
        if (response.ok) {
            const data = await response.json();

            // Store security identity variables locally
            localStorage.setItem("token", data.token);
            localStorage.setItem("userRole", "doctor");

            // Complete workstation navigation path change execution triggers
            if (typeof window.selectRole === "function") {
                window.selectRole("doctor");
            } else {
                window.location.href = "/templates/doctor/doctorDashboard.html";
            }
        } else {
            // Step 5: Process login credential match processing failures
            alert("Invalid clinical login profile matching data. Please check entry criteria correctness and re-submit.");
        }

    } catch (error) {
        // Step 6: Log structural trace exceptions safely to core console logger
        console.error("Critical fault state detected while attempting doctor auth pipeline:", error);
        alert("An unexpected platform connectivity fault intercepted processing requests. Retrying transaction sequence suggested.");
    }
};