package com.docGenSvc.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;


@Configuration
@EnableAsync(proxyTargetClass = true)
public class DocGenConfig {

    @Bean
    OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("DocGenNG")
                        .description("Specification of DocGenNG service that generates documents from quote data")
                        .license(new License()));
    }
}
