package com.DocGenNG.controller;

import com.DocGenNG.model.request.DocumentsRequest;
import com.DocGenNG.serviceImpl.DocGenNgServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class DocGenController {

    @Autowired
    private DocGenNgServiceImpl excelService;


    @PostMapping("/documents")
    public ResponseEntity<String> submit(@RequestParam("file") MultipartFile file, DocumentsRequest request) {
        try {
            String fileId = excelService.processFile(file,request);
            return ResponseEntity.ok("File submitted successfully. File ID: "                             + fileId);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        }
    }
    @GetMapping("/documents/jobs/")
    public ResponseEntity<Boolean> isReady(@RequestParam("jobId") String jobId) {
        boolean isReady= excelService.isFileReady(jobId);
        return ResponseEntity.ok(isReady);
    }


    @GetMapping("/templates/")
    public ResponseEntity<byte[]> retrieve(@RequestParam("Id") String Id) {
        try {
            byte[] fileData = excelService.getFile(Id);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + Id + ".xlsx")
                    .body(fileData);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);

        }

    }
}
