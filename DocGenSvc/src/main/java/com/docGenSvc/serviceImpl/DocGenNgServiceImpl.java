package com.docGenSvc.serviceImpl;


import com.docGenSvc.exception.DocGenNGException;
import com.docGenSvc.model.quoteXWrapper.QuoteXWrapper;
import com.docGenSvc.properties.DocGenProperties;
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


    public String processFile(String requestId, String trace, DocGenData request) {

        docGenUtility.validateRequest(request);

        String ticketNumber = docGenUtility.ticketGenerator(request.getQuoteId());

        docGenUtility.addDocumentStatus(requestId, ticketNumber);

        // call  asynchronously and do computation accordingliy

        generateFile(ticketNumber, requestId, request);

        return ticketNumber;
    }

    /*
    utility -> logger define -> error, info, debug, audit= should use slf4j
    error => string msg of error, exception object, object{req/res}
    info=> string msg , object === info comman use
    debug = > same as info
    audit=> audit( starttime, Object, rotuingKey,requestId, object, endTime)
    take proper naming
       - write a method for creation csv file from master template and return type should be new generated csv file
        second method for take new csv file as input , doctype and templatename, Quatxservice response, quateId
        third method should take input of new csv file and it should be return type should be same csv file{ this method take list of string (contains mane of sheet)} delete that sheet and return as versin sheet
     */
    @Async
    public CompletableFuture<Void> generateFile(String fileId, String requestId, DocGenData request) {
        return CompletableFuture.runAsync(() -> {
            Object quoteXData = switch (request.getClientId()) {
                case "PROS" -> docGenUtility.getQuoteXData(request); // comes from constant
                default -> docGenUtility.getQuoteXData(request);
            };
            logger.info("QuoteX service call{}", quoteXData.toString());


            try {
                // duplicate check request then throw exp{400 BR }


                File dataFile = docGenUtility.copyTemplate(docGenProperties.getTemplateFile());
                Thread.sleep(docGenProperties.getDocGenLag());
                File docGenReport = docGenUtility.generateDocGenReport(dataFile,request,(QuoteXWrapper) quoteXData);
                // write a method to return list of sheet to be deleted .->
                File generatedData    = docGenUtility.deleteSelectedSheet(dataFile,docGenUtility.sheetsToBeDelete()) ;
                // file status update in DB and update path of file
                logger.info("final file generated {}", generatedData);
            } catch (DocGenNGException e) {
                throw new DocGenNGException(e, e.getMessage(), "400.00.1000");
            } catch (Exception e) {
                throw new DocGenNGException(e,e.getMessage(),"500.00.1000");
            }
            logger.info("successfully document created !!!");
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









}
