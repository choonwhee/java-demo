package com.demo.test.customer.controller;

import com.demo.test.common.exception.NotFoundException;
import com.demo.test.common.security.SimpleUserDetails;
import com.demo.test.common.workflow.dto.EntityTask;
import com.demo.test.common.workflow.dto.TaskStatus;
import com.demo.test.common.workflow.service.WorkflowService;
import com.demo.test.customer.dto.*;
import com.demo.test.customer.repository.CustomerRepository;
import com.demo.test.customer.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;


@RestController
@RequestMapping("/rest")
public class CustomerController {

    Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService service;
    private final WorkflowService wfSvc;

    public CustomerController(CustomerRepository repo, CustomerService service, WorkflowService wfSvc) {
        this.service = service;
        this.wfSvc = wfSvc;
    }

    /*@GetMapping("/customers")
    List<Customer> all() {
        return repo.findAll();
    }*/

    /*@GetMapping("/customers")
    Page<Customer> findPage(
            Pageable pageable,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email
    ) {
        return repo.findCustomers(pageable, id, firstName, lastName, address, country, phone, email);
    }*/

    @GetMapping("/customers")
    Page<Customer> search(Pageable pageable, CustomerSearchDto customerSearch) {
        return service.search(pageable, customerSearch);
    }

    @GetMapping("/customers/usertasks")
    Page<CustomerTaskDto> searchUserCustomerTasks(Authentication authentication, Pageable pageable, CustomerTaskSearchDto searchDto) {
        logger.info("Search Customer User Tasks");
        SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
        return service.searchTaskByUserRights(pageable, userDetails.getUsername(), userDetails.getUser().getRoles(), searchDto);
    }

    @PostMapping("/customers/usertasks/{taskId}/firstlinecheckapproval")
    Customer firstLineCheckApprove(Authentication authentication, @PathVariable String taskId, @RequestBody CusFirstLineCheckDto dto) {
        logger.info("First Line Check Approve");
        SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
        return service.updateDtoTaskCompleted("approved", dto, userDetails.getUsername(), taskId);
    }

    @PostMapping("/customers/usertasks/{taskId}/firstlinecheckrejection")
    Customer firstLineCheckReject(Authentication authentication, @PathVariable String taskId, @RequestBody CusFirstLineCheckDto dto) {
        logger.info("First Line Check Rejected");
        SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
        return service.updateDtoTaskCompleted("rejected", dto, userDetails.getUsername(), taskId);
    }



    @GetMapping("/customers/tasks")
    List<EntityTask<Customer>> searchUserTasks(Authentication authentication, CustomerTaskSearch customerTaskSearch) {
        logger.info("Search User Tasks");
        SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();
        return service.searchTaskByUserRights(userDetails.getUsername(), userDetails.getUser().getRoles(), customerTaskSearch);
    }

    @GetMapping("/customers/tasks/assignee")
    List<EntityTask<Customer>> findAssignedTasks(Principal principal, CustomerTaskSearch customerTaskSearch) {
        String userId = principal.getName();
        logger.info("Search Tasks Assigned  to " + userId);
        customerTaskSearch.setTaskAssigneeEql(userId);
        List<TaskStatus> statusList = new ArrayList<>();
        statusList.add(TaskStatus.ACTIVE);
        customerTaskSearch.setTaskTaskStatusInc(statusList);
        return service.searchTask(customerTaskSearch);
    }

    @GetMapping("/customers/tasks/user")
    List<EntityTask<Customer>> findTasksByLoggedInRights(Authentication authentication, CustomerTaskSearch customerTaskSearch) {
        SimpleUserDetails userDetails = (SimpleUserDetails)authentication.getPrincipal();

        logger.info("Find Task By Logged in Rights " + customerTaskSearch.getTaskIdEql() + " to " + userDetails.getUsername());
        List<TaskStatus> statusList = new ArrayList<>();
        statusList.add(TaskStatus.ACTIVE);
        customerTaskSearch.setTaskTaskStatusInc(statusList);
        return service.searchTaskByUserRights(userDetails.getUsername(), userDetails.getUser().getRoles(), customerTaskSearch);
    }

