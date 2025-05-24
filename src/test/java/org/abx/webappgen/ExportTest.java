package org.abx.webappgen;

import org.abx.util.StreamUtils;
import org.abx.webappgen.utils.SpecsExporter;
import org.json.JSONArray;
import org.json.JSONException;
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
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            assertJsonEquals("", generated, original);
            compareFolders(folderName+"/binary",appSpecsPath+"/binary");
            compareFolders(folderName+"/text",appSpecsPath+"/text");
            compareFolders(folderName+"/methods",appSpecsPath+"/methods");
            compareJsonStringArraysInFolders(folderName+"/array",appSpecsPath+"/array");
            compareJsonStringObjectsInFolders(folderName+"/map",appSpecsPath+"/map");
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

    public static void assertJsonEquals(String path, Object expected, Object actual) {
        if (expected instanceof JSONObject expectedJson && actual instanceof JSONObject actualJson) {
            Assertions.assertEquals(expectedJson.keySet(), actualJson.keySet(), "Key sets don't match");
            for (String key : expectedJson.keySet()) {
                assertJsonEquals(path + "/" + key, expectedJson.get(key), actualJson.get(key));
            }
        } else if (expected instanceof JSONArray expectedArr && actual instanceof JSONArray actualArr) {
            assertJsonArraysEqualIgnoringOrder(path, expectedArr, actualArr);
        } else {
            Assertions.assertEquals(expected, actual, "Values don't match on path " + path);
        }
    }

    private static void assertJsonArraysEqualIgnoringOrder(String path, JSONArray expectedArr, JSONArray actualArr) {
        Assertions.assertEquals(expectedArr.length(), actualArr.length(), "Array lengths differ");

        boolean[] matched = new boolean[actualArr.length()];

        for (int i = 0; i < expectedArr.length(); i++) {
            Object expectedElem = expectedArr.get(i);
            boolean foundMatch = false;

            for (int j = 0; j < actualArr.length(); j++) {
                if (matched[j]) continue;

                try {
                    assertJsonEquals(path + "[]/", expectedElem, actualArr.get(j));
                    matched[j] = true;
                    foundMatch = true;
                    break;
                } catch (AssertionError ignored) {
                    // not a match, keep looking
                }
            }

            if (!foundMatch) {
                if (expectedElem instanceof JSONArray jsonExpect) {
                    Assertions.fail("No match found for JSONArray element: " + jsonExpect.toString(2) + " on path " + path);
                } else if (expectedElem instanceof JSONObject jsonExpect) {
                    Assertions.fail("No match found for JSONObject element: " + jsonExpect.toString(2) + " on path " + path);
                } else {

                    Assertions.fail("No match found for element: " + expectedElem + " on path " + path);

                }

            }
        }
    }


    private void compareFolders(String folderPath1, String folderPath2) throws IOException {
        Path dir1 = Paths.get(folderPath1).toAbsolutePath();
        Path dir2 = Paths.get(folderPath2).toAbsolutePath();

        // Get all files in both folders as relative paths
        List<Path> files1 = Files.walk(dir1)
                .filter(Files::isRegularFile)
                .map(dir1::relativize)
                .collect(Collectors.toList());

        List<Path> files2 = Files.walk(dir2)
                .filter(Files::isRegularFile)
                .map(dir2::relativize)
                .collect(Collectors.toList());

        // Compare file sets
        Set<Path> set1 = new HashSet<>(files1);
        Set<Path> set2 = new HashSet<>(files2);

        if (!set1.equals(set2)) {
            throw new AssertionError("Folders contain different files:\nOnly in first: " +
                    diff(set1, set2) + "\nOnly in second: " + diff(set2, set1));
        }

        // Compare checksums
        for (Path relative : set1) {
            Path file1 = dir1.resolve(relative);
            Path file2 = dir2.resolve(relative);

            //No checksum for .json files
            if (file1.toString().endsWith(".json")){
                continue;
            }
            String hash1 = sha256(file1);
            String hash2 = sha256(file2);

            if (!hash1.equals(hash2)) {
                throw new AssertionError("Checksum mismatch for file: " + relative +
                        "\n" + hash1 + " != " + hash2);
            }
        }

        System.out.println("Folders match: structure and contents are identical.");
    }

    private  String sha256(Path path) throws IOException {
        try (DigestInputStream dis = new DigestInputStream(Files.newInputStream(path), MessageDigest.getInstance("SHA-256"))) {
            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1) {}
            byte[] digest = dis.getMessageDigest().digest();
            return bytesToHex(digest);
        } catch (Exception e) {
            throw new IOException("Failed to compute hash for " + path, e);
        }
    }

    private  String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private  Set<Path> diff(Set<Path> a, Set<Path> b) {
        return a.stream().filter(p -> !b.contains(p)).collect(Collectors.toSet());
    }


    public static void compareJsonStringArraysInFolders(String folderPath1, String folderPath2) throws IOException {
        Path dir1 = Paths.get(folderPath1);
        Path dir2 = Paths.get(folderPath2);

        if (!Files.isDirectory(dir1) || !Files.isDirectory(dir2)) {
            throw new IllegalArgumentException("Both paths must be directories.");
        }

        Set<String> fileNames1 = listFileNames(dir1);
        Set<String> fileNames2 = listFileNames(dir2);

        if (!fileNames1.equals(fileNames2)) {
            throw new AssertionError("Folders do not contain the same files.\nOnly in folder1: "
                    + diff2(fileNames1, fileNames2) + "\nOnly in folder2: " + diff2(fileNames2, fileNames1));
        }

        for (String fileName : fileNames1) {
            Path file1 = dir1.resolve(fileName);
            Path file2 = dir2.resolve(fileName);

            JSONArray arr1 = parseJsonArray(Files.readString(file1), file1);
            JSONArray arr2 = parseJsonArray(Files.readString(file2), file2);

            if (arr1.length() != arr2.length()) {
                throw new AssertionError("Length mismatch in file: " + fileName +
                        " (folder1: " + arr1.length() + ", folder2: " + arr2.length() + ")");
            }

            for (int i = 0; i < arr1.length(); i++) {
                String val1 = arr1.getString(i);
                String val2 = arr2.getString(i);
                if (!val1.equals(val2)) {
                    throw new AssertionError("Mismatch at index " + i + " in file " + fileName +
                            "\nfolder1: " + val1 + "\nfolder2: " + val2);
                }
            }
        }

        System.out.println("All files have matching String arrays in order.");
    }

    private static JSONArray parseJsonArray(String content, Path path) {
        try {
            return new JSONArray(content);
        } catch (JSONException e) {
            throw new RuntimeException("Invalid JSON array in file: " + path, e);
        }
    }

    private static Set<String> listFileNames(Path dir) throws IOException {
        Set<String> names = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    names.add(file.getFileName().toString());
                }
            }
        }
        return names;
    }

    private static Set<String> diff2(Set<String> a, Set<String> b) {
        Set<String> result = new HashSet<>(a);
        result.removeAll(b);
        return result;
    }
    public static void compareJsonStringObjectsInFolders(String folderPath1, String folderPath2) throws IOException {
        Path dir1 = Paths.get(folderPath1);
        Path dir2 = Paths.get(folderPath2);

        if (!Files.isDirectory(dir1) || !Files.isDirectory(dir2)) {
            throw new IllegalArgumentException("Both paths must be directories.");
        }

        Set<String> fileNames1 = listFileNames(dir1);
        Set<String> fileNames2 = listFileNames(dir2);

        if (!fileNames1.equals(fileNames2)) {
            throw new AssertionError("Folders do not contain the same files.\nOnly in folder1: "
                    + diff2(fileNames1, fileNames2) + "\nOnly in folder2: " + diff2(fileNames2, fileNames1));
        }

        for (String fileName : fileNames1) {
            Path file1 = dir1.resolve(fileName);
            Path file2 = dir2.resolve(fileName);

            JSONObject obj1 = parseJsonObject(Files.readString(file1), file1);
            JSONObject obj2 = parseJsonObject(Files.readString(file2), file2);

            if (!obj1.keySet().equals(obj2.keySet())) {
                throw new AssertionError("Key set mismatch in file: " + fileName +
                        "\nKeys in folder1: " + obj1.keySet() +
                        "\nKeys in folder2: " + obj2.keySet());
            }

            for (String key : obj1.keySet()) {
                String val1 = obj1.getString(key);
                String val2 = obj2.getString(key);
                if (!val1.equals(val2)) {
                    throw new AssertionError("Value mismatch for key '" + key + "' in file " + fileName +
                            "\nfolder1: " + val1 + "\nfolder2: " + val2);
                }
            }
        }

        System.out.println("All files contain identical flat JSONObjects of strings.");
    }

    private static JSONObject parseJsonObject(String content, Path path) {
        try {
            return new JSONObject(content);
        } catch (JSONException e) {
            throw new RuntimeException("Invalid JSON object in file: " + path, e);
        }
    }


}
