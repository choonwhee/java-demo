package com.demo.test.common.workflow.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@MappedSuperclass
public class BaseTask implements Serializable {

    private @Id
    String id;
    private String taskDefinitionKey;
    private String processBusinessKey;
    private String businessKey;
    private String name;
    private String description;
    private String assignee;
    private String owner;
    @ElementCollection
    @Column(name = "groupName")
    private Set<String> candidateGroups;
    @ElementCollection
    @Column(name = "userId")
    private Set<String> candidateUsers;
    private Date dueDate;
    private Integer priority;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(columnDefinition = "processInstanceId")
    private ProcessInstance processInstance;
    private Date createdDate;

    @Override
    public String toString() {
        return "BaseTask{" +
                "id='" + id + '\'' +
                ", taskDefinitionKey='" + taskDefinitionKey + '\'' +
                ", processBusinessKey='" + processBusinessKey + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", assignee='" + assignee + '\'' +
                ", owner='" + owner + '\'' +
                ", candidateGroups=" + candidateGroups +
                ", candidateUsers=" + candidateUsers +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", processInstance=" + processInstance +
                ", createdDate=" + createdDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof BaseTask)) return false;

        BaseTask baseTask = (BaseTask) o;

        return new EqualsBuilder().append(id, baseTask.id).append(taskDefinitionKey, baseTask.taskDefinitionKey).append(processBusinessKey, baseTask.processBusinessKey).append(businessKey, baseTask.businessKey).append(name, baseTask.name).append(description, baseTask.description).append(assignee, baseTask.assignee).append(owner, baseTask.owner).append(candidateGroups, baseTask.candidateGroups).append(candidateUsers, baseTask.candidateUsers).append(dueDate, baseTask.dueDate).append(priority, baseTask.priority).append(processInstance, baseTask.processInstance).append(createdDate, baseTask.createdDate).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(taskDefinitionKey).append(processBusinessKey).append(businessKey).append(name).append(description).append(assignee).append(owner).append(candidateGroups).append(candidateUsers).append(dueDate).append(priority).append(processInstance).append(createdDate).toHashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
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

    public Set<String> getCandidateGroups() {
        return candidateGroups;
    }

    public void setCandidateGroups(Set<String> candidateGroups) {
        this.candidateGroups = candidateGroups;
    }

    public Set<String> getCandidateUsers() {
        return candidateUsers;
    }

    public void setCandidateUsers(Set<String> candidateUsers) {
        this.candidateUsers = candidateUsers;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}

