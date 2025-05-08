package com.ums.controller;

import com.ums.model.Admin;
import com.ums.model.Alumni;
import com.ums.model.AlumniEvent;
import com.ums.model.ActivityLog;
import com.ums.service.AdminService;
import com.ums.service.AlumniService;
import com.ums.service.EventService;

import jakarta.servlet.http.HttpSession;

import com.ums.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AlumniService alumniService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ActivityLogService activityLogService;

 // Show Admin Login Page
    @GetMapping("/AdminLogin")
    public String showAdminLoginPage() {
        return "adminLogin";  // This should match your adminLogin.html page name (without .html)
    }
    //1. Admin Login
    @PostMapping("/login")
    public String adminLogin(@RequestParam String username,
                             @RequestParam String password,
                             HttpSession session) {
        Admin admin = adminService.fetchAdminByUsername(username);
        if (admin != null && admin.getPassword().equals(password)) {
            session.setAttribute("loggedInAdmin", admin); // ✅ store in session
            return "redirect:/admin/dashboard";
        } else {
            return "adminLogin";
        }
    }

 
    // 2. Dashboard (Welcome Page)
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/AdminLogin"; // In case someone tries to bypass login
        }

        model.addAttribute("adminId", admin.getAdminId()); //Inject adminId
        model.addAttribute("admin", admin);  // Add this line
        model.addAttribute("alumniList", alumniService.fetchAllAlumni());
     
        
        return "AdminPage"; // This should match your Thymeleaf template name
    }

    // 3. Fetch Alumni
    @GetMapping("/admin/alumni/fetch")
    public List<Alumni> fetchAllAlumni() {
        return alumniService.fetchAllAlumni();
    }

    // 4. Add Alumni
    @PostMapping("/admin/alumni/add")
    public String addAlumni(@ModelAttribute Alumni alumni) {
        alumniService.insertAlumni(alumni);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/alumni/update")
    public ResponseEntity<String> updateAlumniViaAjax(@RequestBody Alumni updatedAlumni) {
        alumniService.updateAlumni(updatedAlumni, updatedAlumni.getAlumniId());
        return ResponseEntity.ok("Alumni updated successfully");
    }
    
    // 5. Delete Alumni
    @PostMapping("/admin/alumni/delete")
    public String deleteAlumniViaForm(@RequestParam("alumniId") int id) {
        alumniService.deleteAlumni(id);
        return "redirect:/admin/dashboard";
    }
    
    @PostMapping("/admin/update-profile")
    @ResponseBody
    public ResponseEntity<String> updateAdminProfile(@RequestBody Admin updatedAdmin, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");

        if (loggedInAdmin == null) {
            return ResponseEntity.status(401).body("Unauthorized: Please login.");
        }

        // Copy updated fields
        loggedInAdmin.setFullName(updatedAdmin.getFullName());
        loggedInAdmin.setUsername(updatedAdmin.getUsername());
        loggedInAdmin.setPassword(updatedAdmin.getPassword());

        try {
            adminService.updateAdmin(loggedInAdmin, loggedInAdmin.getAdminId());
            session.setAttribute("loggedInAdmin", loggedInAdmin); // update session
            return ResponseEntity.ok("Admin profile updated successfully.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body("Update failed: " + ex.getMessage());
        }
    }

    
    // 6. Post Alumni Event
    @PostMapping("/admin/events/save")
    public String saveEvent(@ModelAttribute AlumniEvent event, HttpSession session) {
        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new IllegalArgumentException("Event ID is required.");
        }

        eventService.insertEvent(event);

        // ✅ Set flag in session so it survives redirect
        session.setAttribute("showViewEventsSection", true);

        return "redirect:/admin/dashboard";
    }
    @GetMapping("/admin/events/view")
    public String viewEventsPage(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/AdminLogin";
        }

        model.addAttribute("admin", admin);
        model.addAttribute("events", eventService.fetchAllEvents());
        return "ViewEvents"; // Matches the new HTML filename
    }


    // 7. View Activity Logs
    @GetMapping("/admin/logs")
    public List<ActivityLog> viewActivityLogs() {
        return activityLogService.fetchLogsByAdmin(1);  // Assuming 1 admin for now, adjust later
    }

    // 8. Logout (basic, just frontend redirect)
    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful");
    }
}