    @GetMapping("/customers/tasks/candidateuser")
    List<EntityTask<Customer>> findTasksByCandidateUser(Principal principal, CustomerTaskSearch customerTaskSearch) {
        String userId = principal.getName();

        customerTaskSearch.setTaskCandidateUsersMeo(userId);
        List<TaskStatus> statusList = new ArrayList<>();
        statusList.add(TaskStatus.ACTIVE);
        customerTaskSearch.setTaskTaskStatusInc(statusList);
        return service.searchTask(customerTaskSearch);
    }

    @GetMapping("/customers/tasks/roles")
    List<EntityTask<Customer>> findTasksByRole(Authentication authentication, CustomerTaskSearch customerTaskSearch) {
        SimpleUserDetails userDetails = (SimpleUserDetails)authentication.getPrincipal();


        customerTaskSearch.setTaskCandidateGroupsMeo(userDetails.getUser().getRoles());
        List<TaskStatus> statusList = new ArrayList<>();
        statusList.add(TaskStatus.ACTIVE);
        customerTaskSearch.setTaskTaskStatusInc(statusList);
        return service.searchTask(customerTaskSearch);
    }


    @PostMapping("/customers")
    Customer newCustomer(Principal principal, @RequestBody CusNewDto cusNewDto) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("submitFormStatus", "submitted");
        variables.put("submitFormBy", principal.getName());
        variables.put("submitFormDate", new Date());

        return service.saveDtoAndStartProcess(cusNewDto, principal.getName(), variables);
    }

    @GetMapping("/customers/{id}")
    Customer findById(@PathVariable Long id) {
        return service.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @GetMapping("/customers/{id}/usertasks")
    Customer simpleSearch(Authentication authentication, @PathVariable Long id) {
        SimpleUserDetails userDetails = (SimpleUserDetails)authentication.getPrincipal();
        return service.simpleSearchTaskByUserRightsAndId(userDetails.getUsername(), userDetails.getUser().getRoles(), id).getEntity();
    }

    @PutMapping("/customers/{id}")
    Customer replaceCustomers(@RequestBody Customer newCustomer, @PathVariable Long id) {
        newCustomer.setId(id);
        return service.save(newCustomer);
    }

    @DeleteMapping("customers/{id}")
    void deleteCustomer(@PathVariable Long id) {
        service.deleteById(id);
    }


    @GetMapping("customers/tasks/{taskId}")
    EntityTask<Customer> getCustomerWorkflowTask(Authentication authentication, @PathVariable String taskId) {
        logger.info("Get Customer Workflow Task");
        SimpleUserDetails userDetails = (SimpleUserDetails) authentication.getPrincipal();

        List<EntityTask<Customer>> cusWorkflowTasks = service.simpleSearchTaskByUserRightsByTaskId(userDetails.getUsername(), userDetails.getUser().getRoles(), taskId);
        if (cusWorkflowTasks == null || cusWorkflowTasks.size() == 0) {
            throw new NotFoundException("Customer Workflow Task not found");
        }
        return cusWorkflowTasks.get(0);
    }

    @PostMapping("customers/tasks/{taskId}/approve")
    void workflowTaskApprove(Principal principal, @PathVariable String taskId, @RequestBody Map<String, Object> params) {
        logger.info("Customer Workflow Task - approved");
        service.taskApproved(principal.getName(), taskId);
    }

    @PostMapping("customers/tasks/{taskId}/reject")
    void workflowTaskReject(Principal principal, @PathVariable String taskId, @RequestBody Map<String, Object> params) {
        logger.info("Customer Workflow Task - rejected");
        service.taskRejected(principal.getName(), taskId);
    }

}
