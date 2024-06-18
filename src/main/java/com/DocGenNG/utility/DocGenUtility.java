package com.DocGenNG.utility;

import com.DocGenNG.exception.DocumentProcessingException;
import com.DocGenNG.model.entity.DocGenEntity;
import com.DocGenNG.model.request.DocGenData;
import com.DocGenNG.serviceImpl.DocGenNgServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DocGenUtility {
    OkHttpClient client= new OkHttpClient();
    ObjectMapper mapper= new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(DocGenUtility.class);

    private List<DocGenEntity> dbData = new ArrayList<>();


    public String docNameCreator(String quoteId){
        LocalDateTime localDateTime = LocalDateTime.now();
        // Format LocalDateTime to a string with a separator
        String formattedDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
        System.out.println(quoteId + "-" + formattedDateTime);
        return  quoteId + "-" + formattedDateTime;
    }

    public Object callQuoteService() throws IOException {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request request = new Request.Builder()
                    .url("http://localhost:8083/student/2")
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String,String > student = mapper.readValue(response.body().string(), Map.class);
                return student;

            }
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addDocumentStatus(String requestId, String ticketNumber){
        DocGenEntity docGenEntity = new DocGenEntity();
        docGenEntity.setReady(false);
        docGenEntity.setRequestId(requestId);
        docGenEntity.setTicketNumber(ticketNumber);
        dbData.add(docGenEntity);
        logger.info("DB status before completing file{}", dbData);

    }
    public boolean checkDuplicateRequest(String requestId){
        for(DocGenEntity db: dbData){
            if(db.getRequestId().equals(requestId)) {
                return true;
            }
        }
        return false;
    }

    public  void updateDocumentStatus(String requestId){
        dbData.stream().filter(x-> x.getRequestId().equals(requestId)).forEach(x -> x.setReady(true));
        logger.info("DB status after completing file{}", dbData);
    }
}
