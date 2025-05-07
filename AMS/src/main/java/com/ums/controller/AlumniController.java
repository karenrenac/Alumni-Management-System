package com.ums.controller;

import com.ums.model.Alumni;
import com.ums.service.AlumniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class AlumniController {

    @Autowired
    private AlumniService alumniService;

    // Show login page
    @GetMapping("/AlumniLogin")
    public String showLoginPage() {
        return "AlumniLogin"; // HTML file name (alumnilogin.html)
    }

    // Handle login form submission
    @PostMapping("/AlumniLogin")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        Alumni alumni = alumniService.fetchAlumniByEmail(email);

        if (alumni != null && alumni.getPassword().equals(password)) {
            session.setAttribute("loggedInAlumni", alumni);
            return "redirect:/Alumni/dashboard";
        } else {
            model.addAttribute("loginError", "Invalid username or password.");
            return "AlumniLogin";
        }
    }

    // Show alumni dashboard only if logged in
    @GetMapping("/Alumni/dashboard")
    public String showAlumniDashboard(HttpSession session, Model model) {
        Alumni loggedInAlumni = (Alumni) session.getAttribute("loggedInAlumni");
        if (loggedInAlumni == null) {
            return "redirect:/AlumniLogin";
        }
        model.addAttribute("alumni", loggedInAlumni);
        return "AlumniPage"; // HTML file: AlumniDashboard.html
    }

    // Redirect base /alumni to dashboard
    @GetMapping("/alumni")
    public String redirectToDashboard() {
        return "redirect:/alumni/dashboard";
        
    }
}
