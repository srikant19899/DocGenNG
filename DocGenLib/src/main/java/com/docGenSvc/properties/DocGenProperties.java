package com.docGenSvc.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class DocGenProperties {
    @Value("${docgen.backend.quotex.url}")
    private String url;
    @Value("${docgen.backend.quotex.uri}")
    private String uri;
    @Value("${docgen.backend.quotex.timeout}")
    private int timeout;
    @Value("${docgen.backend.generation.wait}")
    private int docGenLag;
    @Value("${docgen.template}")
    private String templateFile;

}
