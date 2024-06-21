package com.docGenSvc.model.request;


import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class DocGenData {

    private String clientId;
    private String quoteId;
    private String docType;
    private String template;
}