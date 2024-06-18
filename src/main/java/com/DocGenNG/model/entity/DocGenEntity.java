package com.DocGenNG.model.entity;

import lombok.Data;

@Data
public class DocGenEntity {


    private String requestId;
    private String filePath;
    private String ticketNumber;
    private boolean isReady; // if true file is ready, false we are generating file


}
