package com.demo.test.common.workflow.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Task extends BaseTask implements Serializable {

    @ManyToOne
    @JoinColumn(columnDefinition = "status")
    private Status status;
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;
    private Date completedDate;
    private Date assignmentDate;
    private Date deletedDate;
    private Date updatedDate;
    private Date timeoutDate;

    @Override
    public String toString() {
        return "Task{" +
                "status=" + status +
                ", taskStatus=" + taskStatus +
                ", completedDate=" + completedDate +
                ", assignmentDate=" + assignmentDate +
                ", deletedDate=" + deletedDate +
                ", updatedDate=" + updatedDate +
                ", timeoutDate=" + timeoutDate +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(status, task.status).append(taskStatus, task.taskStatus).append(completedDate, task.completedDate).append(assignmentDate, task.assignmentDate).append(deletedDate, task.deletedDate).append(updatedDate, task.updatedDate).append(timeoutDate, task.timeoutDate).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(status).append(taskStatus).append(completedDate).append(assignmentDate).append(deletedDate).append(updatedDate).append(timeoutDate).toHashCode();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public Date getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(Date assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Date getTimeoutDate() {
        return timeoutDate;
    }

    public void setTimeoutDate(Date timeoutDate) {
        this.timeoutDate = timeoutDate;
    }
}

