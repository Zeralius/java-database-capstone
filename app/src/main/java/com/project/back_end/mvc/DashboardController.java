package com.project.back_end.mvc;

import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private Service service;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> response = service.validateToken(token, "admin");

        if (response.getStatusCode() == HttpStatus.OK) {
            return "admin/adminDashboard";
        }

        return "redirect:/";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> response = service.validateToken(token, "doctor");

        if (response.getStatusCode() == HttpStatus.OK) {
            return "doctor/doctorDashboard";
        }

        return "redirect:/";
    }
}
