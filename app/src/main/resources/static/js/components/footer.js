/**
 * Hospital CMS Footer Component Service
 * Dynamically renders a structured multi-column navigation footer across all workspace views
 */

// Step 11 (Part 2): Automatically initialize the footer rendering process when the page loads
document.addEventListener("DOMContentLoaded", () => {
    renderFooter();
});

/**
 * Function to render the footer content into the page
 */
function renderFooter() {
    // Select the footer element from the DOM using its ID hook
    const footerDiv = document.getElementById("footer");
    if (!footerDiv) return; // Defensive exit if no footer container matches on the current page

    // Step 1 - 10: Initialize the HTML string layout blocks
    let footerContent = `
        <footer class="footer">
            <div class="footer-container">
                
                <div class="footer-logo">
                    <img src="../assets/images/logo/logo.png" alt="Hospital CMS Logo">
                    <p>© Copyright 2025. All Rights Reserved by Hospital CMS.</p>
                </div>
                
                <div class="footer-links">
                    
                    <div class="footer-column">
                        <h4>Company</h4>
                        <a href="#">About</a>
                        <a href="#">Careers</a>
                        <a href="#">Press</a>
                    </div>
                    
                    <div class="footer-column">
                        <h4>Support</h4>
                        <a href="#">Account</a>
                        <a href="#">Help Center</a>
                        <a href="#">Contact Us</a>
                    </div>
                    
                    <div class="footer-column">
                        <h4>Legals</h4>
                        <a href="#">Terms & Conditions</a>
                        <a href="#">Privacy Policy</a>
                        <a href="#">Licensing</a>
                    </div>
                    
                </div> </div> </footer>
    `;

    // Step 11 (Part 1): Set the inner HTML of the matching target element to inject the content
    footerDiv.innerHTML = footerContent;
}