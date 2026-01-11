package com.totaldocs;

import java.security.NoSuchAlgorithmException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.totaldocs")
public class ChecklistonlineApplication {
	public static void main(String[	] args) throws NoSuchAlgorithmException {
		SpringApplication.run(ChecklistonlineApplication.class, args);
	}
}
