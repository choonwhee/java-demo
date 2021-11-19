package com.demo.test.common.workflow.controller;

import com.demo.test.common.workflow.dto.TaskAssignTo;
import com.demo.test.common.workflow.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rest/workflow/")
public class WorkflowController {

    Logger logger = LoggerFactory.getLogger(WorkflowController.class);

    private final WorkflowService service;

    public WorkflowController(WorkflowService service) {
        this.service = service;
    }

    @PostMapping("/task/{taskId}/assign")
    void workflowTaskAssignment(@PathVariable String taskId, @RequestBody Map<String, Object> params) {
        logger.info("Workflow Task - Assign");
        if (TaskAssignTo.LINE_MANAGER.name().equals(params.get("assignTo"))) {
            logger.info("Assign " + taskId + " to LINE_MANAGER");
            service.assignTaskToLineManager(taskId, params.get("startBy").toString());
        } else {
            throw new RuntimeException("Could not match assignTo parameter to any predefined assign actions in WorkflowTaskAssignTo");
        }
    }


}
