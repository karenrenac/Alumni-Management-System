package com.ums.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Serve the main home page
    @GetMapping("/")
    public String home() {
        return "homepage"; // Will load homepage.html from templates or static
    }

    // (Optional) Serve a logout success page
    @GetMapping("/logout-success")
    public String logoutSuccess() {
        return "logout-success"; // You can create a simple logout-success.html
    }

    // (Optional) Serve a login error page
    @GetMapping("/login-error")
    public String loginError() {
        return "login-error"; // You can create a simple login-error.html
    }
}
