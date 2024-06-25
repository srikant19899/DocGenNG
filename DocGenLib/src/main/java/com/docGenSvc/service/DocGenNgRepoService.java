package com.docGenSvc.service;

import com.docGenSvc.exception.DocGenNGException;
import com.docGenSvc.model.entity.DocGenNgStatus;
import com.docGenSvc.repository.DocGenNgRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DocGenNgRepoService {
    private static final Logger logger = LoggerFactory.getLogger(DocGenNgRepoService.class);




    public String moveFileToServerPath(File filePath, String path) {
        Path targetPath;
        try {
            File file = new File("src/main/resources/CPEA_TEMPLATE_v3.0.xlsx");
            FileInputStream inputStream = new FileInputStream(file);
            logger.info("File has taken from resource :{} ", file);
            targetPath = Paths.get("D:\\Project\\files\\CPEA_TEMPLATE_v3.0.xlsx");
            Files.createDirectories(targetPath);
            Path targetFilePath = targetPath.resolve(targetPath);
            int length = 0;
            try (OutputStream outputStream = new FileOutputStream(targetFilePath.toFile())) {
                while ((length = inputStream.read()) > 0) {
                    outputStream.write(length);
                }
            } finally {
                inputStream.close();
            }
            logger.info("File moved to:{} ", targetFilePath);
        }catch (IOException e) {
            throw new DocGenNGException(e,e.getMessage(),"400.00.1000");
        }catch (Exception e) {
            throw new DocGenNGException(e,e.getMessage(),"500.00.1000");
        }
        return String.valueOf(targetPath);
    }
}
