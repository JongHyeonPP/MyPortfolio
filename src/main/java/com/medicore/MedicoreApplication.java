package com.medicore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MedicoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedicoreApplication.class, args);
    }
}
