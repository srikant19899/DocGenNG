package com.DocGenNG.controller;

import com.DocGenNG.model.request.DocumentsRequest;
import com.DocGenNG.service.DocGenNgService;
import com.DocGenNG.serviceImpl.DocGenNgServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class DocGenController {

    private final DocGenNgService docGenNgService;

    @Autowired
    public DocGenController(DocGenNgService docGenNgService) {
        this.docGenNgService = docGenNgService;
    }

       /*
        step 1: change this service to asych
        step 2: once the request comes in - the service should return the ticket , another thread to process the request
        Step 3: return the ticket from service class even if there is any exception
        step 4: in service class write a method to call QuoteX service ( for now hardcode it as - it should return a template details )
        step 5: in resource folder add the master template and use that template and raw data excel file to create a new template ( sales / other )
        step 6: in service write a method that will hold the business logic to map the raw data to excel file using the mastet template
        NOTE: Business logic --> Create a copy of master template, copy thr values from raw excel to the newly created excel file - refer poc code
        Step 7: once the raw data is mapped in the newly created excel then --> you will make a call to a new method that will call the db and store the ticket number and is_ready flag as true
        Step 8: the created excel sheet to be moved to server path --. write a nethod for this and once we move it then we need to store the path in redis/db for the retrive service to use the path
        Step 9: Exception handling to be solid across the application.
         */

    @PostMapping("/documents")
    public ResponseEntity<String> submit(@RequestParam("file") MultipartFile file, DocumentsRequest request) {
        try {
            String fileId = docGenNgService.processFile(file, request);
            return ResponseEntity.ok("File submitted successfully. File ID: " + fileId);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/documents/jobs/")
    public ResponseEntity<Boolean> isReady(@RequestParam("jobId") String jobId) {
        boolean isReady= docGenNgService.isFileReady(jobId);
        return ResponseEntity.ok(isReady);
    }


    @GetMapping("/templates/")
    public ResponseEntity<byte[]> retrieve(@RequestParam("Id") String Id) {
        try {
            byte[] fileData = docGenNgService.getFile(Id);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + Id + ".xlsx")
                    .body(fileData);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);

        }

    }
}
