package com.DocGenNG.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class DocumentsRequest {

    @NotEmpty(message = "user name is required")
    private String quoteId;


    @NotEmpty(message = "user name is required")
    private String docType;


    @NotEmpty(message = "user name is required")
    private String template;
}