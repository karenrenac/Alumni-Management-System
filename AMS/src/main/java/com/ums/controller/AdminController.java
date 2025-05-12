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

import java.sql.Timestamp;
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
/*
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
*/
 
    // 2. Dashboard (Welcome Page)
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/AdminLogin"; // In case someone tries to bypass login
        }

        model.addAttribute("adminId", admin.getAdminId()); //Inject adminId
        model.addAttribute("admin", admin);  // Add this line
        //model.addAttribute("alumniList", alumniService.fetchAllAlumni());
     
        
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
    	 if (alumni.getImageUrl() == null || alumni.getImageUrl().isBlank()) {
    	        alumni.setImageUrl("/images/default_alumni.png");
    	        System.out.println("✅ Default image URL set.");
    	    } else {
    	        System.out.println("🟡 Submitted image URL: " + alumni.getImageUrl());
    	    }
    	 alumni.setPasswordChangeRequired(false);
    	
    	
        alumniService.insertAlumni(alumni);
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/admin/alumni/view")
    public String viewAlumniDirectory(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/AdminLogin";
        }

        List<Alumni> alumniList = alumniService.fetchAllAlumni();
        model.addAttribute("admin", admin);
        model.addAttribute("allAlumni", alumniList);

        return "AdminAlumniDirectory"; // ✅ This is your new HTML page
    }

    
    @PostMapping("/admin/alumni/update")
    public String updateAlumni(@ModelAttribute Alumni alumni) {
        Alumni existing = alumniService.fetchAlumniById(alumni.getAlumniId());

        // ✅ Update allowed fields
        existing.setName(alumni.getName());
        existing.setEmail(alumni.getEmail());
        existing.setUsername(alumni.getUsername());
        existing.setGraduationYear(alumni.getGraduationYear());
        existing.setBranch(alumni.getBranch());
        existing.setCompanyName(alumni.getCompanyName());
        existing.setJobTitle(alumni.getJobTitle());

        // ✅ Preserve existing image if none was submitted
        if (alumni.getImageUrl() != null && !alumni.getImageUrl().isBlank()) {
            existing.setImageUrl(alumni.getImageUrl());
        }

        // ✅ Preserve required fields not present in the form
        existing.setPassword(alumni.getPassword() != null ? alumni.getPassword() : existing.getPassword());
        existing.setUniversityId(existing.getUniversityId());
        existing.setPasswordChangeRequired(existing.isPasswordChangeRequired());

        alumniService.updateAlumni(existing, existing.getAlumniId());
        return "redirect:/admin/alumni/view";
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
        return "AdminViewEvents"; // Matches the new HTML filename
    }
    
    @GetMapping("/admin/edit-event/{eventId}")
    public String showEditEventForm(@PathVariable String eventId, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/AdminLogin";
        }

        AlumniEvent event = eventService.fetchEventById(eventId);
        model.addAttribute("event", event);
        model.addAttribute("admin", admin);
        return "AdminViewEvents";  // This should match your new Thymeleaf edit page
    }
    
    @PostMapping("/admin/delete-event")
    public String deleteEvent(@RequestParam String eventId, HttpSession session) {
        eventService.deleteEvent(eventId);

        // Activity Log
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin != null) {
            ActivityLog log = new ActivityLog();
            log.setAdminId(admin.getAdminId());
            log.setAction("Deleted event with ID: " + eventId);
            log.setEntityType("AlumniEvent");
            log.setEntityId(eventId);
            log.setTimestamp(new Timestamp(System.currentTimeMillis()));
            activityLogService.insertLog(log);
        }

        return "redirect:/admin/events/view";
    }

    
    // 7. View Activity Logs
    @GetMapping("/admin/logs")
    public List<ActivityLog> viewActivityLogs() {
        return activityLogService.fetchLogsByAdmin(1);  // Assuming 1 admin for now, adjust later
    }

/*    
    // 8. Logout (basic, just frontend redirect)
    @GetMapping("/admin/logout")
    public String logoutAdmin() {
       
        return "redirect:/AdminLogin";
    }
*/
}
