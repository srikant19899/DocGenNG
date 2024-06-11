package com.DocGenNG.service;

import com.DocGenNG.model.request.DocumentsRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface DocGenNgService {

    public String processFile(MultipartFile file, DocumentsRequest request) throws IOException;
    public boolean isFileReady(String fileId);
    public byte[] getFile(String fileId) throws IOException;

}
