package com.abx.webappgen.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.abx.webappgen.spring",
        "com.abx.webappgen.creds",
        "org.abx.services",
        "com.abx.webappgen.controller"})
public class ABXWebAppGen {

    public static void main(String[] args) {
        SpringApplication.run(ABXWebAppGen.class, args);

    }

}
