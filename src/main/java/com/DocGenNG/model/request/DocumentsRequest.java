package com.DocGenNG.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DocumentsRequest {

    private String quoteId;
    private String docType;
    private String template;


}
