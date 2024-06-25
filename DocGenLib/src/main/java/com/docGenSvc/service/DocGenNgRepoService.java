package com.docGenSvc.service;

import com.docGenSvc.model.entity.DocGenNgStatus;
import com.docGenSvc.repository.DocGenNgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class DocGenNgRepoService {

    @Autowired
    private DocGenNgRepository docGenNgRepository;
    @Autowired
    private ResourceLoader resourceLoader;

    public DocGenNgStatus saveFileStatus(DocGenNgStatus docGenStatus){
       return docGenNgRepository.save(docGenStatus);
    }
    public DocGenNgStatus retriveFileStatus(String requestId) {
        return docGenNgRepository.getReferenceById(requestId);
    }
    public void moveFiletoPath(String fileName, String serverPath) throws IOException {
        // move this method to main service utility
        // take file parameter and take service path={moveFileToServerPath}
        Resource resource = resourceLoader.getResource("classpath:" + fileName);
        if (resource.exists()) {
            InputStream inputStream = resource.getInputStream();

            String targetFilePath = serverPath + File.separator + fileName;
            try (OutputStream outputStream = new FileOutputStream(targetFilePath)) {
                byte[] buffer = new byte[1024];
                //
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();//remove this
            }
        } else {
            throw new FileNotFoundException("File not found" + fileName);
            // logger proper
        }
    }

}
