package com.docGenSvc.utility;


import com.docGenSvc.exception.DocGenNGException;
import com.docGenSvc.exception.InvalidInputException;
import com.docGenSvc.exception.QuoteXException;
import com.docGenSvc.model.quoteXWrapper.QuoteXWrapper;
import com.docGenSvc.model.request.DocGenData;
import com.fasterxml.jackson.core.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;

import java.io.*;

import com.docGenSvc.model.entity.DocGenNgStatus;
import com.docGenSvc.properties.DocGenProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class DocGenUtility {
    private static final Logger logger = LoggerFactory.getLogger(DocGenUtility.class);

    private List<DocGenNgStatus> dbData = new ArrayList<>();
    @Autowired
    private DocGenProperties docGenProperties;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String sheetNameToDelete = "PIVOT";


    public String ticketGenerator(String quoteId) {
        LocalDateTime localDateTime = LocalDateTime.now();
        // Format LocalDateTime to a string with a separator
        String formattedDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
        System.out.println(quoteId + "-" + formattedDateTime);
        return quoteId + "-" + formattedDateTime;
    }

    public String docNameGenerator(String requestId){
        LocalDateTime localDateTime = LocalDateTime.now();
        // Format LocalDateTime to a string with a separator
        String formattedDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
        return requestId + "-" + formattedDateTime+".xlsx";
    }

    public Object getQuoteXData(DocGenData docGenData) {
        String serviceUrl = docGenProperties.getUrl();
        int timeout = docGenProperties.getTimeout();
//        add audit and info log here
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(Timeout.ofMilliseconds(timeout))
                    .setResponseTimeout(Timeout.ofMilliseconds(timeout))
                    .build();
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build();
            CloseableHttpResponse response = httpClient.execute(new HttpGet(serviceUrl));

            if (response.getCode() == 200) {
                return quoteXDataMapper(response.getEntity().getContent());
            }
        } catch (IOException e) {
            throw new QuoteXException(e,e.getMessage(),"400.00.1000");
        } catch (Exception e) {
            throw new QuoteXException(e,e.getMessage(),"500.00.1000");
        }
        return null;
    }

    public Object quoteXDataMapper(InputStream inputStream) {
        try {
            return objectMapper.readValue( inputStream, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDocumentStatus(String requestId, String ticketNumber){
        DocGenNgStatus docGenNgStatus = new DocGenNgStatus();
        docGenNgStatus.setReady(false);
        docGenNgStatus.setRequestId(requestId);
        docGenNgStatus.setTicketNumber(ticketNumber);
        dbData.add(docGenNgStatus);
        logger.info("DB status before completing file{}", dbData);

    }
    public boolean checkDuplicateRequest(String requestId){
        for(DocGenNgStatus db: dbData){
            if(db.getRequestId().equals(requestId)) {
                return true;
            }
        }
        return false;
    }

    public void updateDocumentStatus(String requestId) {
        dbData.stream().filter(x -> x.getRequestId().equals(requestId)).forEach(x -> x.setReady(true));
        logger.info("DB status after completing file{}", dbData);
    }

    public void validateRequest(DocGenData docGenData) {
        quoteIdValidator(docGenData.getQuoteId());
        docTypeValidator(docGenData.getDocType());
    }

    private void quoteIdValidator(String quoteId) {
        if (StringUtils.isBlank(quoteId)) {
            throw new InvalidInputException("quoteId should be present", "400.00.1000");
        } else if (quoteId.length() < 2) {
            throw new InvalidInputException("quoteId minimum length should be 2", "400.00.1000");
        } else if (quoteId.length() > 50) {
            throw new InvalidInputException("quoteId maximum length should be 50", "400.00.1000");
        }
    }

    private void docTypeValidator(String docType) {
        if (StringUtils.isBlank(docType)) {
            throw new InvalidInputException("docType should be present", "400.00.1000");
        } else if (docType.length() < 2) {
            throw new InvalidInputException("docType minimum length should be 2", "400.00.1000");
        } else if (docType.length() > 50) {
            throw new InvalidInputException("docType maximum length should be 50", "400.00.1000");
        }
    }

    public File copyTemplate(String templateFile )  { // utility
        ZipSecureFile.setMinInflateRatio(0.001);
         String copyTemplate = "CPEA_TEMPLATE_v3copy.xlsx";
        logger.info("Starting to copy file from {} to {}", templateFile, copyTemplate);
        ClassPathResource classPathResource = new ClassPathResource(templateFile);
        try (InputStream fis = classPathResource.getInputStream();
             Workbook workbook = new XSSFWorkbook(fis);
             FileOutputStream fos = new FileOutputStream(copyTemplate)) {
            workbook.write(fos);
            logger.info("File copied successfully from {} to {}", templateFile, copyTemplate);
        } catch (IOException e) {
            logger.error("Error copying file: {}", e.getMessage(), e);// need to add error code as well
            throw new DocGenNGException(e,e.getMessage(),"400.00.1000");
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new DocGenNGException(e,e.getMessage(),"500.00.1000");
        }
        return new File(copyTemplate);// fos should be return
    }
    public File generateDocGenReport(File copyTemplateFile, DocGenData request, QuoteXWrapper quoteXData) {
        return new File("report");//
    }
    public List<String> sheetsToBeDelete(){
        return Arrays.asList(sheetNameToDelete);
    }

    public File deleteSelectedSheet(File dataFile,  List<String> sheets) { // utility
        String generatedData = "CPEA_TEMPLATE_v3copy_delete.xlsx";
        ZipSecureFile.setMinInflateRatio(0.001);
        logger.info("Starting to delete sheets from file {}", dataFile);
        try (FileInputStream fis = new FileInputStream(dataFile);
             Workbook workbook = new XSSFWorkbook(fis);
             FileOutputStream fos = new FileOutputStream(generatedData)) {
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
            logger.info("Selected sheets deleted and file saved as {}", generatedData);
        } catch (IOException e) {
            logger.error("Error deleting sheets: {}", e.getMessage(), e);
            throw new DocGenNGException(e,e.getMessage(),"400.00.00"); // return a response exp back-> throw custom, exp -> constance
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new DocGenNGException(e,e.getMessage(),"500.00.1000");
        }
        return new File(generatedData);
    }

}
