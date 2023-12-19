package com.colegiado.sistemacolegiado.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String indexController() {
        return "home";
    }
    @GetMapping("/home")
    public String homeController() {
        return "home";
    }
}
