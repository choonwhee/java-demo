package com.demo.test.common.workflow.service;

import com.demo.test.common.workflow.dto.Task;

import java.util.List;
import java.util.Map;

public interface WorkflowService {

    String startProcess(String processName, String businessKey, Map<String, Object> variables);

    List<Task> getTasksByProcessId(String processId);

    Task getTaskById(String id);

    List<String> getTasks(Integer pageNum, Integer pageSize, Map<String, String> searchParams);

    String getProcessVariables(String processInstanceId);

    String claimTask(String taskId, String assignee);

    String unclaimTask(String taskId);

    String assignTask(String taskId, String assignee);

    String assignTaskToLineManager(String taskId, String userId);

    String getTaskVariables(String taskId);

    String completeTask(String taskId, Map<String, Object> variables);
}
