package com.telecom.invoicePdfGeneration.services.impl;

import com.telecom.invoicePdfGeneration.services.PdfStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.io.File;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfStorageServiceImpl implements PdfStorageService {

    @Value("${file.path}")
    private String baseStoragePath;

    @Override
    public String storePdf(byte[] pdfContent, String msisdn) {
        LocalDate date = LocalDate.now();
        String month = date.getMonth().toString();
        
        File theDir = new File( baseStoragePath +"/pdfs/" + month + "/");
		log.info("Dir path : {}", theDir);
		if (!theDir.exists()) {
			theDir.mkdirs();
        }

        try {
    
            Path storageDirectory = Paths.get(theDir.getAbsolutePath());
            // createDirectoryIfNotExists(storageDirectory);

            String fileName = generateFileName(msisdn);
            Path filePath = storageDirectory.resolve(fileName);

            // Files.write will overwrite the file if it already exists
            Files.write(filePath, pdfContent);

            return "http://localhost:8080/api/invoices/download/" + month + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store PDF file for MSISDN: " + msisdn, e);
        }
    }

    @Override
    public Resource loadPdfAsResource(String fileName) {
        try {
            Path filePath = Paths.get(baseStoragePath, "pdfs").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable: " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading file: " + fileName, e);
        }
    }

    // private void createDirectoryIfNotExists(Path directory) throws IOException {
    //     if (!Files.exists(directory)) {
    //         Files.createDirectories(directory);
    //     }
    // }

    private String generateFileName(String msisdn) {
        return msisdn + ".pdf";
    }
}
