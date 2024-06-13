package com.DocGenNG.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsRequest {
    @JsonProperty("quoteId")
    private String quoteId;

    @JsonProperty("docType")
    private String docType;

    @JsonProperty("template")
    private String template;
}