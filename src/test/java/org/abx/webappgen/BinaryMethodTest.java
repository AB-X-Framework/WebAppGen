package org.abx.webappgen;

import org.abx.services.ServiceRequest;
import org.abx.services.ServiceResponse;
import org.abx.services.ServicesClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest(classes = org.abx.webappgen.spring.ABXWebAppGen.class)
class BinaryMethodTest {

    private static ConfigurableApplicationContext context;
    @Autowired
    ServicesClient servicesClient;


    @BeforeAll
    public static void setup() {
        context= SpringApplication.run(org.abx.webappgen.spring.ABXWebAppGen.class);
    }

    @Test
    public void doBasicTest() throws Exception {
        ServiceRequest req = servicesClient.post("app", "/process/demo.uppercase").addPart(
                "args","{}").addPart("data","abcd".getBytes(),"mydata.data");
        ServiceResponse res = servicesClient.process(req);
        System.out.println(res.asString());
        Assertions.assertEquals("ABCD",res.asString());
    }

    @AfterAll
    public static void teardown() {
        context.stop();
    }

}
