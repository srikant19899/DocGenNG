package com.DocGenNG.model.request;

import com.DocGenNG.model.validator.ValidDocType;
import com.DocGenNG.model.validator.ValidQuoteId;
import lombok.Data;


@Data
public class DocGenData {

    private String clientId;
    @ValidQuoteId
    private String quoteId;
    @ValidDocType
    private String docType;
    private String template;
}