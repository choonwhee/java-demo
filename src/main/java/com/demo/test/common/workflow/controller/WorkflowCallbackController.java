package com.demo.test.common.workflow.controller;

import com.demo.test.common.workflow.dto.Status;
import com.demo.test.common.workflow.repository.WorkflowTaskRepository;
import com.demo.test.common.workflow.dto.TaskStatus;
import com.demo.test.common.workflow.dto.BaseTask;
import com.demo.test.common.workflow.dto.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/rest/workflow-callback")
public class WorkflowCallbackController {

    Logger logger = LoggerFactory.getLogger(WorkflowCallbackController.class);

    private final WorkflowTaskRepository repo;

    public WorkflowCallbackController(WorkflowTaskRepository repo) {
        this.repo = repo;
    }

    @Value("${application.workflow.task.default_task_status}")
    private String defaultTaskStatus;

    public String getDefaultTaskStatus() {
        return defaultTaskStatus;
    }

    public void setDefaultTaskStatus(String defaultTaskStatus) {
        this.defaultTaskStatus = defaultTaskStatus;
    }

    @PostMapping("/task/create")
    Task workflowTaskCreate(@RequestBody BaseTask baseTask) {
        logger.info("Workflow Callback - Create:" + baseTask);
        Task task = new Task();
        BeanUtils.copyProperties(baseTask, task);
        task.setTaskStatus(TaskStatus.ACTIVE);
        task.setStatus(getStatus(baseTask));
        return repo.save(task);
    }

    @PostMapping("/task/update")
    Task workflowTaskUpdate(@RequestBody BaseTask baseTask) {
        logger.info("Workflow Callback - Update");
        Task task = repo.getById(baseTask.getId());
        BeanUtils.copyProperties(baseTask, task, "id");
        task.setUpdatedDate(new Date());
        if (task.getAssignee() == null && task.getAssignmentDate() != null) {
            logger.info("Task Unclaimed");
            task.setAssignmentDate(null);
        }
        task.setStatus(getStatus(baseTask));
        return repo.save(task);
    }

    private Status getStatus(BaseTask baseTask) {
        String taskStatusId = baseTask.getProcessInstance().getVariables().get(baseTask.getTaskDefinitionKey() + "Status");
        Status status = new Status();
        if (taskStatusId == null) taskStatusId = defaultTaskStatus;
        status.setId(taskStatusId);
        return status;
    }

    @PostMapping("/task/complete")
    Task workflowTaskComplete(@RequestBody BaseTask baseTask) {
        logger.info("Workflow Callback - Complete");
        Task task = repo.getById(baseTask.getId());
        BeanUtils.copyProperties(baseTask, task, "id");
        task.setCompletedDate(new Date());
        task.setTaskStatus(TaskStatus.COMPLETED);
        task.setStatus(getStatus(baseTask));
        return repo.save(task);
    }

    @PostMapping("/task/delete")
    Task workflowTaskDelete(@RequestBody BaseTask baseTask) {
        logger.info("Workflow Callback - Delete");
        Task task = repo.getById(baseTask.getId());
        BeanUtils.copyProperties(baseTask, task, "id");
        task.setDeletedDate(new Date());
        task.setTaskStatus(TaskStatus.DELETED);
        task.setStatus(getStatus(baseTask));
        return repo.save(task);
    }

    @PostMapping("/task/assignment")
    Task workflowTaskAssignment(@RequestBody BaseTask baseTask) {
        logger.info("Workflow Callback - Assignment");
        Task task = repo.getById(baseTask.getId());
        BeanUtils.copyProperties(baseTask, task, "id");
        task.setAssignmentDate(new Date());
        task.setStatus(getStatus(baseTask));
        return repo.save(task);
    }

    @PostMapping("/task/timeout")
    Task workflowTaskTimeout(@RequestBody BaseTask baseTask) {
        logger.info("Workflow Callback - Timeout");
        Task task = repo.getById(baseTask.getId());
        BeanUtils.copyProperties(baseTask, task, "id");
        task.setTimeoutDate(new Date());
        task.setTaskStatus(TaskStatus.TIMEOUT);
        task.setStatus(getStatus(baseTask));
        return repo.save(task);
    }
}
