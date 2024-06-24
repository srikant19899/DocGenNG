package com.docGenSvc.utility;


import com.docGenSvc.exception.InvalidInputException;
import com.docGenSvc.exception.QuoteXException;
import com.docGenSvc.model.request.DocGenData;
import com.fasterxml.jackson.core.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;

import com.docGenSvc.model.entity.DocGenEntity;
import com.docGenSvc.properties.DocGenProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DocGenUtility {
    private static final Logger logger = LoggerFactory.getLogger(DocGenUtility.class);

    private List<DocGenEntity> dbData = new ArrayList<>();
    @Autowired
    private DocGenProperties docGenProperties;
    private ObjectMapper objectMapper = new ObjectMapper();


    public String docNameCreator(String quoteId) {
        LocalDateTime localDateTime = LocalDateTime.now();
        // Format LocalDateTime to a string with a separator
        String formattedDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
        System.out.println(quoteId + "-" + formattedDateTime);
        return quoteId + "-" + formattedDateTime;
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
            throw new QuoteXException("QuoteX Input output Exception");
        } catch (Exception e) {
            throw new QuoteXException("QuoteX called Failed");
        }
        return null;
    }

    private Object quoteXDataMapper(InputStream inputStream) {
        try {
            return objectMapper.readValue( inputStream, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDocumentStatus(String requestId, String ticketNumber) {
        DocGenEntity docGenEntity = new DocGenEntity();
        docGenEntity.setReady(false);
        docGenEntity.setRequestId(requestId);
        docGenEntity.setTicketNumber(ticketNumber);
        dbData.add(docGenEntity);
        logger.info("DB status before completing file{}", dbData);

    }

    public boolean checkDuplicateRequest(String requestId) {
        for (DocGenEntity db : dbData) {
            if (db.getRequestId().equals(requestId)) {
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
            throw new InvalidInputException("docType should be present");
        } else if (quoteId.length() < 2) {
            throw new InvalidInputException("docType minimum length should be 2");
        } else if (quoteId.length() > 50) {
            throw new InvalidInputException("docType maximum length should be 50");
        }
    }

    private void docTypeValidator(String docType) {
        if (StringUtils.isBlank(docType)) {
            throw new InvalidInputException("docType should be present");
        } else if (docType.length() < 2) {
            throw new InvalidInputException("docType minimum length should be 2");
        } else if (docType.length() > 50) {
            throw new InvalidInputException("docType maximum length should be 50");
        }
    }

}
