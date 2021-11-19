package com.demo.test.common.workflow.camunda.delegates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.camunda.bpm.engine.delegate.*;
import org.camunda.bpm.engine.impl.cfg.TransactionListener;
import org.camunda.bpm.engine.impl.cfg.TransactionState;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.inject.Named;

@Named("assignmentTaskListener")
public class AssignmentTaskListener implements TaskListener {
    Logger logger = LoggerFactory.getLogger(AssignmentTaskListener.class);

    private Expression assignTo;
    private Expression startBy;
    private final WebClient callbackWebClient;

    public AssignmentTaskListener(WebClient callbackWebClient) {
        this.callbackWebClient = callbackWebClient;
    }


    public void setAssignTo(Expression assignTo) {
        this.assignTo = assignTo;
    }
    public void setStartBy(Expression startBy) {
        this.startBy = startBy;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        logger.info("AssignmentTaskDelegate Notify | delegateTask: " + delegateTask.getName() + " | assignTo: " + assignTo.getExpressionText());
        String assignToValue = assignTo.getValue(delegateTask).toString();
        String startByValue = startBy.getValue(delegateTask).toString();
        Context.getCommandContext().getTransactionContext().addTransactionListener(TransactionState.COMMITTED, new TransactionListener() {
            @Override
            public void execute(CommandContext commandContext) {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode body = objectMapper.createObjectNode();
                body.put("assignTo", assignToValue);
                body.put("startBy", startByValue);
                try {
                    String response = callbackWebClient
                            .post()
                            .uri("http://localhost:8080/rest/workflow/task/" + delegateTask.getId() + "/assign")
                            .header("Content-Type", "application/json")
                            .body(BodyInserters.fromValue(objectMapper.writeValueAsString(body)))
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
