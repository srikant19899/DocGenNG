package com.DocGenNG.serviceImpl;

import com.DocGenNG.model.request.DocumentsRequest;
import com.DocGenNG.service.DocGenNgService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class DocGenNgServiceImpl implements DocGenNgService {
    private static final String FILE_DIRECTORY = "processed_files/";

    public String processFile(MultipartFile file, DocumentsRequest request) throws IOException {
        // Create directory if not exists
        Path directory = Paths.get(FILE_DIRECTORY);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        // Save file to disk
        String fileId = UUID.randomUUID().toString();
        Path filePath = directory.resolve(fileId + ".xlsx");
        try (OutputStream os = Files.newOutputStream(filePath)) {
            os.write(file.getBytes());
        }

        // Perform any additional processing if needed (e.g., creating a workbook)
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            // Example: Just saving the workbook back
            try (OutputStream os = Files.newOutputStream(filePath)) {
                workbook.write(os);
            }
        }

        return fileId;
    }



    public boolean isFileReady(String fileId) {
        // Check if the file exists
        Path filePath = Paths.get(FILE_DIRECTORY, fileId + ".xlsx");
        return Files.exists(filePath);
    }

    public byte[] getFile(String fileId) throws IOException {
        Path filePath = Paths.get(FILE_DIRECTORY, fileId + ".xlsx");
        return Files.readAllBytes(filePath);
    }
}
