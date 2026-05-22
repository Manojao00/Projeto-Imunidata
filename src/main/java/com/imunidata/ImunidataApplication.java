package com.imunidata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ImunidataApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImunidataApplication.class, args);
    }
}
