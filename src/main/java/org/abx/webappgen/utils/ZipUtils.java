package org.abx.webappgen.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.zip.ZipEntry;
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
}
