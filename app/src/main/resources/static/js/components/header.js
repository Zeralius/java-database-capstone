/**
 * Hospital CMS Header Component Service
 * Dynamically handles layout generation and authentication guards across views
 */

// Step 16: Automatically initialize the header rendering process when the page loads
document.addEventListener("DOMContentLoaded", () => {
    renderHeader();
});

// Step 1: Define the renderHeader Function
function renderHeader() {
    // Step 2: Select the Header Div
    const headerDiv = document.getElementById("header");
    if (!headerDiv) return; 

    // Step 3: Check if the Current Page is the Root Page
    if (window.location.pathname.endsWith("/") || window.location.pathname.endsWith("/index.html")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
        headerDiv.innerHTML = `
            <header class="header">
              <div class="logo-section">
                <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
                <span class="logo-title">Hospital CMS</span>
              </div>
            </header>`;
        return;
    }

    // Step 4: Retrieve the User's Role and Token from LocalStorage
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // Step 6: Handle Session Expiry or Invalid Login
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    // Step 5: Initialize Header Content
    let headerContent = `
        <header class="header">
          <div class="logo-section">
            <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
            <span class="logo-title">Hospital CMS</span>
          </div>
          <nav>
            <ul class="nav-items">`;

    // Step 7: Add Role-Specific Header Content (Cleaned of unsafe inline onclicks)
    if (role === "admin") {
        headerContent += `
            <li><button id="addDocBtn" class="adminBtn">Add Doctor</button></li>
            <li><a href="#" id="logoutBtn">Logout</a></li>`;
    } 
    else if (role === "doctor") {
        headerContent += `
            <li><button id="doctorHomeBtn" class="adminBtn">Home</button></li>
            <li><a href="#" id="logoutBtn">Logout</a></li>`;
    } 
    else if (role === "patient") {
        headerContent += `
            <li><button id="patientLogin" class="adminBtn">Login</button></li>
            <li><button id="patientSignup" class="adminBtn">Sign Up</button></li>`;
    } 
    else if (role === "loggedPatient") {
        headerContent += `
            <li><button id="patientHomeBtn" class="adminBtn">Home</button></li>
            <li><button id="patientAppointmentsBtn" class="adminBtn">Appointments</button></li>
            <li><a href="#" id="logoutPatientBtn">Logout</a></li>`;
    }

    // Step 9: Close the Header Section HTML tags safely
    headerContent += `
            </ul>
          </nav>
        </header>`;

    // Step 10: Render the Header Content
    headerDiv.innerHTML = headerContent;

    // Step 11: Attach Event Listeners to Header Buttons
    attachHeaderButtonListeners();
}

// Step 13: Attach Event Listeners Handler Function
function attachHeaderButtonListeners() {
    // Admin Selectors
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => openModal('addDoctor'));
    }

    // Doctor Selectors
    const doctorHomeBtn = document.getElementById("doctorHomeBtn");
    if (doctorHomeBtn) {
        doctorHomeBtn.addEventListener("click", () => selectRole('doctor'));
    }

    // Unauthenticated Patient Selectors
    const patientLogin = document.getElementById("patientLogin");
    if (patientLogin) {
        patientLogin.addEventListener("click", () => openModal('login'));
    }

    const patientSignup = document.getElementById("patientSignup");
    if (patientSignup) {
        patientSignup.addEventListener("click", () => openModal('signup'));
    }

    // Logged-in Patient Selectors
    const patientHomeBtn = document.getElementById("patientHomeBtn");
    if (patientHomeBtn) {
        patientHomeBtn.addEventListener("click", () => {
            window.location.href = '/pages/loggedPatientDashboard.html';
        });
    }

    const patientAppointmentsBtn = document.getElementById("patientAppointmentsBtn");
    if (patientAppointmentsBtn) {
        patientAppointmentsBtn.addEventListener("click", () => {
            window.location.href = '/pages/patientAppointments.html';
        });
    }

    // Shared Standard Logout Selector (Admin & Doctor)
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            logout();
        });
    }

    // Logged Patient Specific Logout Selector
    const logoutPatientBtn = document.getElementById("logoutPatientBtn");
    if (logoutPatientBtn) {
        logoutPatientBtn.addEventListener("click", (e) => {
            e.preventDefault();
            logoutPatient();
        });
    }
}

// Step 14: Global Logout (Clears all state variables and boots to landing view)
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

// Step 15: Patient-Specific Logout (Evicts auth tokens but preserves guest profile status)
function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}