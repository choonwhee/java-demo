package com.demo.test.demo;

import com.demo.test.DemoApplication;
import com.demo.test.common.workflow.dto.EntityTask;
import com.demo.test.common.workflow.dto.TaskStatus;
import com.demo.test.customer.dto.*;
import com.demo.test.customer.service.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {DemoApplication.class})
@Transactional
public class CustomerServiceTest {
    Logger logger = LoggerFactory.getLogger(CustomerServiceTest.class);

    @Autowired
    private CustomerService customerService;

    @Test
    public void save() {
        saveCustomerAndStartProcess();

        CusNewDto cusDto = createTestCusNewDto();
        Map<String, Object> vars = new HashMap<>();
        vars.put("submitFormStatus", "submitted");
        Customer cus = customerService.saveDtoAndStartProcess(cusDto, "2222222", vars);
        Assertions.assertNotNull(cus.getId());
        Assertions.assertNotNull(cus.getFirstName());
        Assertions.assertNotNull(cus.getLastName());

        Customer customer = createTestCustomer();
        cus = customerService.save(customer);
        logger.info("customerService.save " + cus);
        Assertions.assertNotNull(cus.getId());
        Assertions.assertNotNull(cus.getFirstName());
        Assertions.assertNotNull(cus.getLastName());

        customer = createTestCustomer();
        CusNewDto dto = customerService.saveAndGetDto(customer);
        logger.info("customerService.save and return Dto: " + dto);
        Assertions.assertNotNull(dto.getId());
        Assertions.assertNotNull(dto.getFirstName());
        Assertions.assertNotNull(dto.getLastName());

        cusDto = createTestCusNewDto();
        CusNewDto returnDto = customerService.saveDto(cusDto);
        logger.info("customerService.save Dto into Entity And return Dto: " + dto);
        Assertions.assertNotNull(returnDto.getId());
        Assertions.assertNotNull(returnDto.getFirstName());
        Assertions.assertNotNull(returnDto.getLastName());

        Optional<Customer> cusOp = customerService.findById(1L);
        Assertions.assertTrue(cusOp.isPresent());
        cus = cusOp.get();
        Assertions.assertEquals(cus.getId(),1L);
        Assertions.assertEquals(cus.getFirstName(), "Terry");
        Assertions.assertEquals(cus.getLastName(), "Bogard");
        Assertions.assertEquals(cus.getAddress(), "Texas, USA");
        Assertions.assertEquals(cus.getPhone(), "123456789");
        Assertions.assertEquals(cus.getCountry(), "US");
        Assertions.assertEquals(cus.getEmail(), "tb@gmail.com");

        //check that existing values of old record not in Dto remains the same
        Assertions.assertEquals(cus.getFirstLineCheckComments(), "Test First Line Review Comments.");
        Assertions.assertEquals(cus.getFinalReviewComments(), "Test Final Review Comments.");


    }

