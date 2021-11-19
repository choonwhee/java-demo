package com.demo.test.common.workflow.service;

import com.demo.test.common.user.dto.User;
import com.demo.test.common.user.service.UserService;
import com.demo.test.common.workflow.dto.ProcessInstance;
import com.demo.test.common.workflow.dto.Task;
import com.demo.test.common.workflow.dto.TaskSearch;
import com.demo.test.common.workflow.repository.WorkflowTaskRepository;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final WebClient workflowWebClient;
    private final WorkflowTaskRepository repo;
    private final ObjectMapper jsonMapper;
    private final UserService userSvc;
    Logger logger = LoggerFactory.getLogger(WorkflowServiceImpl.class);

    @Autowired
    public WorkflowServiceImpl(WebClient workflowWebClient, WorkflowTaskRepository repo, ObjectMapper jsonMapper, UserService userSvc) {
        this.workflowWebClient = workflowWebClient;
        this.repo = repo;
        this.jsonMapper = jsonMapper;
        this.userSvc = userSvc;
    }

    private ObjectNode getVariablesJson(Map<String, Object> variables) {
        logger.info("get VarJson: " + variables.toString());
        ObjectNode variablesNode = jsonMapper.createObjectNode();

        for (Map.Entry<String, Object> variable : variables.entrySet()) {

            Object value = variable.getValue();
            //pass in Base64 encoded String if setting File as variable
            if (value instanceof String || value instanceof Character) {
                ObjectNode valueNode = variablesNode.putObject(variable.getKey());
                valueNode.put("value", value.toString());
                valueNode.put("type", "String");
            } else if (value instanceof Boolean) {
                ObjectNode valueNode = variablesNode.putObject(variable.getKey());
                valueNode.put("value", (Boolean) value);
                valueNode.put("type", "Boolean");
            } else if (value instanceof Integer) {
                ObjectNode valueNode = variablesNode.putObject(variable.getKey());
                valueNode.put("value", (Integer) value);
                valueNode.put("type", "Integer");
            } else if (value instanceof Long) {
                ObjectNode valueNode = variablesNode.putObject(variable.getKey());
                valueNode.put("value", (Long) value);
                valueNode.put("type", "Long");
            } else if (value instanceof Double) {
                ObjectNode valueNode = variablesNode.putObject(variable.getKey());
                valueNode.put("value", (Double) value);
                valueNode.put("type", "Double");
            } else if (value instanceof Short) {
                ObjectNode valueNode = variablesNode.putObject(variable.getKey());
                valueNode.put("value", (Short) value);
                valueNode.put("type", "Short");
            } else if (value instanceof Byte) {
                ObjectNode valueNode = variablesNode.putObject(variable.getKey());
                valueNode.put("value", (Byte) value);
                valueNode.put("type", "Byte");
            } else if (value instanceof Date) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                ObjectNode valueNode = variablesNode.putObject(variable.getKey());
                valueNode.put("value", dateFormat.format((Date) value));
                valueNode.put("type", "Date");
            } else if (value != null) {
                //if is Object
                ObjectNode valueNode = new ObjectMapper().valueToTree(value);
                variablesNode.set("value", valueNode);
                valueNode.put("type", "Object");
            }
        }
        return variablesNode;
    }

    @Override
    public String startProcess(String processName, String businessKey, Map<String, Object> variables) {
        ObjectNode bodyNode = jsonMapper.createObjectNode();
        if (businessKey != null) bodyNode.put("businessKey", businessKey);
        if (variables != null) bodyNode.set("variables", getVariablesJson(variables));

        try {
            logger.info("Start WF BODY" + jsonMapper.writeValueAsString(bodyNode));
            String response = workflowWebClient
                    .post()
                    .uri("http://localhost:8080/engine-rest/process-definition/key/{processName}/start", processName)
                    .header("Content-Type", "application/json")
                    .body(BodyInserters.fromValue(jsonMapper.writeValueAsString(bodyNode)))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Start Process Response" + response);

            final ObjectNode node = new ObjectMapper().readValue(response, ObjectNode.class);
            return node.get("id").asText();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Task> getTasksByProcessId(String processId) {
        TaskSearch searchParams = new TaskSearch();
        ProcessInstance searchPIParams = new ProcessInstance();
        searchPIParams.setId(processId);
        searchParams.setProcessInstance(searchPIParams);
        return repo.search(searchParams);
    }

    @Override
    public Task getTaskById(String id) {
        return repo.getById(id);
    }

    @Override
    public List<String> getTasks(Integer pageNum, Integer pageSize, Map<String, String> searchParams) {
        StringBuilder uriStr = new StringBuilder("http://localhost:8080/engine-rest/task?");
        if (pageNum != null) {
            uriStr.append("firstResult=").append(pageNum);
        }
        if (pageSize != null) {
            uriStr.append("maxResults=").append(pageSize);
        }
        logger.info("Uri: " + uriStr);

        try {
            String bodyJson = jsonMapper.writeValueAsString(searchParams);

            logger.info("body Json: " + bodyJson);
            String response = workflowWebClient
                    .post()
                    .uri(uriStr.toString())
                    .header("Content-Type", "application/json")
                    .body(BodyInserters.fromValue(bodyJson))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Get Tasks Response: " + response);

            List<String> taskIds = new ArrayList<>();

            JsonNode taskNodes = jsonMapper.readTree(response);
            if (taskNodes.isArray()) {
                for (final JsonNode taskNode : taskNodes) {
                    taskIds.add(taskNode.get("id").asText());
                }
            }

            return taskIds;
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getProcessVariables(String processInstanceId) {
        String response = workflowWebClient
                .get()
                .uri("http://localhost:8080/engine-rest/process-instance/{processInstanceId}/variables", processInstanceId)
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        logger.info("Get Process Variables Response: " + response);
        return response;
    }

    @Override
    public String claimTask(String taskId, String assignee) {
        try {
            ObjectNode bodyNode = jsonMapper.createObjectNode();
            bodyNode.put("userId", assignee);
            String response = workflowWebClient
                    .post()
                    .uri("http://localhost:8080/engine-rest/task/{taskId}/claim", taskId)
                    .header("Content-Type", "application/json")
                    .body(BodyInserters.fromValue(jsonMapper.writeValueAsString(bodyNode)))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            logger.info("Claim Task Response: " + response);
            return response;
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String unclaimTask(String taskId) {
        String response = workflowWebClient
                .post()
                .uri("http://localhost:8080/engine-rest/task/{taskId}/unclaim", taskId)
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        logger.info("Unclaim Task Response: " + response);
        return response;
    }

    @Override
    public String assignTask(String taskId, String assignee) {
        try {
            ObjectNode bodyNode = jsonMapper.createObjectNode();
            bodyNode.put("userId", assignee);
            String response = workflowWebClient
                    .post()
                    .uri("http://localhost:8080/engine-rest/task/{taskId}/assignee", taskId)
                    .header("Content-Type", "application/json")
                    .body(BodyInserters.fromValue(jsonMapper.writeValueAsString(bodyNode)))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            logger.info("Assign Task Response: " + response);
            return response;
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String assignTaskToLineManager(String taskId, String userId) {
        Optional<User> user = userSvc.findById(userId);
        if (!user.isPresent()) throw new RuntimeException("User(" + userId + ") has no Line Manager!");
        logger.info("Assign Task to Line Manager of User(" + user + "): " + user.get().getLineManager());
        return assignTask(taskId, user.get().getLineManager());
    }

    @Override
    public String getTaskVariables(String taskId) {
        String response = workflowWebClient
                .get()
                .uri("http://localhost:8080/engine-rest/task/{taskId}/variables", taskId)
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        logger.info("Get Task Variables Response: " + response);
        return response;
    }



    @Override
    public String completeTask(String taskId, Map<String, Object> variables) {
        ObjectNode bodyNode = jsonMapper.createObjectNode();
        if (variables != null) bodyNode.set("variables", getVariablesJson(variables));
        try {
            logger.info(jsonMapper.writeValueAsString(bodyNode));
            String response = workflowWebClient
                    .post()
                    .uri("http://localhost:8080/engine-rest/task/{taskId}/complete", taskId)
                    .header("Content-Type", "application/json")
                    .body(BodyInserters.fromValue(jsonMapper.writeValueAsString(bodyNode)))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            logger.info("Complete Task Response: " + response);
            return response;
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }
}
