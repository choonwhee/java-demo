package com.demo.test.common.workflow.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class TaskSearch implements Serializable {

    Integer priorityGt;
    private String id;
    private String processBusinessKey;
    private String businessKey;
    private String name;
    private String description;
    private String assignee;
    private String owner;
    private String candidateGroupsMeo;
    private Set<String> candidateUsers;
    private Date dueDateGe;
    private Date dueDateLe;
    private Date createdDateGe;
    private Date createdDateLe;
    private String statusEq;
    private TaskStatus taskStatus;
    private ProcessInstance processInstance;

    public Integer getPriorityGt() {
        return priorityGt;
    }

    public void setPriorityGt(Integer priorityGt) {
        this.priorityGt = priorityGt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcessBusinessKey() {
        return processBusinessKey;
    }

    public void setProcessBusinessKey(String processBusinessKey) {
        this.processBusinessKey = processBusinessKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCandidateGroupsMeo() {
        return candidateGroupsMeo;
    }

    public void setCandidateGroupsMeo(String candidateGroupsMeo) {
        this.candidateGroupsMeo = candidateGroupsMeo;
    }

    public Set<String> getCandidateUsers() {
        return candidateUsers;
    }

    public void setCandidateUsers(Set<String> candidateUsers) {
        this.candidateUsers = candidateUsers;
    }

    public Date getDueDateGe() {
        return dueDateGe;
    }

    public void setDueDateGe(Date dueDateGe) {
        this.dueDateGe = dueDateGe;
    }

    public Date getDueDateLe() {
        return dueDateLe;
    }

    public void setDueDateLe(Date dueDateLe) {
        this.dueDateLe = dueDateLe;
    }

    public Date getCreatedDateGe() {
        return createdDateGe;
    }

    public void setCreatedDateGe(Date createdDateGe) {
        this.createdDateGe = createdDateGe;
    }

    public Date getCreatedDateLe() {
        return createdDateLe;
    }

    public void setCreatedDateLe(Date createdDateLe) {
        this.createdDateLe = createdDateLe;
    }

    public String getStatusEq() {
        return statusEq;
    }

    public void setStatusEq(String statusEq) {
        this.statusEq = statusEq;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }
}

