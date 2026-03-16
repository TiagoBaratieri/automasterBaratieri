package com.baratieri.automasterbaratieri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AutoMasterBaratieriApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoMasterBaratieriApplication.class, args);
    }

}
