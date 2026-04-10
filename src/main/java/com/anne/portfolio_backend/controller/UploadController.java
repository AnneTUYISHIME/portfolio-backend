package com.anne.portfolio_backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @GetMapping("/test")
    public String test() {
        return "Upload controller is working!";
    }
}