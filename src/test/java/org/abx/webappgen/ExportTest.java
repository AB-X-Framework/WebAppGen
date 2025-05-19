package org.abx.webappgen;

import org.abx.services.ServiceRequest;
import org.abx.services.ServiceResponse;
import org.abx.services.ServicesClient;
import org.abx.util.StreamUtils;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.utils.SpecsExporter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.FileInputStream;

@SpringBootTest(classes = org.abx.webappgen.spring.ABXWebAppGen.class)
class ExportTest {

    private static ConfigurableApplicationContext context;
    @Autowired
    SpecsExporter specsExporter;


    @Value("${app.specs}")
    public String appSpecsPath;

    @BeforeAll
    public static void setup() {
        context = SpringApplication.run(org.abx.webappgen.spring.ABXWebAppGen.class);
    }

    @Test
    public void doBasicTest() throws Exception {
        String folderName = "src/test/gen";
        File specsFolder = new File(folderName);
        Assertions.assertFalse(specsFolder.exists());
        try {
            specsFolder.mkdirs();
            specsExporter.createSpecs(folderName);
            JSONObject generated =
                    new JSONObject(StreamUtils.readStream(new FileInputStream(folderName+"/specs.json")));
            JSONObject original =
                    new JSONObject(StreamUtils.readStream(new FileInputStream(appSpecsPath+"/specs.json")));
            assertJsonEquals(generated,original);
        }finally {
            deleteDir(specsFolder);
        }

    }
    private void deleteDir(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                deleteDir(file);
            }
        }
         dir.delete();
    }

    @AfterAll
    public static void teardown() {
        context.stop();
    }


    private void assertJsonEquals(Object expected, Object actual) {
        if (expected instanceof JSONObject && actual instanceof JSONObject) {
            JSONObject expectedJson = (JSONObject) expected;
            JSONObject actualJson = (JSONObject) actual;

            Assertions.assertEquals(expectedJson.keySet(), actualJson.keySet(), "Key sets don't match");

            for (String key : expectedJson.keySet()) {
                assertJsonEquals(expectedJson.get(key), actualJson.get(key));
            }

        } else if (expected instanceof JSONArray && actual instanceof JSONArray) {
            JSONArray expectedArr = (JSONArray) expected;
            JSONArray actualArr = (JSONArray) actual;

            Assertions.assertEquals(expectedArr.length(), actualArr.length(), "Array lengths don't match");

            for (int i = 0; i < expectedArr.length(); i++) {
                assertJsonEquals(expectedArr.get(i), actualArr.get(i));
            }

        } else {
            Assertions.assertEquals(expected, actual, "Values don't match");
        }
    }

}
