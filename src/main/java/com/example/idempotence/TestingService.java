package com.example.idempotence;

import com.example.idempotence.idempotent.annotations.Idempotent;
import org.springframework.stereotype.Service;

@Service
public class TestingService {

    @Idempotent
    public String getSomething(String prefix, String suffix) {
        return String.format("%s\t%s\n", prefix, suffix);
    }
}
