package org.abx.webappgen.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.abx.webappgen.persistence",
        "org.abx.webappgen.spring",
        "org.abx.webappgen.utils",
        "org.abx.services",
        "org.abx.webappgen.controller"})
public class ABXWebAppGen {

    public static void main(String[] args) {
        SpringApplication.run(ABXWebAppGen.class, args);

    }

}
