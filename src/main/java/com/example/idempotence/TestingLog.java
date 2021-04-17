package com.example.idempotence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class TestingLog {

    @Autowired
    private TestingService testingService;

    @GetMapping
    public ResponseEntity getResponse() {
        final String result = testingService.getSomething("hello", "world");
        System.out.print(result);
        return ResponseEntity.ok().build();
    }
}
