package com.ums.controller;

import com.ums.model.Alumni;
import com.ums.service.AlumniService;
import com.ums.service.EventRegistrationService;
import com.ums.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class AlumniController {

    @Autowired
    private AlumniService alumniService;
    
    @Autowired
    private EventService eventService;

    @Autowired
    private EventRegistrationService eventRegistrationService;
    
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
            if (alumni.isPasswordChangeRequired()) {
                return "redirect:/alumni/changepassword"; // force password update
            }
            
            return "redirect:/Alumni/dashboard";
        } else {
            model.addAttribute("loginError", "Invalid username or password.");
            return "AlumniLogin";
        }
    }
    
    @GetMapping("/alumni/changepassword")
    public String showChangePasswordForm(HttpSession session) {
        Alumni loggedInAlumni = (Alumni) session.getAttribute("loggedInAlumni");
        if (loggedInAlumni == null) {
            return "redirect:/AlumniLogin";
        }
        return "ChangePassword";
    }
    
    
    // Handle password change after first time login
    @PostMapping("/alumni/changepassword")
    public String changePassword(@RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 Model model) {
        Alumni loggedInAlumni = (Alumni) session.getAttribute("loggedInAlumni");
        if (loggedInAlumni == null) {
            return "redirect:/AlumniLogin";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "ChangePassword";
        }

        loggedInAlumni.setPassword(newPassword);
        loggedInAlumni.setPasswordChangeRequired(false);
        alumniService.updateAlumni(loggedInAlumni, loggedInAlumni.getAlumniId());

        session.setAttribute("loggedInAlumni", loggedInAlumni); // update session

        return "redirect:/Alumni/dashboard";
    }

    
    // Show alumni dashboard only if logged in
    @GetMapping("/Alumni/dashboard")
    public String showAlumniDashboard(HttpSession session, Model model) {
        Alumni loggedInAlumni = (Alumni) session.getAttribute("loggedInAlumni");
        if (loggedInAlumni == null) {
            return "redirect:/AlumniLogin";
        }
        model.addAttribute("alumni", loggedInAlumni);
        model.addAttribute("events", eventService.fetchAllEvents());
        model.addAttribute("registrations", eventRegistrationService.fetchRegistrationsByAlumni(loggedInAlumni.getAlumniId()));


        return "AlumniPage"; // HTML file: AlumniDashboard.html
    }

    // Redirect base /alumni to dashboard
    @GetMapping("/alumni")
    public String redirectToDashboard() {
        return "redirect:/alumni/dashboard";
        
    }
}
