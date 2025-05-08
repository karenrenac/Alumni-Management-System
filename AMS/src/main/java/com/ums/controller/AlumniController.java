package com.ums.controller;

import com.ums.model.Alumni;
import com.ums.model.AlumniEvent;
import com.ums.model.EventRegistration;
import com.ums.service.AlumniService;
import com.ums.service.EventService;
import com.ums.service.EventRegistrationService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
        return "AlumniLogin";
    }

    // Process login
    @PostMapping("/AlumniLogin")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        Alumni alumni = alumniService.fetchAlumniByEmail(email);
        if (alumni != null && alumni.getPassword().equals(password)) {
            session.setAttribute("loggedInAlumni", alumni);
            if (alumni.isPasswordChangeRequired()) {
                return "redirect:/alumni/changepassword";
            }
            return "redirect:/Alumni/dashboard";
        } else {
            model.addAttribute("loginError", "Invalid username or password.");
            return "AlumniLogin";
        }
    }

    // Show change password form
    @GetMapping("/alumni/changepassword")
    public String showChangePasswordForm(HttpSession session) {
        Alumni loggedInAlumni = (Alumni) session.getAttribute("loggedInAlumni");
        return (loggedInAlumni == null) ? "redirect:/AlumniLogin" : "ChangePassword";
    }

    // Handle password change
    @PostMapping("/alumni/changepassword")
    public String changePassword(@RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 Model model) {
        Alumni alumni = (Alumni) session.getAttribute("loggedInAlumni");
        if (alumni == null) return "redirect:/AlumniLogin";

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "ChangePassword";
        }

        alumni.setPassword(newPassword);
        alumni.setPasswordChangeRequired(false);
        alumniService.updateAlumni(alumni, alumni.getAlumniId());
        session.setAttribute("loggedInAlumni", alumni);
        return "redirect:/Alumni/dashboard";
    }

    // Show alumni dashboard
    @GetMapping("/Alumni/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Alumni alumni = (Alumni) session.getAttribute("loggedInAlumni");
        if (alumni == null) return "redirect:/AlumniLogin";

        model.addAttribute("alumni", alumni);
        model.addAttribute("events", eventService.fetchAllEvents());
        model.addAttribute("registeredEvents", eventService.getEventsRegisteredByAlumni(alumni.getAlumniId()));
        model.addAttribute("allAlumni", alumniService.fetchAllAlumni());

        return "AlumniPage";
    }

    
    // Update alumni profile
    @PostMapping("/alumni/update")
    public String updateAlumniProfile(@RequestParam String name,
                                      @RequestParam String email,
                                      @RequestParam String username,
                                      @RequestParam int graduationYear,
                                      HttpSession session) {
        Alumni alumni = (Alumni) session.getAttribute("loggedInAlumni");
        if (alumni == null) return "redirect:/AlumniLogin";

        alumni.setName(name);
        alumni.setEmail(email);
        alumni.setUsername(username);
        alumni.setGraduationYear(graduationYear);

        alumniService.updateAlumni(alumni, alumni.getAlumniId());
        session.setAttribute("loggedInAlumni", alumni);
        return "redirect:/Alumni/dashboard";
    }
    
    @GetMapping("/alumni/view-events")
    public String showEventsPage(HttpSession session, Model model) {
        Alumni alumni = (Alumni) session.getAttribute("loggedInAlumni");
        if (alumni == null) return "redirect:/AlumniLogin";

        model.addAttribute("events", eventService.fetchAllEvents());
        return "AlumniViewEvents"; // This matches the filename above
    }
    
    @GetMapping("/alumni/registered-events")
    public String showRegisteredEventsPage(HttpSession session, Model model) {
        Alumni alumni = (Alumni) session.getAttribute("loggedInAlumni");
        if (alumni == null) {
            return "redirect:/AlumniLogin";
        }

        List<AlumniEvent> registeredEvents = eventService.getEventsRegisteredByAlumni(alumni.getAlumniId());
        model.addAttribute("alumni", alumni);
        model.addAttribute("registeredEvents", registeredEvents);
        return "AlumniRegisteredEvents"; // HTML page
    }


    
    // Register for event
    @PostMapping("/alumni/events/register")
    public String registerForEvent(@RequestParam String eventId,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Alumni alumni = (Alumni) session.getAttribute("loggedInAlumni");
        if (alumni == null) return "redirect:/AlumniLogin";

        boolean registered = eventService.registerAlumniForEvent(alumni.getAlumniId(), eventId);

        if (registered) {
            redirectAttributes.addFlashAttribute("success", "You have registered for this event.");
        } else {
            redirectAttributes.addFlashAttribute("info", "You already registered for this event.");
        }

        return "redirect:/alumni/view-events";
    }


    // Contact Admin (optional - for message feature)
    @PostMapping("/alumni/contact")
    public String contactAdmin(@RequestParam int adminId,
                               @RequestParam String subject,
                               @RequestParam String message,
                               HttpSession session) {
        Alumni alumni = (Alumni) session.getAttribute("loggedInAlumni");
        if (alumni != null) {
            System.out.println("Message from Alumni " + alumni.getAlumniId() + " to Admin " + adminId);
            // Add service to save/send message
        }
        return "redirect:/Alumni/dashboard";
    }

    // Redirect /alumni → dashboard
    @GetMapping("/alumni")
    public String redirectToDashboard() {
        return "redirect:/Alumni/dashboard";
    }

    // Logout
    @GetMapping("/alumni/logout")
    public String logoutAlumni(HttpSession session) {
        session.invalidate();
        return "redirect:/AlumniLogin";
    }
}
