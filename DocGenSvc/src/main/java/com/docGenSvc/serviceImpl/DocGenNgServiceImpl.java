package com.docGenSvc.serviceImpl;


import com.docGenSvc.exception.DocGenNGException;
import com.docGenSvc.model.quoteXWrapper.QuoteXWrapper;
import com.docGenSvc.properties.DocGenProperties;
import com.docGenSvc.service.DocGenNgRepoService;
import org.springframework.stereotype.Service;

import java.io.*;

import com.docGenSvc.model.request.DocGenData;
import com.docGenSvc.service.DocGenNgService;
import com.docGenSvc.utility.DocGenUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Service
public class DocGenNgServiceImpl implements DocGenNgService {
    private static final Logger logger = LoggerFactory.getLogger(DocGenNgServiceImpl.class);

    private static final String FILE_DIRECTORY = "processed_files/";


    @Autowired
    private DocGenUtility docGenUtility;
    @Autowired
    private DocGenProperties docGenProperties;
    @Autowired
    private DocGenNgRepoService docGenNgRepoService;


    public String processFile(String requestId, String trace, DocGenData request) {

        docGenUtility.validateRequest(request);

        String ticketNumber = docGenUtility.ticketGenerator(request.getQuoteId());


        docGenUtility.checkDuplicateRequest(requestId);
        docGenUtility.addDocumentStatus(requestId, ticketNumber);

        generateFile(ticketNumber, requestId, request);

        return ticketNumber;
    }


    @Async
    public CompletableFuture<Void> generateFile(String fileId, String requestId, DocGenData request) {
        return CompletableFuture.runAsync(() -> {
            Object quoteXData = switch (request.getClientId()) {
                case "PROS" -> docGenUtility.getQuoteXData(request); // comes from constant
                default -> docGenUtility.getQuoteXData(request);
            };
            logger.info("QuoteX service call{}", quoteXData.toString());


            try {

                File dataFile = docGenUtility.copyTemplate(docGenProperties.getTemplateFile());
                Thread.sleep(docGenProperties.getDocGenLag());
                File docGenReport = docGenUtility.generateDocGenReport(dataFile, request, (QuoteXWrapper) quoteXData);
                File generatedData = docGenUtility.deleteSelectedSheet(dataFile, docGenUtility.sheetsToBeDelete());
                String filePath = null;
                String generatedFilePath = docGenNgRepoService.moveFileToServerPath(generatedData, filePath);
                docGenUtility.updateDocumentStatus(requestId, generatedFilePath);
                logger.info("final file generated {}", generatedData);
            } catch (DocGenNGException e) {
                throw new DocGenNGException(e, e.getMessage(), "400.00.1000");
            } catch (Exception e) {
                throw new DocGenNGException(e, e.getMessage(), "500.00.1000");
            }
            logger.info("successfully document created !!!");
        });
    }

    public boolean isFileReady(String fileId) {
        Path filePath = Paths.get(FILE_DIRECTORY, fileId + ".xlsx");
        return Files.exists(filePath);
    }

    public byte[] getFile(String fileId) throws IOException {
        Path filePath = Paths.get(FILE_DIRECTORY, fileId + ".xlsx");
        return Files.readAllBytes(filePath);
    }
}
