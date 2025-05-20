package org.abx.webappgen.utils;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static byte[] zipFolderToByteArray(Path folderPath) throws IOException {
        if (!Files.isDirectory(folderPath)) {
            throw new IllegalArgumentException("Provided path is not a folder: " + folderPath);
        }

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        try (ZipOutputStream zipOut = new ZipOutputStream(byteOut)) {
            Path basePath = folderPath.toAbsolutePath();

            Files.walk(folderPath).filter(path -> !Files.isDirectory(path)).forEach(path -> {
                ZipEntry zipEntry = new ZipEntry(basePath.relativize(path).toString().replace("\\", "/"));
                try {
                    zipOut.putNextEntry(zipEntry);
                    Files.copy(path, zipOut);
                    zipOut.closeEntry();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }

        return byteOut.toByteArray();
    }

    public static void delete(Path path) throws IOException {
        if (!Files.exists(path)) return;
        Files.walk(path)
                .sorted(Comparator.reverseOrder()) // delete files before directories
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete: " + p, e);
                    }
                });
    }


    public static String unzipToTempFolder(byte[] zipBytes) throws IOException {
        // Create a temporary directory
        Path tempDir = Files.createTempDirectory("unzipped_");

        try (ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
             ZipInputStream zis = new ZipInputStream(bais)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path newPath = resolveZipEntry(tempDir, entry);

                if (entry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    Files.createDirectories(newPath.getParent());
                    try (OutputStream os = Files.newOutputStream(newPath)) {
                        zis.transferTo(os);
                    }
                }
                zis.closeEntry();
            }
        }

        return tempDir.toAbsolutePath().toString();
    }
    // Prevent Zip Slip vulnerability
    private static Path resolveZipEntry(Path targetDir, ZipEntry entry) throws IOException {
        Path resolvedPath = targetDir.resolve(entry.getName()).normalize();
        if (!resolvedPath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + entry.getName());
        }
        return resolvedPath;
    }
}
