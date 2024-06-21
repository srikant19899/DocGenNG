package com.docGenSvc.serviceImpl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.docGenSvc.csv.CsvGenerator;
import com.docGenSvc.csv.ExcelParser;
import com.docGenSvc.csv.FileService;
import com.docGenSvc.exception.DocumentProcessingException;
import com.docGenSvc.model.request.DocGenData;
import com.docGenSvc.service.DocGenNgService;
import com.docGenSvc.utility.DocGenUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class DocGenNgServiceImpl implements DocGenNgService {
    private static final Logger logger = LoggerFactory.getLogger(DocGenNgServiceImpl.class);

    private static final String FILE_DIRECTORY = "processed_files/";
    private static final String CSV_DIRECTORY = "csv-files";
    private static final String COMBINED_CSV_FILE = "combined_sheets.csv";

    @Autowired
    private DocGenUtility docGenUtility;
    @Autowired
    private FileService fileService;
    @Autowired
    private ExcelParser excelParser;
    @Autowired
    private CsvGenerator csvGenerator;


    public String processFile(String requestId, String trace, DocGenData request) {

        docGenUtility.validateRequest(request);


        String ticketNumber = docGenUtility.docNameCreator(request.getQuoteId());
        // create getQuatXRequest(DocGenNgRequest)->  return type quatXRequest o
        Object object = switch (request.getClientId()) {
            case "PROS" -> docGenUtility.getQuoteXData(request);
            default -> docGenUtility.getQuoteXData(request);
        };


        logger.info("QuoteX service call{}", object.toString());
        if (docGenUtility.checkDuplicateRequest(requestId)) {
            throw new DocumentProcessingException("your Document is processing !!");
        }
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

//            try {
//                Thread.sleep(60000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            convertXlsxToCsv("CPEA_TEMPLATE_v3.0.xlsx","output.csv");
            try {
//                List<File> csvFiles=copyTemplateExcelToCsv("CPEA_TEMPLATE_v3.0.xlsx");
                convertExcelToSingleCsv();
            } catch (IOException e) {
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

    public List<File> copyTemplateExcelToCsv(String templateFileXLSX) throws IOException {
        List<File> csvFiles = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(templateFileXLSX);

        try (InputStream is = resource.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                File csvFile = convertSheetToCsv(sheet);
                csvFiles.add(csvFile);
            }
        }

        return csvFiles;
    }
    private File convertSheetToCsv(Sheet sheet) throws IOException {
        File csvFile = File.createTempFile(sheet.getSheetName(), ".csv");
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
            for (Row row : sheet) {
                List<String> cells = new ArrayList<>();
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case STRING:
                            cells.add(cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            cells.add(String.valueOf(cell.getNumericCellValue()));
                            break;
                        case BOOLEAN:
                            cells.add(String.valueOf(cell.getBooleanCellValue()));
                            break;
                        case FORMULA:
                            cells.add(cell.getCellFormula());
                            break;
                        default:
                            cells.add("");
                    }
                }
                writer.println(String.join(",", cells));
            }
        }
        return csvFile;
    }

    public String convertExcelToSingleCsv() throws IOException {
        ClassPathResource resource = new ClassPathResource("CPEA_TEMPLATE_v3.0.xlsx");
        Path csvDirectoryPath = Paths.get(CSV_DIRECTORY);
        if (!Files.exists(csvDirectoryPath)) {
            Files.createDirectories(csvDirectoryPath);
        }
        Path csvFilePath = csvDirectoryPath.resolve(COMBINED_CSV_FILE);

        try (InputStream is = resource.getInputStream();
             Workbook workbook = new XSSFWorkbook(is);
             PrintWriter writer = new PrintWriter(Files.newBufferedWriter(csvFilePath))) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                writer.println("Sheet: " + sheet.getSheetName());
                for (Row row : sheet) {
                    List<String> cells = new ArrayList<>();
                    for (Cell cell : row) {
                        switch (cell.getCellType()) {
                            case STRING:
                                cells.add(cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                cells.add(String.valueOf(cell.getNumericCellValue()));
                                break;
                            case BOOLEAN:
                                cells.add(String.valueOf(cell.getBooleanCellValue()));
                                break;
                            case FORMULA:
                                cells.add(cell.getCellFormula());
                                break;
                            default:
                                cells.add("");
                        }
                    }
                    writer.println(String.join(",", cells));
                }
                writer.println();
            }
        }

        return csvFilePath.toString();
    }

}
