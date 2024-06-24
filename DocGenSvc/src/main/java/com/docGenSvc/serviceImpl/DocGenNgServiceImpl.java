package com.docGenSvc.serviceImpl;

import com.docGenSvc.model.quoteXWrapper.QuoteXWrapper;
import com.docGenSvc.properties.DocGenProperties;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;
import com.docGenSvc.model.request.DocGenData;
import com.docGenSvc.service.DocGenNgService;
import com.docGenSvc.utility.DocGenUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class DocGenNgServiceImpl implements DocGenNgService {
    private static final Logger logger = LoggerFactory.getLogger(DocGenNgServiceImpl.class);

    private static final String FILE_DIRECTORY = "processed_files/";
    private String copyTemplate = "CPEA_TEMPLATE_v3copy.xlsx";
    private String finalReport = "CPEA_TEMPLATE_v3copy_delete.xlsx";
    private String sheetNameToDelete = "PIVOT";

    @Autowired
    private DocGenUtility docGenUtility;
    @Autowired
    private DocGenProperties docGenProperties;


    public String processFile(String requestId, String trace, DocGenData request) {

        docGenUtility.validateRequest(request);

        String ticketNumber = docGenUtility.docNameCreator(request.getQuoteId());

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
                case "PROS" -> docGenUtility.getQuoteXData(request);
                default -> docGenUtility.getQuoteXData(request);
            };
            logger.info("QuoteX service call{}", quoteXData.toString());

            try {
                // create initial file status in DB
                File copyTemplateFile = copyTemplate(docGenProperties.getTemplateFile(), copyTemplate);
                Thread.sleep(docGenProperties.getDocGenLag());
//                File docGenReport = generateDocGenReport(copyTemplateFile,request,(QuoteXWrapper) quoteXData);
                File finalReport = deleteSelectedSheet(copyTemplateFile, this.finalReport, Arrays.asList(sheetNameToDelete));
                // file status update in DB and update path of file
                logger.info("final file generated {}", finalReport);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
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

    private File copyTemplate(String inputFilePath, String outputFilePath) throws Exception {
        ZipSecureFile.setMinInflateRatio(0.001);
        logger.info("Starting to copy file from {} to {}", inputFilePath, outputFilePath);
        ClassPathResource classPathResource = new ClassPathResource(inputFilePath);
        try (InputStream fis = classPathResource.getInputStream();
             Workbook workbook = new XSSFWorkbook(fis);
             FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            workbook.write(fos);
            logger.info("File copied successfully from {} to {}", inputFilePath, outputFilePath);
        } catch (IOException e) {
            logger.error("Error copying file: {}", e.getMessage(), e);
            throw new IOException(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
        return new File(outputFilePath);
    }

    private File generateDocGenReport(File copyTemplateFile, DocGenData request, QuoteXWrapper quoteXData) {
        return new File("report");
    }


    private File deleteSelectedSheet(File inputFilePath, String outputFilePath, List<String> sheets) throws Exception {
        ZipSecureFile.setMinInflateRatio(0.001);
        logger.info("Starting to delete sheets from file {}", inputFilePath);
        try (FileInputStream fis = new FileInputStream(inputFilePath);
             Workbook workbook = new XSSFWorkbook(fis);
             FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            for (String sheetName : sheets) {
                int sheetIndex = workbook.getSheetIndex(sheetName);
                if (sheetIndex != -1) {
                    workbook.removeSheetAt(sheetIndex);
                    logger.info("Deleted sheet: {}", sheetName);
                } else {
                    logger.warn("Sheet {} not found", sheetName);
                }
            }
            workbook.write(fos);
            logger.info("Selected sheets deleted and file saved as {}", outputFilePath);
        } catch (IOException e) {
            logger.error("Error deleting sheets: {}", e.getMessage(), e);
            throw new IOException(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
        return new File(outputFilePath);
    }
}
