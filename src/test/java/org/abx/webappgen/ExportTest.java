package org.abx.webappgen;

import org.abx.util.StreamUtils;
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
                    new JSONObject(StreamUtils.readStream(new FileInputStream(folderName + "/specs.json")));
            JSONObject original =
                    new JSONObject(StreamUtils.readStream(new FileInputStream(appSpecsPath + "/specs.json")));
            assertJsonEquals(generated, original);
        } finally {
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

    public static void assertJsonEquals(Object expected, Object actual) {
        if (expected instanceof JSONObject expectedJson && actual instanceof JSONObject actualJson) {
            Assertions.assertEquals(expectedJson.keySet(), actualJson.keySet(), "Key sets don't match");
            for (String key : expectedJson.keySet()) {
                assertJsonEquals(expectedJson.get(key), actualJson.get(key));
            }
        } else if (expected instanceof JSONArray expectedArr && actual instanceof JSONArray actualArr) {
            assertJsonArraysEqualIgnoringOrder(expectedArr, actualArr);
        } else {
            Assertions.assertEquals(expected, actual, "Values don't match");
        }
    }

    private static void assertJsonArraysEqualIgnoringOrder(JSONArray expectedArr, JSONArray actualArr) {
        Assertions.assertEquals(expectedArr.length(), actualArr.length(), "Array lengths differ");

        boolean[] matched = new boolean[actualArr.length()];

        for (int i = 0; i < expectedArr.length(); i++) {
            Object expectedElem = expectedArr.get(i);
            boolean foundMatch = false;

            for (int j = 0; j < actualArr.length(); j++) {
                if (matched[j]) continue;

                try {
                    assertJsonEquals(expectedElem, actualArr.get(j));
                    matched[j] = true;
                    foundMatch = true;
                    break;
                } catch (AssertionError ignored) {
                    // not a match, keep looking
                }
            }

            if (!foundMatch) {
                Assertions.fail("No match found for array element: " + expectedElem);
            }
        }
    }
}
