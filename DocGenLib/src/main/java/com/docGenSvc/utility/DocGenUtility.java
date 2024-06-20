package com.docGenSvc.utility;


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
    private  ObjectMapper objectMapper = new ObjectMapper();


    public String docNameCreator(String quoteId){
        LocalDateTime localDateTime = LocalDateTime.now();
        // Format LocalDateTime to a string with a separator
        String formattedDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
        System.out.println(quoteId + "-" + formattedDateTime);
        return  quoteId + "-" + formattedDateTime;
    }

    public Object getQuoteXData(/*Object request*/)  {
        String serviceUrl = docGenProperties.getUrl();
        int timeout = docGenProperties.getTimeout();
//        add audit band info log here
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(timeout))
                .setResponseTimeout(Timeout.ofMilliseconds(timeout))
                .build();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build()) {
            HttpGet request = new HttpGet(serviceUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getCode() == 200) {
                    String responseBody = new String(response.getEntity().getContent().readAllBytes());
                    return objectMapper.readValue(responseBody, Map.class);
                } else {
                    //             add error and audit logs

                    return  null;
                }
                 /*

                response entity of object insted of string
                two catch
                one have mutiple catchs and
                one and excp
                //  use proper exception httpclient exc, see all possible ecp for http client
                return  null;

                */
            } catch (IOException e) { //  use proper exception httpclient exc, see all possible ecp for http client
                //             add error and audit logs

                return  null;
            }
        } catch (IOException e) {
//             add error and audit logs
            return  null;
        }
//               add audit band info log here
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
