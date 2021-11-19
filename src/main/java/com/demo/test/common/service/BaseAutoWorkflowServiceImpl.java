package com.demo.test.common.service;

import com.demo.test.common.jpa.CustomRepository;
import com.demo.test.common.workflow.dto.EntityTask;
import com.demo.test.common.workflow.dto.Task;
import com.demo.test.common.workflow.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import java.lang.reflect.InvocationTargetException;

import java.util.*;

public class BaseAutoWorkflowServiceImpl<T, IDT extends Serializable, R extends CustomRepository<T, IDT>> extends BaseAutoServiceImpl<T, IDT, R> implements BaseAutoWorkflowService<T, IDT, R> {

    Logger logger = LoggerFactory.getLogger(BaseAutoWorkflowServiceImpl.class);

    private WorkflowService workflowService;

    public BaseAutoWorkflowServiceImpl(Class entityClass, R repo, WorkflowService workflowService) {
        super(entityClass, repo);
        this.workflowService = workflowService;
    }

    public T saveAndStartProcess(T newEntity, String userId) {
        return saveAndStartProcess(newEntity, null, userId, null, null);
    }

    public T saveAndStartProcess(T newEntity, String userId, Map<String, Object> additionalProcessVariables) {
        return saveAndStartProcess(newEntity, null, userId, null, additionalProcessVariables);
    }

    public T saveAndStartProcess(T newEntity, String workflowProcessName, String userId, String createdStatus, Map<String, Object> additionalProcessVariables) {
        T savedEntity = getRepo().save(newEntity);
        startProcess(newEntity, workflowProcessName, userId, createdStatus, additionalProcessVariables);

        return savedEntity;
    }

    public T saveDtoAndStartProcess(Object dto, String userId) {
        return saveDtoAndStartProcess(dto, null, userId, null, null);
    }

    public T saveDtoAndStartProcess(Object dto, String userId, Map<String, Object> additionalProcessVariables) {
        return saveDtoAndStartProcess(dto, null, userId, null, additionalProcessVariables);
    }

    public T saveDtoAndStartProcess(Object dto, String workflowProcessName, String userId, String createdStatus, Map<String, Object> additionalProcessVariables) {
        T savedEntity = this.saveDto(dto);
        startProcess(savedEntity, workflowProcessName, userId, createdStatus, additionalProcessVariables);

        return savedEntity;
    }

    private void startProcess(T newEntity, String workflowProcessName, String userId, String createdStatus, Map<String, Object> additionalProcessVariables) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("startBy", userId);
        variables.put("startStatus", (createdStatus == null ? "Submitted" : createdStatus));
        variables.put("startDate", new Date());
        if (additionalProcessVariables != null) {
            variables.putAll(additionalProcessVariables);
        }

        Class entityClass = newEntity.getClass();

        if (workflowProcessName == null || "".equals(workflowProcessName)) {
            String entityClassSimpleName = entityClass.getSimpleName();
            workflowProcessName = Character.toLowerCase(entityClassSimpleName.charAt(0)) + entityClassSimpleName.substring(1) + "Workflow";
        }
        logger.info("Workflow Process Name: " + workflowProcessName);

        cacheGetIdMethodByIntrospection(entityClass);

        try {
            String processInstanceId = workflowService.startProcess(workflowProcessName, getEntityGetIdMethod().invoke(newEntity).toString(), variables);
            logger.info("Process Instance Started ID: " + processInstanceId);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public T updateTaskCompleted(String status, T updateEntity, String userId, String taskId) {
        return updateTaskCompleted(status, updateEntity, userId, taskId, null);
    }

    public T updateTaskCompleted(String status, T updateEntity, String userId, String taskId, Map<String, Object> additionalProcessVariables) {
        logger.info("Update and Complete Workflow Task");

        T savedEntity = update(updateEntity);
        taskCompleted(userId, taskId, status, additionalProcessVariables);
        return savedEntity;
    }

    public T updateDtoTaskCompleted(String status, Object dto, String userId, String taskId) {
        return updateDtoTaskCompleted(status, dto, userId, taskId, null);
    }

    public T updateDtoTaskCompleted(String status, Object dto, String userId, String taskId, Map<String, Object> additionalProcessVariables) {
        logger.info("Update DTO and Complete Workflow Task");

        T savedEntity = updateDto(dto);
        taskCompleted(status, userId, taskId, additionalProcessVariables);
        return savedEntity;
    }

    public void taskCompleted(String status, String userId, String taskId) {
        taskCompleted(status, userId, taskId, null);
    }


    public void taskCompleted(String status, String userId, String taskId, Map<String, Object> additionalProcessVariables) {
        logger.info("Complete Workflow Task: " + taskId);
        Task task = workflowService.getTaskById(taskId);
        Map<String, Object> completeVariables = new HashMap<>();
        completeVariables.put(task.getTaskDefinitionKey() + "CompletedBy", userId);
        completeVariables.put(task.getTaskDefinitionKey() + "Status", status);
        completeVariables.put(task.getTaskDefinitionKey() + "CompletedDate", new Date());
        if (additionalProcessVariables != null) completeVariables.putAll(additionalProcessVariables);
        workflowService.completeTask(taskId, completeVariables);
    }

    public void claimTask(String userId, String taskId) {
        logger.info("Claim Task: " + taskId);
        workflowService.claimTask(taskId, userId);
    }

    public void unclaimTask(String userId, String taskId) {
        logger.info("Claim Task: " + taskId);
        workflowService.claimTask(taskId, userId);
    }

    public EntityTask<T> claimTaskAndLoad(String userId, List<String> userRoles , String taskId) {
        logger.info("Claim Task: " + taskId);
        workflowService.claimTask(taskId, userId);
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("workflowTaskId", taskId);
        List<EntityTask<T>> results = getRepo().simpleSearchTaskByUserRights(userId, userRoles, searchMap);
        if (results == null || results.size() > 1) return null;
        return results.get(0);
    }

}
