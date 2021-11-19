package com.demo.test.common.workflow.camunda;

import com.demo.test.common.workflow.dto.BaseTask;
import com.demo.test.common.workflow.dto.ProcessInstance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.IdentityLinkType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.camunda.bpm.engine.delegate.TaskListener.*;

@Component
public class WorkflowTaskListener {
    private final WebClient callbackWebClient;
    private final TaskService taskService;
    Logger logger = LoggerFactory.getLogger(WorkflowTaskListener.class);

    @Autowired
    public WorkflowTaskListener(WebClient callbackWebClient, TaskService taskService) {
        this.callbackWebClient = callbackWebClient;
        this.taskService = taskService;
    }

    @EventListener
    public void onTaskEvent(DelegateTask delegateTask) {
        switch (delegateTask.getEventName()) {
            case EVENTNAME_CREATE:
                create(delegateTask);
                break;
            case EVENTNAME_UPDATE:
                update(delegateTask);
                break;
            case EVENTNAME_COMPLETE:
                complete(delegateTask);
                break;
            case EVENTNAME_DELETE:
                delete(delegateTask);
                break;
            case EVENTNAME_TIMEOUT:
                timeout(delegateTask);
                break;
            case EVENTNAME_ASSIGNMENT:
                assignment(delegateTask);
                break;
        }
    }

    private BaseTask mapDelegateTaskToWorkflowTask(DelegateTask task) {
        BaseTask bwfTask = new BaseTask();
        bwfTask.setTaskDefinitionKey(task.getTaskDefinitionKey());
        bwfTask.setProcessBusinessKey(task.getExecution().getProcessBusinessKey());
        bwfTask.setBusinessKey(task.getExecution().getBusinessKey());
        bwfTask.setId(task.getId());
        bwfTask.setDescription(task.getDescription());
        bwfTask.setAssignee(task.getAssignee());
        bwfTask.setOwner(task.getOwner());
        Set<String> candidateGroups = new HashSet<String>();
        Set<String> candidateUsers = new HashSet<String>();
        Set<IdentityLink> candidates = task.getCandidates();
        for (IdentityLink candidate : candidates) {
            switch (candidate.getType()) {
                case IdentityLinkType.CANDIDATE:
                    candidateGroups.add(candidate.getGroupId());
                    break;
                case IdentityLinkType.ASSIGNEE:
                    candidateUsers.add(candidate.getUserId());
                    break;
            }
        }
        bwfTask.setCandidateGroups(candidateGroups);
        bwfTask.setCandidateUsers(candidateUsers);
        bwfTask.setDueDate(task.getDueDate());
        bwfTask.setName(task.getName());
        bwfTask.setCreatedDate(task.getCreateTime());
        bwfTask.setPriority(task.getPriority());
        Map<String, String> variables = new HashMap<>();
        for (Map.Entry<String, Object> variable : taskService.getVariables(task.getId()).entrySet()) {
            variables.put(variable.getKey(), variable.getValue().toString());
        }
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setId(task.getProcessInstanceId());
        ProcessDefinition processDefinition = task.getProcessEngineServices().getRepositoryService().getProcessDefinition(task.getProcessDefinitionId());
        processInstance.setName(processDefinition.getName());
        processInstance.setDescription(processDefinition.getDescription());
        processInstance.setVariables(variables);
        bwfTask.setProcessInstance(processInstance);

        return bwfTask;
    }

    private void create(DelegateTask task) {
        logger.info("Third party event - CREATE");
        BaseTask bwfTask = mapDelegateTaskToWorkflowTask(task);

        String response = callbackWebClient
                .post()
                .uri("http://localhost:8080/rest/workflow-callback/task/create")
                .header("Content-Type", "application/json")
                .body(BodyInserters.fromValue(getJson(bwfTask)))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        logger.info("Assignment Event: " + response);
    }

    private String getJson(BaseTask bwfTask) {
        String bodyJson;
        try {
            bodyJson = new ObjectMapper().writeValueAsString(bwfTask);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return bodyJson;
    }

    private void update(DelegateTask task) {
        logger.info("Third party event - UPDATE");
        BaseTask bwfTask = mapDelegateTaskToWorkflowTask(task);

        String response = callbackWebClient
                .post()
                .uri("http://localhost:8080/rest/workflow-callback/task/update")
                .header("Content-Type", "application/json")
                .body(BodyInserters.fromValue(getJson(bwfTask)))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        logger.info("Update Event: " + response);
    }

    private void complete(DelegateTask task) {
        logger.info("Third party event - COMPLETE");
        BaseTask bwfTask = mapDelegateTaskToWorkflowTask(task);

        String response = callbackWebClient
                .post()
                .uri("http://localhost:8080/rest/workflow-callback/task/complete")
                .header("Content-Type", "application/json")
                .body(BodyInserters.fromValue(getJson(bwfTask)))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        logger.info("Complete Event: " + response);
    }

    private void delete(DelegateTask task) {

        logger.info("Third party event - DELETE");
        BaseTask bwfTask = mapDelegateTaskToWorkflowTask(task);
        String response = callbackWebClient
                .post()
                .uri("http://localhost:8080/rest/workflow-callback/task/delete")
                .header("Content-Type", "application/json")
                .body(BodyInserters.fromValue(getJson(bwfTask)))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        logger.info(response);
        logger.info("Delete Event: " + response);
    }

    private void timeout(DelegateTask task) {
        logger.info("Third party event - TIMEOUT");
        BaseTask bwfTask = mapDelegateTaskToWorkflowTask(task);
        String response = callbackWebClient
                .post()
                .uri("http://localhost:8080/rest/workflow-callback/task/timeout")
                .header("Content-Type", "application/json")
                .body(BodyInserters.fromValue(getJson(bwfTask)))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        logger.info("Timeout Event: " + response);
    }

    private void assignment(DelegateTask task) {
        logger.info("Third party event - ASSIGNMENT");
        BaseTask bwfTask = mapDelegateTaskToWorkflowTask(task);
        String response = callbackWebClient
                .post()
                .uri("http://localhost:8080/rest/workflow-callback/task/assignment")
                .header("Content-Type", "application/json")
                .body(BodyInserters.fromValue(getJson(bwfTask)))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        logger.info("Assignment Event: " + response);
    }
}
