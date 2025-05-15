package com.abx.app.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.abx.app.spring",
        "com.abx.app.creds",
        "org.abx.services",
        "com.abx.app.controller"})
public class ABXWebAppGen {

    public static void main(String[] args) {
        SpringApplication.run(ABXWebAppGen.class, args);

    }

}
