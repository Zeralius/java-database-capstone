# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]

## Admin User Stories

User Story 1: Admin Login

Title:
As an Admin, I want to log into the portal with my username and password, so that I can manage the hospital platform securely.

Acceptance Criteria:
1. The system must provide a secure login form requiring a unique username and password.
2. Upon entering correct credentials, the Admin must be redirected to the admin dashboard.
3. If incorrect credentials are typed, the system must display a clear error message and deny access.

Priority: High
Story Points: 3
Notes:
- Passwords must be encrypted in transit and at rest.
- Session timeout should occur after 15 minutes of inactivity to maintain security.

---

User Story 2: Admin Logout

Title:
As an Admin, I want to log out of the portal, so that I can protect system access when I am away from my workstation.

Acceptance Criteria:
1. A visible "Log Out" button must be accessible from any screen within the admin view.
2. Clicking the logout button must instantly terminate the current user session.
3. After logging out, navigating back using the browser history must not reveal any restricted admin pages.

Priority: High
Story Points: 2
Notes:
- Redirect the user back to the public login landing page immediately upon successful logout.

---

User Story 3: Add Doctor Profiles

Title:
As an Admin, I want to add new doctors to the portal, so that they can be registered in the system and begin managing patient appointments.

Acceptance Criteria:
1. The Admin must be able to fill out a form with a doctor's details (e.g., Name, Specialization, Email, and Employee ID).
2. The system must validate that the Email and Employee ID are unique before saving.
3. Upon successful creation, the new doctor's account must be active, and a confirmation message must be displayed.

Priority: High
Story Points: 5
Notes:
- Consider adding an optional field for profile pictures or specific department assignments in a future iteration.

---

User Story 4: Delete Doctor Profiles

Title:
As an Admin, I want to delete a doctor's profile from the portal, so that the roster remains accurate when a medical professional leaves the hospital.

Acceptance Criteria:
1. The Admin must see a "Delete" option next to any doctor's profile in the management directory.
2. Clicking delete must trigger a confirmation pop-up warning the Admin of the action.
3. The system must prevent deletion or handle the data gracefully if the doctor has active, upcoming patient appointments.

Priority: Medium
Story Points: 5
Notes:
- Edge Case: Evaluate whether hard deleting data is safe, or if implementing a "soft delete" (setting an is_active flag to false) is better to preserve past appointment history for medical auditing.

---

User Story 5: Track Appointment Usage Statistics

Title:
As an Admin, I want to execute a stored procedure in the MySQL CLI, so that I can fetch the number of appointments per month and track platform usage statistics.

Acceptance Criteria:
1. A MySQL stored procedure must exist that aggregates appointment data grouped by month and year.
2. Running the command in the MySQL CLI must output a clean, tabular format displaying the month and the total appointment count.
3. The query performance must be optimized to handle large datasets efficiently without locking the live appointment database table.

Priority: Medium
Story Points: 3
Notes:
- This task is specific to backend management via the database command line rather than a frontend UI element. Ensure the database user account used by the Admin has the proper execution privileges for this specific stored procedure.

## Patient User Stories 

User Story 1: List of Doctors without login

Title:
As a patient, I want to view a list of doctors without logging in, so that I can explore my options before registering.

Acceptance Criteria:
1. The public landing page must display a searchable directory or list of available doctors.
2. Unauthenticated visitors must be able to view the doctors' names, specializations, and profile details.
3. ttempting to click a "Book Appointment" button from this public list must prompt the user to log in or sign up.

Priority: High
Story Points: 3
Notes:
- Ensure the public view does not expose any private scheduling details or contact information of the doctors.

---

User Story 2: Register user account 

Title:
As a patient, I want to sign up using my email and password, so that I can book appointments.

Acceptance Criteria:
1. The system must provide a registration form requiring a valid email address and a secure password.
2. The system must validate that the email format is correct and is not already registered in the database.
3. Upon successful registration, the patient account must be created, and the user should be automatically logged in or directed to a verification page.

Priority: High
Story Points: 5
Notes:
- Standard password strength validation (e.g., length, special characters) should be enforced during sign-up.

---

User Story 3: Login

Title:
As a patient, I want to log into the portal, so that I can manage my bookings.

Acceptance Criteria:
1. A login page must accept the patient's registered email address and password.
2. Upon successful authentication, the patient must be redirected to their personal portal dashboard.
3. If the login fails due to incorrect credentials, an appropriate error message must be displayed without revealing which field was incorrect.

Priority: High
Story Points: 3
Notes:
- A "Forgot Password" link should be included on this interface for future implementation.

---

User Story 4: logout

Title:
As a patient, I want to log out of the portal, so that I can secure my account.

Acceptance Criteria:
1. A clear "Log Out" option must be visible on the portal header or navigation menu from any screen.
2. Clicking logout must immediately destroy the current active session tokens.
3. The user must be redirected to the public home page, and using the browser's back button must not grant re-entry to the private dashboard.

