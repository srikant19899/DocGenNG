package com.docGenSvc.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "DocGenStatus")
@Data
public class DocGenNgStatus {

    @Id
    private String requestId;
    private String filePath;
    private String ticketNumber;
    private boolean isReady;


}