    private Customer createTestCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Kyo");
        customer.setLastName("Kusanagi");
        customer.setAddress("123 Shibuya, Tokyo");
        customer.setCountry("JP");
        customer.setPhone("234234424");
        customer.setEmail("kyo@kof.co.jp");
        customer.setFirstLineCheckComments("Test First Line Review Comments.");
        customer.setFinalReviewComments("Test Final Review Comments.");
        return customer;
    }

    private CusNewDto createTestCusNewDto() {
        CusNewDto cusDto = new CusNewDto();
        cusDto.setId(1L);
        cusDto.setFirstName("Terry");
        cusDto.setLastName("Bogard");
        cusDto.setAddress("Texas, USA");
        cusDto.setCountry("US");
        cusDto.setPhone("123456789");
        cusDto.setEmail("tb@gmail.com");

        return cusDto;
    }

    @Test
    public void simpleSearch() {
        saveCustomerAndStartProcess();
        List<Long> idBtw = new ArrayList<>();
        idBtw.add(10L);
        idBtw.add(20L);
        List<Customer> cusList = customerService.simpleSearchByIdAndName(idBtw, "K", "ana");
        Assertions.assertNotNull(cusList);
        Assertions.assertTrue(cusList.size() > 0);

        List<CusNewDto> cusDtoList = customerService.simpleSearchDtoByIdAndName(idBtw, "K", "ana");
        Assertions.assertNotNull(cusDtoList);
        Assertions.assertTrue(cusDtoList.size() > 0);
        cusDtoList.forEach(dto -> {
                Assertions.assertTrue(dto.getId() >= 10L && dto.getId() <= 20L);
                Assertions.assertNotNull(dto.getFirstName());
                Assertions.assertNotNull(dto.getLastName());
            }
        );
    }

    @Test
    public void simpleSearchPageable() {
        saveCustomerAndStartProcess();

        Pageable pageable = PageRequest.of(0, 5);
        Page<Customer> cusPage = customerService.simpleSearch(pageable, null);
        Assertions.assertNotNull(cusPage);
        Assertions.assertTrue(cusPage.getTotalPages() > 0);
        Assertions.assertEquals(cusPage.getNumberOfElements(), cusPage.getContent().size());
        for (Customer cus : cusPage.getContent()) {
            Assertions.assertNotNull(cus);
        }
        logger.info("Customer page: ");
        logger.info("Total Elements: " + cusPage.getTotalElements());
        logger.info("Total Pages: " + cusPage.getTotalPages());
        logger.info("Number Of Elements: " + cusPage.getNumberOfElements());
        logger.info("Size: " + cusPage.getSize());
        logger.info("Number: " + cusPage.getNumber());
        logger.info("Is First: " + cusPage.isFirst());
        logger.info("Is Last: " + cusPage.isLast());
        logger.info("Has Previous: " + cusPage.hasPrevious());
        logger.info("Has Next: " + cusPage.hasNext());
        logger.info("Is Empty: " + cusPage.isEmpty());
        logger.info("Has Content: " + cusPage.hasContent());
        logger.info("Content: " + cusPage.getContent());
        logger.info("Sort: " + cusPage.getSort());
        logger.info("Pageable: " + cusPage.getPageable());
        logger.info("Next Or Last Pageable: " + cusPage.nextOrLastPageable());
        logger.info("Previous Or First Pageable: " + cusPage.previousOrFirstPageable());
        logger.info("Previous Pageable: " + cusPage.previousPageable());
        logger.info("Next Pageable: " + cusPage.nextPageable());

        cusPage.stream().forEach(customer -> logger.info("Customer: " + customer));

        pageable = PageRequest.of(0, 5);
        List<Long> idBtw = new ArrayList<>();
        idBtw.add(10L);
        idBtw.add(20L);
        cusPage = customerService.simpleSearchByIdAndName(pageable, idBtw, "K", "ana");
        Assertions.assertNotNull(cusPage);
        Assertions.assertTrue(cusPage.getTotalPages() > 0);
        Assertions.assertEquals(cusPage.getNumberOfElements(), cusPage.getContent().size());
        for (Customer cus : cusPage.getContent()) {
            Assertions.assertNotNull(cus);
        }
        logger.info("Customer page: ");
        logger.info("Total Elements: " + cusPage.getTotalElements());
        logger.info("Total Pages: " + cusPage.getTotalPages());
        logger.info("Number Of Elements: " + cusPage.getNumberOfElements());
        logger.info("Size: " + cusPage.getSize());
        logger.info("Number: " + cusPage.getNumber());
        cusPage.stream().forEach(customer -> logger.info("Customer: " + customer));
    }

    @Test
    public void searchPageable() {
        saveCustomerAndStartProcess();

        Pageable pageable = PageRequest.of(0, 5);
        CustomerSearchDto searchDto = new CustomerSearchDto();
        Long[] idBtwArray = {10L, 20L};
        searchDto.setIdCoi("10");
        Page<Customer> cusPage = customerService.search(pageable, searchDto);
        Assertions.assertNotNull(cusPage);
        Assertions.assertTrue(cusPage.getTotalPages() > 0);
        Assertions.assertEquals(cusPage.getNumberOfElements(), cusPage.getContent().size());
        for (Customer cus : cusPage.getContent()) {
            Assertions.assertNotNull(cus);
        }
        logger.info("Customer page: ");
        logger.info("Total Elements: " + cusPage.getTotalElements());
        logger.info("Total Pages: " + cusPage.getTotalPages());
        logger.info("Number Of Elements: " + cusPage.getNumberOfElements());
        logger.info("Size: " + cusPage.getSize());
        logger.info("Number: " + cusPage.getNumber());
        cusPage.stream().forEach(customer -> logger.info("Customer: " + customer));
    }

    @Test
    public void simpleSearchWorkflowTask() throws InterruptedException {
        saveCustomerAndStartProcess();
        List<EntityTask<Customer>> cusWtList = customerService.simpleSearchTaskByAssignee("6666666");
        Assertions.assertNotNull(cusWtList);
        Assertions.assertTrue(cusWtList.size() > 0);

        List<TaskStatus> taskStatuses = new ArrayList<>();
        taskStatuses.add(TaskStatus.ACTIVE);
        taskStatuses.add(TaskStatus.COMPLETED);
        taskStatuses.add(TaskStatus.DELETED);
        cusWtList = customerService.simpleSearchTaskByTaskStatus(taskStatuses);
        Assertions.assertNotNull(cusWtList);
        Assertions.assertTrue(cusWtList.size() > 0);

        cusWtList = customerService.simpleSearchTaskByNameAndTaskStatus("Yo", "kuSaN", "@KOF.co", taskStatuses);
        Assertions.assertNotNull(cusWtList);
        Assertions.assertTrue(cusWtList.size() > 0);

        Map<String, Object> searchMap = new HashMap<>();
        List<Long> idBtw = new ArrayList<>();
        idBtw.add(13L);
        idBtw.add(20L);
        searchMap.put("idBtw", idBtw);
        searchMap.put("firstNameStw", "Ky");
        searchMap.put("LastNameCon", "sanag");
        searchMap.put("emailEnw", ".co.jp");
        searchMap.put("workflowTaskAssigneeEql", "6666666");
        cusWtList = customerService.simpleSearchTask(searchMap);
        Assertions.assertNotNull(cusWtList);
        Assertions.assertTrue(cusWtList.size() > 0);

        List<CustomerWorkflowTaskDto> cusWtDtoList = customerService.simpleSearchTaskDtoByNameAndTaskStatus("Yo", "kuSaN", "@KOF.co", taskStatuses);
        Assertions.assertNotNull(cusWtDtoList);
        Assertions.assertTrue(cusWtDtoList.size() > 0);
        cusWtDtoList.forEach(dto -> {
            Assertions.assertTrue(dto.getEntityId() >= 10L && dto.getEntityId() <= 20L);
            Assertions.assertTrue(dto.getEntityFirstName().startsWith("Ky"));
            Assertions.assertTrue(dto.getEntityLastName().contains("sanagi"));
            Assertions.assertTrue(dto.getEntityEmail().endsWith(".co.jp"));
            Assertions.assertTrue(dto.getTaskAssignee().endsWith("6666666"));
            Assertions.assertNotNull(dto.getTaskTaskStatus());
            Assertions.assertNotNull(dto.getTaskTaskDefinitionKey());
            Assertions.assertNotNull(dto.getTaskId());
        });

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        EntityTask<Customer> cusWf = customerService.simpleSearchTaskByCusId(13L);
        logger.info("customerService.simpleSearchWorkflowTaskByCusId: " + cusWf);
        Assertions.assertNotNull(cusWf);
        Assertions.assertNotNull(cusWf.getEntity());
        Assertions.assertNotNull(cusWf.getTask());
        Assertions.assertTrue(cusWf.getEntity().getId() == 13L);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
        CustomerWorkflowTaskDto dto = customerService.simpleSearchTaskDtoByCusId(13L);
        logger.info("customerService.simpleSearchWorkflowTaskDtoByCusId: " + dto);
        Assertions.assertNotNull(dto);
        Assertions.assertTrue(dto.getEntityId() == 13L);
        Assertions.assertNotNull(dto.getEntityFirstName());
        Assertions.assertNotNull(dto.getEntityLastName());
        Assertions.assertNotNull(dto.getEntityEmail());
        Assertions.assertNotNull(dto.getTaskAssignee());
        Assertions.assertNotNull(dto.getTaskTaskStatus());
        Assertions.assertNotNull(dto.getTaskTaskDefinitionKey());
        Assertions.assertNotNull(dto.getTaskId());

    }

    @Test
    public void simpleSearchWorkflowTaskPageable() throws InterruptedException {
        saveCustomerAndStartProcess();
        Pageable pageable = PageRequest.of(0, 5);
        Page<EntityTask<Customer>> cusWtPage = customerService.simpleSearchTaskByAssignee(pageable, "6666666");
        Assertions.assertNotNull(cusWtPage);
        Assertions.assertTrue(cusWtPage.getContent().size() > 0);

        List<TaskStatus> taskStatuses = new ArrayList<>();
        taskStatuses.add(TaskStatus.ACTIVE);
        taskStatuses.add(TaskStatus.COMPLETED);
        taskStatuses.add(TaskStatus.DELETED);
        cusWtPage = customerService.simpleSearchTaskByTaskStatus(pageable, taskStatuses);
        Assertions.assertNotNull(cusWtPage);
        Assertions.assertTrue(cusWtPage.getContent().size() > 0);

        cusWtPage = customerService.simpleSearchTaskByNameAndTaskStatus(pageable, "Yo", "kuSaN", "@KOF.co", taskStatuses);
        Assertions.assertNotNull(cusWtPage);
        Assertions.assertTrue(cusWtPage.getContent().size() > 0);

        Map<String, Object> searchMap = new HashMap<>();
        List<Long> idBtw = new ArrayList<>();
        idBtw.add(13L);
        idBtw.add(20L);
        searchMap.put("idBtw", idBtw);
        searchMap.put("firstNameStw", "Ky");
        searchMap.put("LastNameCon", "sanag");
        searchMap.put("emailEnw", ".co.jp");
        searchMap.put("workflowTaskAssigneeEql", "6666666");
        cusWtPage = customerService.simpleSearchTask(pageable, searchMap);
        Assertions.assertNotNull(cusWtPage);
        Assertions.assertTrue(cusWtPage.getContent().size() > 0);

        Page<CustomerWorkflowTaskDto> cusWtDtoPage = customerService.simpleSearchTaskDtoByNameAndTaskStatus(pageable, "Yo", "kuSaN", "@KOF.co", taskStatuses);
        Assertions.assertNotNull(cusWtDtoPage);
        Assertions.assertTrue(cusWtDtoPage.getContent().size() > 0);
        cusWtDtoPage.forEach(dto -> {
            Assertions.assertTrue(dto.getEntityId() >= 10L && dto.getEntityId() <= 20L);
            Assertions.assertTrue(dto.getEntityFirstName().startsWith("Ky"));
            Assertions.assertTrue(dto.getEntityLastName().contains("sanagi"));
            Assertions.assertTrue(dto.getEntityEmail().endsWith(".co.jp"));
            Assertions.assertTrue(dto.getTaskAssignee().endsWith("6666666"));
            Assertions.assertNotNull(dto.getTaskTaskStatus());
            Assertions.assertNotNull(dto.getTaskTaskDefinitionKey());
            Assertions.assertNotNull(dto.getTaskId());
        });
    }

    private void saveCustomerAndStartProcess() {
        Customer customer = createTestCustomer();
        Map<String, Object> vars = new HashMap<>();
        vars.put("submitFormStatus", "submitted");
        customerService.saveAndStartProcess(customer, "2222222", vars);
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
    }

    @Test
    public void workflowTaskComplete() {
        saveCustomerAndStartProcess();
        List<TaskStatus> taskStatuses = new ArrayList<>();
        taskStatuses.add(TaskStatus.ACTIVE);
        List<EntityTask<Customer>> cusWtList = customerService.simpleSearchTaskByUserRightsAndTaskStatus("6666666", null, taskStatuses);
        Assertions.assertNotNull(cusWtList);
        Assertions.assertTrue(cusWtList.size() > 0);
        EntityTask<Customer> cusWt = cusWtList.get(0);
        customerService.taskApproved("6666666", cusWt.getTask().getId());
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
        saveCustomerAndStartProcess();
        cusWtList = customerService.simpleSearchTaskByUserRightsAndTaskStatus("6666666", null, taskStatuses);
        Assertions.assertNotNull(cusWtList);
        Assertions.assertTrue(cusWtList.size() > 0);
        cusWt = cusWtList.get(0);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
        Map<String, Object> vars = new HashMap<>();
        vars.put("testVar1", "testVal1");
        vars.put("testVar2", 2L);
        vars.put("testVar3", 3);
        vars.put("testVar4", true);
        //customerService.taskApproved("6666666", cusWt.getTask().getId(), vars);

        CusFirstLineCheckDto dto = new CusFirstLineCheckDto();
        dto.setId(cusWt.getEntity().getId());
        String comments = "Test First Line Check Comments";
        dto.setFirstLineCheckComments(comments);
        customerService.updateDtoTaskCompleted("approved", dto, "6666666", cusWt.getTask().getId(), vars);

        cusWt = customerService.simpleSearchTaskByTaskId(cusWt.getTask().getId());
        Assertions.assertNotNull(cusWt);
        Map<String, String> processVars = cusWt.getTask().getProcessInstance().getVariables();
        Assertions.assertNotNull(processVars);
        Assertions.assertEquals(processVars.get("testVar1"), "testVal1");
        Assertions.assertEquals(processVars.get("testVar2"), "2");
        Assertions.assertEquals(processVars.get("testVar3"), "3");
        Assertions.assertEquals(processVars.get("testVar4"), "true");
        Assertions.assertEquals(cusWt.getEntity().getFirstLineCheckComments(), comments);
    }

}
