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
        String filename = "resources/mydata.data";
        ServiceRequest req = servicesClient.post("app", "/session/login");
        req.addPart("username", "root@abxwebappgen.org");
        req.addPart("password", "12345");
        String data = "w4324235l2k34jsd";
        System.out.println("STATUS " + servicesClient.process(req).asString());
        req = servicesClient.post("app", "/resources/binaries").
                addPart("contentType", "application/octet-stream").
                addPart("role", "Admin").
                addPart("name", filename);
        ServiceResponse res = servicesClient.process(req);
        System.out.println(res.asString());
        Assertions.assertTrue(res.asJSONObject().getBoolean("success"));
        req =servicesClient.post("app", "/resources/binaries/upload").
                addPart("data", data.getBytes(), "data").
                addPart("name", filename);
        res = servicesClient.process(req);
        System.out.println(res.asString());
        Assertions.assertTrue(res.asJSONObject().getBoolean("success"));

        req = servicesClient.get("app", "/resources/binary/" + filename);
        res = servicesClient.process(req);
        Assertions.assertEquals("application/octet-stream", res.headers().get("Content-Type").getFirst());
        Assertions.assertEquals(data, res.asString());

    }

    @AfterAll
    public static void teardown() {
        context.stop();
    }

}
