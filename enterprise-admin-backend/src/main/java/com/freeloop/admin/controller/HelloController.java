package com.freeloop.admin.controller;

import com.freeloop.admin.dto.GreetingRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @GetMapping("/greet")
    public String greet(
            @RequestParam(value = "name", defaultValue = "Guest") String name) {
        return "Hello " + name;
    }

    @PostMapping("/greet")
    public String greetByBody(@RequestBody GreetingRequest request) {
        return "Hello " + request.getName();
    }
}
