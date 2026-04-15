package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("org.example.mapper")
@SpringBootApplication(scanBasePackages = {"org.example.*", "org.example.controller"})

public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}