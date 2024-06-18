package com.DocGenNG.model.entity;

import lombok.Data;

@Data
public class DocGenEntity {

    private int id;
    private String requestId;
    private String ticketNumber;
    private boolean isReady;


}
