package com.demo.test.customer.dto;

import com.demo.test.common.workflow.dto.TaskStatus;

import java.util.Map;

public class CustomerWorkflowTaskDto {
    private Long entityId;
    private String entityFirstName;
    private String entityLastName;
    private String entityEmail;
    private String taskId;
    private String taskAssignee;
    private String taskTaskDefinitionKey;
    private TaskStatus taskStatus;
    private Map<String, String> taskWorkflowProcessInstanceVariables;

    @Override
    public String toString() {
        return "CustomerWorkflowTaskDto{" +
                "entityId=" + entityId +
                ", entityFirstName='" + entityFirstName + '\'' +
                ", entityLastName='" + entityLastName + '\'' +
                ", entityEmail='" + entityEmail + '\'' +
                ", taskId='" + taskId + '\'' +
                ", taskAssignee='" + taskAssignee + '\'' +
                ", taskTaskDefinitionKey='" + taskTaskDefinitionKey + '\'' +
                ", taskTaskStatus=" + taskStatus +
                ", taskWorkflowProcessInstanceVariables=" + taskWorkflowProcessInstanceVariables +
                '}';
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityFirstName() {
        return entityFirstName;
    }

    public void setEntityFirstName(String entityFirstName) {
        this.entityFirstName = entityFirstName;
    }

    public String getEntityLastName() {
        return entityLastName;
    }

    public void setEntityLastName(String entityLastName) {
        this.entityLastName = entityLastName;
    }

    public String getEntityEmail() {
        return entityEmail;
    }

    public void setEntityEmail(String entityEmail) {
        this.entityEmail = entityEmail;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskAssignee() {
        return taskAssignee;
    }

    public void setTaskAssignee(String taskAssignee) {
        this.taskAssignee = taskAssignee;
    }

    public String getTaskTaskDefinitionKey() {
        return taskTaskDefinitionKey;
    }

    public void setTaskTaskDefinitionKey(String taskTaskDefinitionKey) {
        this.taskTaskDefinitionKey = taskTaskDefinitionKey;
    }

    public TaskStatus getTaskTaskStatus() {
        return taskStatus;
    }

    public void setTaskTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Map<String, String> getTaskWorkflowProcessInstanceVariables() {
        return taskWorkflowProcessInstanceVariables;
    }

    public void setTaskWorkflowProcessInstanceVariables(Map<String, String> taskWorkflowProcessInstanceVariables) {
        this.taskWorkflowProcessInstanceVariables = taskWorkflowProcessInstanceVariables;
    }
}
