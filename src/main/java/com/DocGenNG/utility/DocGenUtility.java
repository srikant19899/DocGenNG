package com.DocGenNG.utility;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DocGenUtility {
    public String docNameCreator(String quoteId){
        LocalDateTime localDateTime = LocalDateTime.now();
        // Format LocalDateTime to a string with a separator
        String formattedDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
        System.out.println(quoteId + "-" + formattedDateTime);
        return  quoteId + "-" + formattedDateTime;
    }
}
