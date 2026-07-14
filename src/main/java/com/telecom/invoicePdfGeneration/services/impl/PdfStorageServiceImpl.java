package com.telecom.invoicePdfGeneration.services.impl;

import com.telecom.invoicePdfGeneration.services.PdfStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PdfStorageServiceImpl implements PdfStorageService {

    @Value("${storage.pdf.path:pdf/}")
    private String baseStoragePath;

    @Override
    public String storePdf(byte[] pdfContent, String msisdn) {
        try {
            Path storageDirectory = Paths.get(baseStoragePath);
            createDirectoryIfNotExists(storageDirectory);

            String fileName = generateFileName(msisdn);
            Path filePath = storageDirectory.resolve(fileName);

            // Files.write will overwrite the file if it already exists
            Files.write(filePath, pdfContent);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store PDF file for MSISDN: " + msisdn, e);
        }
    }

    private void createDirectoryIfNotExists(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
    }

    private String generateFileName(String msisdn) {
        return msisdn + ".pdf";
    }
}
