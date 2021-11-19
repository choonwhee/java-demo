package com.demo.test;

import com.demo.test.common.jpa.CustomRepositoryImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableJpaRepositories(basePackages = "com.demo.test", repositoryBaseClass = CustomRepositoryImpl.class)
public class DemoApplication {

    @Bean
    public WebClient workflowWebClient(){
        return WebClient.builder()
                .filter(ExchangeFilterFunctions.basicAuthentication("demo","demo"))
                .build();
    }

    @Bean
    public WebClient callbackWebClient(){

        return WebClient.builder()
                .filter(ExchangeFilterFunctions.basicAuthentication("SYSTEM","password"))
                .build();
    }

    @Bean
    public ObjectMapper jsonMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


}
