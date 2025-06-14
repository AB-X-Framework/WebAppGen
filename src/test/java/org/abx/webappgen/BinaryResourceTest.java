package org.abx.webappgen;

import org.abx.services.ServiceRequest;
import org.abx.services.ServiceResponse;
import org.abx.services.ServicesClient;
import org.abx.webappgen.persistence.PageModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import static org.abx.webappgen.utils.ElementUtils.elementHashCode;

@SpringBootTest(classes = org.abx.webappgen.spring.ABXWebAppGen.class)
class BinaryResourceTest {

    private static ConfigurableApplicationContext context;
    @Autowired
    ServicesClient servicesClient;


    @BeforeAll
    public static void setup() {
        context = SpringApplication.run(org.abx.webappgen.spring.ABXWebAppGen.class);
    }

    @Test
    public void doBasicTest() throws Exception {
        servicesClient.enableCookies();
        String filename = "mydata.data";
        ServiceRequest req = servicesClient.post("app", "/session/login");
        req.addPart("username", "root@abxwebappgen.org");
        req.addPart("password", "12345");
        System.out.println("STATUS "+servicesClient.process(req).asString());
        req = servicesClient.post("app", "/resources/binaries").
                addPart("contentType", "application/octet-stream").
                addPart("role", "Admin").
                addPart("packageName", "myPackage").
                addPart("data", "abcd".getBytes(), filename);
        ServiceResponse res = servicesClient.process(req);
        System.out.println(res.asString());
        Assertions.assertEquals(elementHashCode(filename), res.asLong());

        req = servicesClient.get("app", "/resources/binaries/"+filename);
        res = servicesClient.process(req);
        Assertions.assertEquals(res.headers().get("Content-Type").getFirst(), "application/octet-stream");
        Assertions.assertEquals("abcd", res.asString());

    }

    @AfterAll
    public static void teardown() {
        context.stop();
    }

}
