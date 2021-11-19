package com.demo.test.common.service;

import com.demo.test.common.jpa.CustomRepository;
import java.util.Map;
import java.io.Serializable;

public interface BaseAutoWorkflowService<T, IDT extends Serializable, R extends CustomRepository<T, IDT>> extends BaseAutoService<T, IDT, R>{

    public T saveAndStartProcess(T newEntity, String userId);

    public T saveAndStartProcess(T newEntity, String userId, Map<String, Object> additionalProcessVariables);

    public T saveAndStartProcess(T newEntity, String workflowProcessName, String userId, String createdStatus, Map<String, Object> additionalProcessVariables);

    public T saveDtoAndStartProcess(Object dto, String userId);

    public T saveDtoAndStartProcess(Object dto, String userId, Map<String, Object> additionalProcessVariables);

    public T saveDtoAndStartProcess(Object dto, String workflowProcessName, String userId, String createdStatus, Map<String, Object> additionalProcessVariables);

    public T updateTaskCompleted(String status, T updateEntity, String userId, String taskId, Map<String, Object> additionalProcessVariables);

    public T updateTaskCompleted(String status, T updateEntity, String userId, String taskId);

    public T updateDtoTaskCompleted(String status, Object dto, String userId, String taskId, Map<String, Object> additionalProcessVariables);

    public T updateDtoTaskCompleted(String status, Object dto, String userId, String taskId);


    public void taskCompleted(String status, String userId, String taskId);

    public void taskCompleted(String status, String userId, String taskId, Map<String, Object> additionalProcessVariables);
}
