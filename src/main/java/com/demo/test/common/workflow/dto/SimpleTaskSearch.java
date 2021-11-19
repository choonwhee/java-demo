package com.demo.test.common.workflow.dto;

import java.util.List;

public class SimpleTaskSearch {
    private String taskIdEql;
    private String taskNameEql;
    private String taskAssigneeEql;
    private String taskOwnerEql;
    private List<String> taskCandidateGroupsMeo;
    private String taskCandidateUsersMeo;
    private List<String> taskCandidateUsersInc;
    private List<TaskStatus> taskStatusInc;

    @Override
    public String toString() {
        return "SimpleTaskSearch{" +
                "taskIdEql='" + taskIdEql + '\'' +
                ", taskNameEql='" + taskNameEql + '\'' +
                ", taskAssigneeEql='" + taskAssigneeEql + '\'' +
                ", taskOwnerEql='" + taskOwnerEql + '\'' +
                ", taskCandidateGroupsMeo=" + taskCandidateGroupsMeo +
                ", taskCandidateUsersMeo='" + taskCandidateUsersMeo + '\'' +
                ", taskCandidateUsersInc=" + taskCandidateUsersInc +
                ", taskTaskStatusInc=" + taskStatusInc +
                '}';
    }

    public String getTaskIdEql() {
        return taskIdEql;
    }

    public void setTaskIdEql(String taskIdEql) {
        this.taskIdEql = taskIdEql;
    }

    public String getTaskNameEql() {
        return taskNameEql;
    }

    public void setTaskNameEql(String taskNameEql) {
        this.taskNameEql = taskNameEql;
    }

    public String getTaskAssigneeEql() {
        return taskAssigneeEql;
    }

    public void setTaskAssigneeEql(String taskAssigneeEql) {
        this.taskAssigneeEql = taskAssigneeEql;
    }

    public String getTaskOwnerEql() {
        return taskOwnerEql;
    }

    public void setTaskOwnerEql(String taskOwnerEql) {
        this.taskOwnerEql = taskOwnerEql;
    }

    public List<String> getTaskCandidateGroupsMeo() {
        return taskCandidateGroupsMeo;
    }

    public void setTaskCandidateGroupsMeo(List<String> taskCandidateGroupsMeo) {
        this.taskCandidateGroupsMeo = taskCandidateGroupsMeo;
    }

    public String getTaskCandidateUsersMeo() {
        return taskCandidateUsersMeo;
    }

    public void setTaskCandidateUsersMeo(String taskCandidateUsersMeo) {
        this.taskCandidateUsersMeo = taskCandidateUsersMeo;
    }

    public List<String> getTaskCandidateUsersInc() {
        return taskCandidateUsersInc;
    }

    public void setTaskCandidateUsersInc(List<String> taskCandidateUsersInc) {
        this.taskCandidateUsersInc = taskCandidateUsersInc;
    }

    public List<TaskStatus> getTaskTaskStatusInc() {
        return taskStatusInc;
    }

    public void setTaskTaskStatusInc(List<TaskStatus> taskStatusInc) {
        this.taskStatusInc = taskStatusInc;
    }
}
