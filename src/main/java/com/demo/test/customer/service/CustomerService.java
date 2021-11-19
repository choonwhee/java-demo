package com.demo.test.customer.service;

import com.demo.test.common.service.AutoService;
import com.demo.test.common.service.BaseAutoWorkflowService;
import com.demo.test.common.workflow.dto.EntityTask;
import com.demo.test.common.workflow.dto.TaskStatus;
import com.demo.test.customer.dto.*;
import com.demo.test.customer.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AutoService(entityClass = Customer.class, entityRepoClass = CustomerRepository.class)
public interface CustomerService extends BaseAutoWorkflowService<Customer, Long, CustomerRepository> {

    Optional<Customer> findById(Long id);

    Customer save(Customer customer);

    CusNewDto saveAndGetDto(Customer customer);

    CusNewDto saveDto(CusNewDto customer);

    Customer saveDtoAndStartProcess(CusNewDto dto, String userId, Map<String, Object> variables);

    Customer saveDtoAndTaskApproved(CusNewDto dto, String userId, Map<String, Object> variables);

    Customer saveDtoAndTaskRejected(CusNewDto dto, String userId, Map<String, Object> variables);

    void deleteById(Long id);

    List<Customer> simpleSearch(Map<String, Object> searchMap);

    Page<Customer> simpleSearch(Pageable pageable, Map<String, Object> searchMap);

    List<Customer> search(Object searchDto);

    Page<Customer> search(Pageable pageable, CustomerSearchDto searchDto);

    List<EntityTask<Customer>> simpleSearchTask(Map<String, Object> searchMap);

    Page<EntityTask<Customer>> simpleSearchTask(Pageable pageable, Map<String, Object> searchMap);

    List<EntityTask<Customer>> simpleSearchTaskByAssignee(String taskAssignee);

    Page<EntityTask<Customer>> simpleSearchTaskByAssignee(Pageable pageable, String taskAssignee);

    List<EntityTask<Customer>> simpleSearchTaskByTaskStatus(List<TaskStatus> taskStatusInc);

    Page<EntityTask<Customer>> simpleSearchTaskByTaskStatus(Pageable pageable, List<TaskStatus> taskStatusInc);

    List<EntityTask<Customer>> simpleSearchTaskByNameAndTaskStatus(String firstNameEwi, String lastNameSwi, String emailCoi, List<TaskStatus> taskStatusInc);

    Page<EntityTask<Customer>> simpleSearchTaskByNameAndTaskStatus(Pageable pageable, String firstNameEwi, String lastNameSwi, String emailCoi, List<TaskStatus> taskStatusInc);

    List<CustomerWorkflowTaskDto> simpleSearchTaskDtoByNameAndTaskStatus(String firstNameEwi, String lastNameSwi, String emailCoi, List<TaskStatus> taskStatusInc);

    Page<CustomerWorkflowTaskDto> simpleSearchTaskDtoByNameAndTaskStatus(Pageable pageable, String firstNameEwi, String lastNameSwi, String emailCoi, List<TaskStatus> taskStatusInc);

    EntityTask<Customer> simpleSearchTaskByUserRightsAndId(String userId, Collection<String> roles, Long idEql);

    List<Customer> simpleSearchByIdAndName(List<Long> idBtw, String firstNameStw, String lastNameCon);

    Page<Customer> simpleSearchByIdAndName(Pageable pageable, List<Long> idBtw, String firstNameStw, String lastNameCon);


    List<CusNewDto> simpleSearchDtoByIdAndName(List<Long> idBtw, String firstNameStw, String lastNameCon);

    List<EntityTask<Customer>> searchTask(Object searchDto);

    EntityTask<Customer> simpleSearchTaskByCusId(Long id);

    EntityTask<Customer> simpleSearchTaskByTaskId(String taskId);


    List<EntityTask<Customer>> simpleSearchTaskByUserRightsAndTaskStatus(String userId, Collection<String> roles, List<TaskStatus> taskTaskStatusInc);

    CustomerWorkflowTaskDto simpleSearchTaskDtoByCusId(Long id);

    List<EntityTask<Customer>> searchTaskByUserRights(String userId, Collection<String> roles, Object searchDto);

    Page<CustomerTaskDto> searchTaskByUserRights(Pageable pageable, String userId, Collection<String> roles, Object searchDto);

    List<EntityTask<Customer>> simpleSearchTaskByUserRightsByTaskId(String userId, Collection<String> roles, String taskId);

    Customer updateDtoTaskCompleted(String status, CusFirstLineCheckDto dto, String userId, String taskId);

    Customer updateDtoTaskCompleted(String status, CusFirstLineCheckDto dto, String userId, String taskId, Map<String, Object> variables);

    void taskApproved(String userId, String taskId);
    void taskApproved(String userId, String taskId, Map<String, Object> variables);

    void taskRejected(String userId, String taskId);
}
