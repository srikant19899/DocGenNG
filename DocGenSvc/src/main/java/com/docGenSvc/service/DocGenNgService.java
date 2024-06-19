package com.docGenSvc.service;


import com.docGenSvc.model.request.DocGenData;

import java.io.IOException;


public interface DocGenNgService {

    public String processFile(String requestId,String trace, DocGenData request) throws IOException, InterruptedException;
    public boolean isFileReady(String fileId);
    public byte[] getFile(String fileId) throws IOException;

}
