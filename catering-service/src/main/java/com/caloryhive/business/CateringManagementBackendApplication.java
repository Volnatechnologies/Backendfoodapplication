package com.caloryhive.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CateringManagementBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(CateringManagementBackendApplication.class, args);
    }
}
