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
class ABXWebAppGenTest {

	private static ConfigurableApplicationContext context;
	@Autowired
	ServicesClient servicesClient;


	@BeforeAll
	public static void setup() {
		context= SpringApplication.run(org.abx.webappgen.spring.ABXWebAppGen.class);
	}

	@Test
	public void doBasicTest() throws Exception {
		ServiceRequest req = servicesClient.post("app", "/session/login");
		req.addPart("username","root@abxwebappgen.org");
		req.addPart("password","12345");
		ServiceResponse res = servicesClient.process(req);
		System.out.println(res.asString());
		Assertions.assertTrue(res.asJSONObject().getBoolean("logged"));
	}

	@AfterAll
	public static void teardown() {
		context.stop();
	}

}
