package com.example.javacodedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class JavacodedemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavacodedemoApplication.class, args);
    }

}
