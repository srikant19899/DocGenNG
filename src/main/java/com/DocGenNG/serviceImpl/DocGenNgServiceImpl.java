package com.DocGenNG.serviceImpl;


import com.DocGenNG.exception.DocumentProcessingException;
import com.DocGenNG.model.entity.DocGenEntity;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class DocGenNgServiceImpl implements DocGenNgService {
    private static final Logger logger = LoggerFactory.getLogger(DocGenNgServiceImpl.class);

    private static final String FILE_DIRECTORY = "processed_files/";
    private final DocGenUtility docGenUtility;
    private List<DocGenEntity> dbData ;
    public DocGenNgServiceImpl(DocGenUtility docGenUtility) {
        this.docGenUtility = docGenUtility;
        dbData= new ArrayList<>();
    }
    public String processFile(String requestId,String trace, DocGenData request) throws IOException, InterruptedException {

        if (FILE_DIRECTORY == null || FILE_DIRECTORY.isEmpty()) {
            throw new IllegalArgumentException("FILE_DIRECTORY is null or empty");
        }
        Path directory = Paths.get(FILE_DIRECTORY, request.getQuoteId());
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
        String ticketNumber = docGenUtility.docNameCreator(request.getQuoteId());
        // calling QuoteX service
        Object object = docGenUtility.callQuoteService();
        logger.info("QuoteX service call{}", object.toString());
        if(docGenUtility.checkDuplicateRequest(requestId)){
            throw  new DocumentProcessingException("your Document is processing !!");
        }
        docGenUtility.addFileStatusInDb(requestId,ticketNumber);


        // call  asynchronously and do computation accordingliy
        generateFile( ticketNumber, requestId, request);

        // Return fileId immediately
        return ticketNumber;
    }
    @Async
    public CompletableFuture<Void> generateFile( String fileId, String requestId, DocGenData request) {
        return CompletableFuture.runAsync(() -> {

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("test run !!!");
            docGenUtility.updateDocumentStatus(requestId);
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