Priority: Medium
Story Points: 2
Notes:
- Clear any locally cached user data upon successful logout to prevent session hijacking on shared devices.

---

User Story 5: booking hour long appointment

Title:
As a logged-in patient, I want to book an hour-long appointment, so that I can consult with a doctor.

Acceptance Criteria:
1. The portal must allow the patient to select a doctor and view their available, unbooked 1-hour time slots.
2. Selecting a valid time slot and confirming the choice must successfully reserve the appointment in the database.
3. The system must immediately block that specific time slot so that no other patient can book it simultaneously.

Priority: High
Story Points: 8
Notes:
- Edge Case: If two patients click "Confirm" on the exact same slot at the same moment, the database transaction must safely handle the race condition, booking the first request and alerting the second user to choose another time.

---

User Story 6: viewing upcoming appointments 

Title:
As a logged-in patient, I want to view my upcoming appointments, so that I can prepare accordingly.

Acceptance Criteria:
1. The patient dashboard must feature a dedicated section displaying a list of all scheduled future appointments.
2. Each entry in the list must clearly show the doctor’s name, specialization, date, and time of the appointment.
3. The list must sort appointments chronologically, showing the soonest upcoming appointment first.

Priority: High
Story Points: 3
Notes:
- Past or canceled appointments should either be filtered out of this specific view or moved to a separate "History" tab to keep the dashboard organized.

## Doctor User Stories 

User Story 1: login and manage appointments

Title:
As a doctor, I want to log into the portal, so that I can manage my appointments.

Acceptance Criteria:
1. A secure login interface must accept the doctor's registered email/username and password.
2. Upon entering valid credentials, the system must authenticate the user and redirect them directly to the doctor dashboard view.
3. Invalid credentials must display an error message and block entry to protect sensitive medical scheduling data.

Priority: High
Story Points: 3
Notes:
- The authentication mechanism should verify the user's role specifically as a "Doctor" to ensure proper access control mapping.

---

User Story 2: log out

Title:
As a doctor, I want to log out of the portal, so that I can protect my data.

Acceptance Criteria:
1. A distinct "Log Out" action must be accessible from the navigation panel on any dashboard view.
2. Triggering the logout must instantly destroy the doctor's active session state and revoke authorization tokens.
3. The browser session must be redirected back to the public homepage, preventing back-button access to patient schedules.

Priority: High
Story Points: 2
Notes:
- Automatic session termination should be implemented after a period of idleness to account for shared workstations in clinical environments.

---

User Story 3: view appointment calendar

Title:
As a doctor, I want to view my appointment calendar, so that I can stay organized.

Acceptance Criteria:
1. The dashboard must feature a clear calendar grid or list interface displaying all confirmed bookings.
2. Appointments must be displayed with chronological clarity, mapping out the booked hour-long segments distinctively.
3. The calendar interface must support switching views (e.g., daily, weekly, or monthly) to manage workloads effectively.

Priority: High
Story Points: 5
Notes:
- It is highly recommended to display the primary patient name directly on the calendar blocks for quick scannability.

---

User Story 4: mark unavailability for patients

Title:
As a doctor, I want to mark my unavailability, so that patients are only shown available slots.

Acceptance Criteria:
1. The calendar interface must allow the doctor to select a specific date and time block to flag as "Unavailable" or "Leave."
2. The system must immediately remove or hide those blocked hours from the public/patient booking view.
3. The system should prevent booking changes if a patient has already reserved an appointment during that specific time block, alerting the doctor to handle the existing conflict.

Priority: Medium
Story Points: 5
Notes:
- Edge case: Consider whether blocking a slot should automatically notify or cancel overlapping patient bookings, or if manual rescheduling is required beforehand.

---

User Story 5: update doctor profile

Title:
As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information.

Acceptance Criteria:
1. An editable profile management form must be accessible to the logged-in doctor.
2. The form must allow input modifications for medical specialization details, phone number, and professional contact email.
3. Saving the changes must immediately update the database and reflect the new information on the public doctor listing page.

Priority: Medium
Story Points: 3
Notes:
- Input validation should ensure phone numbers and email addresses conform to standard data formats before committing updates.

---

User Story 6: view patients details for upcoming appointments

Title:
As a doctor, I want to view the patient details for upcoming appointments, so that I can be prepared.

Acceptance Criteria:
1. Clicking on a scheduled appointment slot within the calendar or dashboard must open a detailed record view.
2. The record view must securely present the assigned patient's full name, contact information, and registration email.
3. The view must only display records for patients matching the doctor's confirmed scheduled hours.

Priority: High
Story Points: 5
Notes:
- Access logs should be maintained behind the scenes whenever patient contact information is loaded to comply with standard data protection principles.