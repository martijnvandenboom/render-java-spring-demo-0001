package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Hello from Java on Render 🚀";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
