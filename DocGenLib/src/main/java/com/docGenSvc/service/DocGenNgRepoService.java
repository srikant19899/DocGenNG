package com.docGenSvc.service;

import com.docGenSvc.model.entity.DocGenNgStatus;
import com.docGenSvc.repository.DocGenNgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocGenNgRepoService {

    @Autowired
    private DocGenNgRepository docGenNgRepository;

    public DocGenNgStatus saveFileStatus(DocGenNgStatus docGenStatus){
       return docGenNgRepository.save(docGenStatus);
    }
}
