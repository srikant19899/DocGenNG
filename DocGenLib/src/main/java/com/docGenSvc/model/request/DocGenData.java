package com.docGenSvc.model.request;

import com.docGenSvc.model.validator.ValidDocType;
import com.docGenSvc.model.validator.ValidQuoteId;
import lombok.Data;
import lombok.ToString;


@Data
@ToString // validate
public class DocGenData {

    private String clientId;
    @ValidQuoteId
    private String quoteId;
    @ValidDocType
    private String docType;
    private String template;
}