package com.demo.test.demo;

import com.demo.test.DemoApplication;
import com.demo.test.common.workflow.dto.Task;
import com.demo.test.common.workflow.dto.TaskStatus;
import com.demo.test.common.workflow.service.WorkflowService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {DemoApplication.class})
public class WorkflowServiceTest {
    Logger logger = LoggerFactory.getLogger(WorkflowServiceTest.class);

    @Autowired
    private WorkflowService workflowService;

    @Test
    @Transactional
    public void workflowLifeCycle() throws InterruptedException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("submitFormBy", "3333333");
        variables.put("submitFormStatus", "saved");
        variables.put("submitFormDate", new Date());
        variables.put("startBy", "3333333");
        variables.put("startStatus", "saved");
        variables.put("startDate", new Date());
        String processId = workflowService.startProcess("customerWorkflow", "1234567", variables);
        Assertions.assertNotNull(processId);
        logger.info("Task Saved Process (" + processId + ") Started");
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
        List<Task> tasks = workflowService.getTasksByProcessId(processId);
        Assertions.assertNotNull(tasks);
        Assertions.assertTrue(tasks.size() > 0);
        Task task = workflowService.getTaskById(tasks.get(0).getId());
        Assertions.assertNotNull(task);
        variables = new HashMap<>();
        variables.put(task.getTaskDefinitionKey() + "By", "3333333");
        variables.put(task.getTaskDefinitionKey() + "Status", "submitted");
        variables.put(task.getTaskDefinitionKey() + "Date", new Date());
        workflowService.completeTask(task.getId(), variables);
        logger.info("Task(" + task.getTaskDefinitionKey() + " | " + task.getId() + ") Submitted");
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
        tasks = workflowService.getTasksByProcessId(processId);
        Assertions.assertNotNull(tasks);
        Assertions.assertTrue(tasks.size() > 0);
        task = null;
        for (int i = 0; i < tasks.size(); i++) {
            if (TaskStatus.ACTIVE.equals(tasks.get(i).getTaskStatus())) {
                task = workflowService.getTaskById(tasks.get(i).getId());
                break;
            }
        }
        Assertions.assertNotNull(task);
        Assertions.assertEquals("5555555", task.getAssignee());
        variables = new HashMap<>();
        variables.put(task.getTaskDefinitionKey() + "By", "5555555");
        variables.put(task.getTaskDefinitionKey() + "Status", "approved");
        variables.put(task.getTaskDefinitionKey() + "Date", new Date());
        workflowService.completeTask(task.getId(), variables);
        logger.info("Task(" + task.getTaskDefinitionKey() + " | " + task.getId() + ") Approved");
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
        tasks = workflowService.getTasksByProcessId(processId);
        Assertions.assertNotNull(tasks);
        Assertions.assertTrue(tasks.size() > 0);
        task = null;
        for (int i = 0; i < tasks.size(); i++) {
            if (TaskStatus.ACTIVE.equals(tasks.get(i).getTaskStatus())) {
                task = workflowService.getTaskById(tasks.get(i).getId());
                break;
            }
        }
        Assertions.assertNotNull(task);
        Assertions.assertTrue(task.getCandidateGroups().contains("CUS_FR_GROUP"));
        variables = new HashMap<>();
        variables.put(task.getTaskDefinitionKey() + "By", "4444444");
        variables.put(task.getTaskDefinitionKey() + "Status", "approved");
        variables.put(task.getTaskDefinitionKey() + "Date", new Date());
        workflowService.completeTask(task.getId(), variables);
        logger.info("Task(" + task.getTaskDefinitionKey() + " | " + task.getId() + ") Approved");
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
        tasks = workflowService.getTasksByProcessId(processId);
        Assertions.assertNotNull(tasks);
        Assertions.assertTrue(tasks.size() > 0);
        task = null;
        for (int i = 0; i < tasks.size(); i++) {
            if (TaskStatus.ACTIVE.equals(tasks.get(i).getTaskStatus())) {
                task = workflowService.getTaskById(tasks.get(i).getId());
                break;
            }
        }
        Assertions.assertNull(task);
        logger.info("Process(" + processId + ") Completed No More ACTIVE Tasks");
    }
}
