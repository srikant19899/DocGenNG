package com.DocGenNG.service;

import com.DocGenNG.model.request.DocGenData;

import java.io.IOException;


public interface DocGenNgService {

    public String processFile(String requestId,String trace, DocGenData request) throws IOException, InterruptedException;
    public boolean isFileReady(String fileId);
    public byte[] getFile(String fileId) throws IOException;

}
