package com.DocGenNG.serviceImpl;


import com.DocGenNG.model.request.DocGenData;
import com.DocGenNG.service.DocGenNgService;
import com.DocGenNG.utility.DocGenUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Service
public class DocGenNgServiceImpl implements DocGenNgService {
    private static final Logger logger = LoggerFactory.getLogger(DocGenNgServiceImpl.class);

    private static final String FILE_DIRECTORY = "processed_files/";
    private final DocGenUtility docGenUtility;

    public DocGenNgServiceImpl(DocGenUtility docGenUtility) {
        this.docGenUtility = docGenUtility;
    }
    public String processFile(String requestId,String trace, DocGenData request) throws IOException, InterruptedException {
        // Create directory if not exists
        String quoteId = request.getQuoteId();

        if (FILE_DIRECTORY == null || FILE_DIRECTORY.isEmpty()) {
            throw new IllegalArgumentException("FILE_DIRECTORY is null or empty");
        }



        Path directory = Paths.get(FILE_DIRECTORY, quoteId);

        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
                logger.info("Directory created: {}", directory);
            } catch (IOException e) {
                logger.error("Error creating directory: {}", directory, e);
                throw new RuntimeException("Error creating directory: " + directory, e);
            }
        }

        // Generate file ID
        String fileId = docGenUtility.docNameCreator(request.getQuoteId());
        // Save file to disk asynchronously
        generateFile(directory, fileId, requestId, request);

        // Return fileId immediately
        return fileId;
    }
    @Async
    public CompletableFuture<Void> generateFile(Path directory, String fileId, String requestId, DocGenData request) {
        return CompletableFuture.runAsync(() -> {
           /* Path filePath = directory.resolve(fileId + ".xlsx");
            try (OutputStream os = Files.newOutputStream(filePath)) {
                // Simulate a delay
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
                System.out.println("Task complete for file: " + fileId);
            } catch (IOException e) {
                throw new RuntimeException("Error saving file: " + filePath, e);
            }*/
//            this for testing code
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("test run !!!");
        });
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
