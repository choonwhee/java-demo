package com.demo.test.common.workflow.camunda.delegates;

import org.camunda.bpm.engine.delegate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.inject.Named;

@Named("simpleRestDelegate")
public class SimpleRestDelegate implements JavaDelegate {
    Logger logger = LoggerFactory.getLogger(SimpleRestDelegate.class);

    private String httpMethod;
    private String uri;
    private String body;
    private String authType;
    private String token;
    private String username;
    private String password;

    public void setHttpMethod(Expression httpMethod) {
        this.httpMethod = httpMethod.getExpressionText();
    }

    public void setUri(Expression uri) {
        this.uri = uri.getExpressionText();
    }

    public void setBody(Expression body) {
        this.body = body.getExpressionText();
    }

    public void setAuthType(Expression authType) {
        this.authType = authType.getExpressionText();
    }

    public void setToken(Expression token) {
        this.token = token.getExpressionText();
    }

    public void setUsername(Expression username) {
        this.username = username.getExpressionText();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("RestDelegate");

        WebClient.Builder builder = WebClient.builder();

        if (AuthType.BASIC.name().equals(authType)) {
            builder.defaultHeaders(header -> {
                header.setBasicAuth(username, password);
            });
        } else if (AuthType.BEARER.name().equals(authType)) {
            builder.defaultHeaders(header -> {
                header.setBearerAuth(token);
            });
        }

        WebClient webClient = builder.build();

        String response;
        if (HttpMethod.POST.name().equals(httpMethod.toUpperCase())) {
            response = webClient
                    .post()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } else if (HttpMethod.GET.name().equals(httpMethod.toUpperCase())) {
            response = webClient
                    .get()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } else if (HttpMethod.PUT.name().equals(httpMethod.toUpperCase())) {
            response = webClient
                    .put()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } else if (HttpMethod.PATCH.name().equals(httpMethod.toUpperCase())) {
            response = webClient
                    .patch()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } else if (HttpMethod.DELETE.name().equals(httpMethod.toUpperCase())) {
            response = webClient
                    .delete()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }
    }

    private enum AuthType {
        NONE,
        BASIC,
        BEARER
    }
}
