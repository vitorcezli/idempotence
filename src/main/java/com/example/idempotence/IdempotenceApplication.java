package com.example.idempotence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@ComponentScan
public class IdempotenceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdempotenceApplication.class, args);
	}

}
