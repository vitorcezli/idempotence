package com.example.idempotence;

import com.example.idempotence.idempotent.annotations.Idempotent;
import org.springframework.stereotype.Service;

@Service
public class TestingService {

    @Idempotent
    public void printSomething(String prefix, String suffix) {
        System.out.printf("%s\t%s\n", prefix, suffix);
    }
}
