package com.demo.test.customer.dto;

public class CustomerTaskDto {
    private Long entityId;
    private String entityFirstName;
    private String entityLastName;
    private String entityAddress;
    private String entityCountry;
    private String entityPhone;
    private String entityEmail;
    private String taskId;
    private String taskTaskDefinitionKey;
    private String taskStatusId;
    private String taskStatusValue;
    private String taskTaskStatus;
    private String taskCreatedDate;

    @Override
    public String toString() {
        return "CustomerTaskDto{" +
                "entityId=" + entityId +
                ", entityFirstName='" + entityFirstName + '\'' +
                ", entityLastName='" + entityLastName + '\'' +
                ", entityAddress='" + entityAddress + '\'' +
                ", entityCountry='" + entityCountry + '\'' +
                ", entityPhone='" + entityPhone + '\'' +
                ", entityEmail='" + entityEmail + '\'' +
                ", taskId='" + taskId + '\'' +
                ", taskTaskDefinitionKey='" + taskTaskDefinitionKey + '\'' +
                ", taskStatusId='" + taskStatusId + '\'' +
                ", taskStatusValue='" + taskStatusValue + '\'' +
                ", taskTaskStatus='" + taskTaskStatus + '\'' +
                ", taskCreatedDate='" + taskCreatedDate + '\'' +
                '}';
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public String getEntityAddress() {
        return entityAddress;
    }

    public void setEntityAddress(String entityAddress) {
        this.entityAddress = entityAddress;
    }

    public String getEntityCountry() {
        return entityCountry;
    }

    public void setEntityCountry(String entityCountry) {
        this.entityCountry = entityCountry;
    }

    public String getEntityPhone() {
        return entityPhone;
    }

    public void setEntityPhone(String entityPhone) {
        this.entityPhone = entityPhone;
    }

    public String getEntityEmail() {
        return entityEmail;
    }

    public void setEntityEmail(String entityEmail) {
        this.entityEmail = entityEmail;
    }

    public String getTaskTaskDefinitionKey() {
        return taskTaskDefinitionKey;
    }

    public void setTaskTaskDefinitionKey(String taskTaskDefinitionKey) {
        this.taskTaskDefinitionKey = taskTaskDefinitionKey;
    }

    public String getTaskStatusId() {
        return taskStatusId;
    }

    public void setTaskStatusId(String taskStatusId) {
        this.taskStatusId = taskStatusId;
    }

    public String getTaskStatusValue() {
        return taskStatusValue;
    }

    public void setTaskStatusValue(String taskStatusValue) {
        this.taskStatusValue = taskStatusValue;
    }

    public String getTaskTaskStatus() {
        return taskTaskStatus;
    }

    public void setTaskTaskStatus(String taskTaskStatus) {
        this.taskTaskStatus = taskTaskStatus;
    }

    public String getTaskCreatedDate() {
        return taskCreatedDate;
    }

    public void setTaskCreatedDate(String taskCreatedDate) {
        this.taskCreatedDate = taskCreatedDate;
    }
}
