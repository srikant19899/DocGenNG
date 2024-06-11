package com.DocGenNG.serviceImpl;

import com.DocGenNG.asyncJob.AsyncJobExecutor;
import com.DocGenNG.model.request.DocumentsRequest;
import com.DocGenNG.service.DocGenNgService;
import com.DocGenNG.utility.DocGenUtility;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DocGenNgServiceImpl implements DocGenNgService {
    private static final String FILE_DIRECTORY = "processed_files/";
    AsyncJobExecutor asyncJobExecutor = new AsyncJobExecutor();
    DocGenUtility docGenUtility = new DocGenUtility();
    private final ExecutorService executor = Executors.newFixedThreadPool(10); // Adjust the pool size as needed



    public String processFile(MultipartFile file, DocumentsRequest request) throws IOException {
        // Create directory if not exists
        Path directory = Paths.get(FILE_DIRECTORY, request.getQuoteId());
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("Error creating directory: " + directory, e);
            }
        }

        // Generate file ID
        String fileId = docGenUtility.docNameCreator(request.getQuoteId());

        // Save file to disk asynchronously
        asyncJobExecutor.executeAsyncJob(() -> {
            Path filePath = directory.resolve(fileId + ".xlsx");
            try (OutputStream os = Files.newOutputStream(filePath)) {
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
                os.write(file.getBytes());

                // Additional processing if needed (e.g., creating a workbook)
                try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                    // Example: Just saving the workbook back
                    try (OutputStream os2 = Files.newOutputStream(filePath)) {
                        workbook.write(os2);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error saving file: " + filePath, e);
            }
            System.out.println("Task complete for file: " + fileId);
        });

        // Return fileId immediately
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


    private void fileGeneratorMock() throws InterruptedException {
        Thread.sleep(10000);
    }
}
