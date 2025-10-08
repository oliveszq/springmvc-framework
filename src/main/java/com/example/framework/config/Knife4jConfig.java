package com.example.framework.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("olives文档")
                        .description("olives")
                        .version("1.0")
                        .contact(new Contact()
                                .name("青子")
                                .email("2783123570@qq.com")));
    }

}
