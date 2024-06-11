package com.DocGenNG.serviceImpl;

import com.DocGenNG.asyncJob.AsyncJobExecutor;
import com.DocGenNG.model.request.DocumentsRequest;
import com.DocGenNG.service.DocGenNgService;
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

@Service
public class DocGenNgServiceImpl implements DocGenNgService {
    private static final String FILE_DIRECTORY = "processed_files/";
    AsyncJobExecutor asyncJobExecutor = new AsyncJobExecutor();

    @Async
    public String processFile(MultipartFile file, DocumentsRequest request) throws IOException, InterruptedException {
        // Create directory if not exists
        Path directory = Paths.get(FILE_DIRECTORY);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        String fileId = UUID.randomUUID().toString();

        // Save file to disk asynchronously
        asyncJobExecutor.executeAsyncJob(() -> {
            try {
                // Save file to disk
                Path filePath = directory.resolve(fileId + ".xlsx");
                try (OutputStream os = Files.newOutputStream(filePath)) {
                    Thread.sleep(10000);
                    os.write(file.getBytes());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Perform any additional processing if needed (e.g., creating a workbook)
                try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                    // Example: Just saving the workbook back
                    try (OutputStream os = Files.newOutputStream(filePath)) {
                        workbook.write(os);
                    }
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        asyncJobExecutor.shutdown();

        // Simulate a delay here if needed
//         Thread.sleep(5000);

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
