package com.DocGenNG.utility;

import com.DocGenNG.model.request.DocGenData;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class DocGenUtility {
    OkHttpClient client= new OkHttpClient();
    ObjectMapper mapper= new ObjectMapper();



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

            // Always close the response to avoid resource leaks
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
