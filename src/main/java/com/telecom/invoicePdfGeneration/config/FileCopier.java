package com.telecom.invoicePdfGeneration.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class FileCopier {
    @PostConstruct
    public void init() {
        try {
            Path src = Paths.get("/Users/rishiseth/.gemini/antigravity-ide/brain/85a44a09-424e-4bfb-8e89-9c57a533e2e2/media__1784008889961.png");
            Path destDir = Paths.get("/Users/rishiseth/Desktop/invoicePdfGeneration/src/main/resources/images");
            Files.createDirectories(destDir);
            Path dest = destDir.resolve("logo.png");
            if (Files.exists(src)) {
                Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("COPIED LOGO SUCCESSFULLY!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
