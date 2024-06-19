package com.docGenSvc.model.response;

import lombok.Data;

@Data
public class TemplateMetadata {
    private String id;
    private String name;
    private String docType;
    private String created;
}
