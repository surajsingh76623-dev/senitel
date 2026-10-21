package com.sentinel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sentinelOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SENTINEL Problem Investigation & Resolution Platform API")
                        .description("REST API for structured problem investigation, solution trade-off analysis, deterministic verification, and knowledge preservation.")
                        .version("1.0.0")
                        .contact(new Contact().name("Sentinel Engineering").email("sentinel@domain.local"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
