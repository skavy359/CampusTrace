package com.example.apigateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/lost-items")
    public String lostItems() {
        return "lost-items";
    }

    @GetMapping("/found-items")
    public String foundItems() {
        return "found-items";
    }

    @GetMapping("/add-lost-item")
    public String addLostItem() {
        return "add-lost-item";
    }

    @GetMapping("/add-found-item")
    public String addFoundItem() {
        return "add-found-item";
    }

    @GetMapping("/claim-item")
    public String claimItem() {
        return "claim-item";
    }

    @GetMapping("/notifications")
    public String notifications() {
        return "notifications";
    }

    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin-dashboard";
    }
}
