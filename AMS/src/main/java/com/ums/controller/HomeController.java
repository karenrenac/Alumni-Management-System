package com.ums.controller;

import com.ums.model.Alumni;
import com.ums.service.AlumniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private AlumniService alumniService;

    // Serve the main home page
    @GetMapping("/")
    public String home() {
        return "homepage";
    }

    // Serve a logout success page (optional)
    @GetMapping("/logout-success")
    public String logoutSuccess() {
        return "logout-success";
    }

    // Serve a login error page (optional)
    @GetMapping("/login-error")
    public String loginError() {
        return "login-error";
    }

    // ✅ Public alumni directory
    @GetMapping("/view-alumni")
    public String showPublicAlumniDirectory(Model model) {
        List<Alumni> alumniList = alumniService.fetchAllAlumni();
        model.addAttribute("allAlumni", alumniList);
        return "PublicAlumniDirectory"; // Match this with your template file name
    }
}
