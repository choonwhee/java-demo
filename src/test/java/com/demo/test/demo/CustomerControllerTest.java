package com.demo.test.demo;

import com.demo.test.DemoApplication;
import com.demo.test.common.workflow.dto.EntityTask;
import com.demo.test.common.workflow.dto.Task;
import com.demo.test.customer.dto.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {DemoApplication.class})
class CustomerControllerTest {
	Logger logger = LoggerFactory.getLogger(CustomerControllerTest.class);

	@LocalServerPort
	private int port;

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void createCustomer() {
		final String firstName = "Peter";
		final String lastName = "Parker";
		final String address = "Marvel Cinematic Universes";
		final String phone = "194139844";
		final String email = "spidey@avengers.com";
		final String country = "UK";
		final String requestStr = "{\n" +
				"    \"firstName\": \"" + firstName + "\",\n" +
				"    \"lastName\": \"" + lastName + "\",\n" +
				"    \"address\": \"" + address + "\",\n" +
				"    \"phone\": \"" + phone + "\",\n" +
				"    \"email\": \"" + email + "\",\n" +
				"    \"country\": \"" + country + "\"\n" +
				"}";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(requestStr, headers);
		ResponseEntity<Customer> response = restTemplate.withBasicAuth("1111111", "password").postForEntity("http://localhost:" + port + "/rest/customers", request, Customer.class);
		Assertions.assertEquals(200, response.getStatusCodeValue());
		Customer cus = response.getBody();
		Assertions.assertNotNull(cus);
		logger.info("Updated Customer: " + cus);
		Assertions.assertNotNull(cus.getId());
		Assertions.assertEquals(firstName, cus.getFirstName());
		Assertions.assertEquals(lastName, cus.getLastName());
		Assertions.assertEquals(address, cus.getAddress());
		Assertions.assertEquals(phone, cus.getPhone());
		Assertions.assertEquals(email, cus.getEmail());
		Assertions.assertEquals(country, cus.getCountry());
	}

	@Test
	public void updateCustomer() {
		final String firstName = "Miles";
		final String lastName = "Morales";
		final String address = "Marvel Comics";
		final String phone = "11414245245";
		final String email = "spidey2@marvel.com";
		final String country = "US";
		final String requestStr = "{\n" +
				"    \"firstName\": \"" + firstName + "\",\n" +
				"    \"lastName\": \"" + lastName + "\",\n" +
				"    \"address\": \"" + address + "\",\n" +
				"    \"phone\": \"" + phone + "\",\n" +
				"    \"email\": \"" + email + "\",\n" +
				"    \"country\": \"" + country + "\"\n" +
				"}";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(requestStr, headers);
		restTemplate.withBasicAuth("1111111", "password").put("http://localhost:" + port + "/rest/customers/3", request);

		ResponseEntity<Customer> response = restTemplate.withBasicAuth("1111111", "password").getForEntity("http://localhost:" + port + "/rest/customers/3", Customer.class);
		Assertions.assertEquals(200, response.getStatusCodeValue());
		Customer cus = response.getBody();
		Assertions.assertNotNull(cus);
		logger.info("Updated Customer: " + cus);
		Assertions.assertNotNull(cus.getId());
		Assertions.assertEquals(firstName, cus.getFirstName());
		Assertions.assertEquals(lastName, cus.getLastName());
		Assertions.assertEquals(address, cus.getAddress());
		Assertions.assertEquals(phone, cus.getPhone());
		Assertions.assertEquals(email, cus.getEmail());
		Assertions.assertEquals(country, cus.getCountry());
	}

	@Test
	public void listCustomers() {
		ResponseEntity<String> response = restTemplate.withBasicAuth("1111111", "password").getForEntity("http://localhost:" + port + "/rest/customers", String.class);
		logger.info("List Customers Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	public void searchCustomers() {
		ResponseEntity<String> response = restTemplate.withBasicAuth("1111111", "password").getForEntity("http://localhost:" + port + "/rest/customers?idBtw=5,10", String.class);
		logger.info("List Customers Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());

		response = restTemplate.withBasicAuth("1111111", "password").getForEntity("http://localhost:" + port + "/rest/customers?countryInc=SO,UK,JP", String.class);
		logger.info("List Customers Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	public void searchCustomerWorkflowTasks() {
		createCustomer();
		ResponseEntity<String> response = restTemplate.withBasicAuth("2222222", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks?countryInc=UK&taskTaskStatusInc=ACTIVE,COMPLETED", String.class);
		Assertions.assertEquals(200, response.getStatusCodeValue());

		ObjectMapper objectMapper = new ObjectMapper();
		List<EntityTask<Customer>> customerWorkflowTasks = null;
		try {
			customerWorkflowTasks = objectMapper.readValue(response.getBody(), new TypeReference<List<EntityTask<Customer>>>() {});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		Assertions.assertNotNull(customerWorkflowTasks);
		Assertions.assertTrue(customerWorkflowTasks.size() > 0);

		logger.info("customerWorkflowTasks: " + customerWorkflowTasks.toString());
		customerWorkflowTasks.forEach(cusWfTask -> {
			Customer cus = cusWfTask.getEntity();
			Task task = cusWfTask.getTask();
			logger.info("Customer ID: " + cus.getId() + ", Name: " + cus.getFirstName() + " " + cus.getLastName() + ", Task ID: " + task.getId() + ", Name: " + task.getName() + ", Status: " + task.getTaskStatus());
		});
	}

	@Test
	public void getCustomer() {
		ResponseEntity<String> response = restTemplate.withBasicAuth("1111111", "password").getForEntity("http://localhost:" + port + "/rest/customers/1", String.class);
		logger.info("Get Customers Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	public void deleteCustomer() {
		ResponseEntity<String> response = restTemplate.withBasicAuth("1111111", "password").getForEntity("http://localhost:" + port + "/rest/customers/2", String.class);
		Assertions.assertEquals(200, response.getStatusCodeValue());
		restTemplate.withBasicAuth("1111111", "password").delete("http://localhost:" + port + "/rest/customers/2");
		response = restTemplate.withBasicAuth("1111111", "password").getForEntity("http://localhost:" + port + "/rest/customers/2", String.class);
		Assertions.assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void listCustomerWorkflowTasksAssignedToUser() {
		createCustomer();
		ResponseEntity<String> response = restTemplate.withBasicAuth("2222222", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks/assignee", String.class);
		logger.info("List Customer Tasks Assigned to Logged in User: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());


	}

	@Test
	public void listCustomerTasksUserIsInCandidateGroup() {
		approveTasks();

		ResponseEntity<String> response = restTemplate.withBasicAuth("3333333", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks/roles", String.class);
		logger.info("List Customer Tasks Where User(3333333) is in Candidate Group: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());

		response = restTemplate.withBasicAuth("4444444", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks/roles", String.class);
		logger.info("List Customer Tasks Where User(4444444) is in Candidate Group: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());
	}

	/*@Test
	public void listCustomerWorkflowTasksUserIsCandidateUser() {
		createCustomer();
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/rest/customers/tasks/candidateGroup", String.class);
		logger.info("List Customer Tasks Assigned to Logged in User is a Candidate User: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());
	}*/

	@Test
	public void approveTasks() {
		completeTasks("approve");
	}


	@Test
	public void rejectTasks() {
		completeTasks("reject");
	}

	@Test
	public void fullProcess() {
		approveTasks();
		logger.info("Get TaskList of User belonging to CUS_FR_GROUP");
		ResponseEntity<String> response = restTemplate.withBasicAuth("3333333", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks/roles", String.class);
		logger.info("Search Customer Tasks Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());

		ObjectMapper objectMapper = new ObjectMapper();
		List<EntityTask<Customer>> customerTasks = null;
		try {
			customerTasks = objectMapper.readValue(response.getBody(), new TypeReference<List<EntityTask<Customer>>>() {});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		logger.info("customerTasks: " + customerTasks.toString());
		customerTasks.forEach(cusWfTask -> {
			Customer cus = cusWfTask.getEntity();
			Task task = cusWfTask.getTask();
			logger.info("Customer ID: " + cus.getId() + ", Name: " + cus.getFirstName() + " " + cus.getLastName() + ", Task ID: " + task.getId() + ", Name: " + task.getName() + ", Status: " + task.getTaskStatus());
		});
		EntityTask<Customer> customerTask = customerTasks.get(0);
		Task task = customerTask.getTask();

		response = restTemplate.withBasicAuth("3333333", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks/" + task.getId(), String.class);
		logger.info("Get Customer Tasks Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());

		response = restTemplate.withBasicAuth("4444444", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks/" + task.getId(), String.class);
		logger.info("Get Customer Tasks Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>("{}", headers);
		response = restTemplate.withBasicAuth("3333333", "password").postForEntity("http://localhost:" + port + "/rest/customers/tasks/" + task.getId() + "/approve", request, String.class);
		logger.info("Complete Tasks (Approve) Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());

		response = restTemplate.withBasicAuth("3333333", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks?taskTaskStatusInc=ACTIVE&taskIdEql=" + task.getId(), String.class);
		logger.info("Get Customer Tasks Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());
		try {
			customerTasks = objectMapper.readValue(response.getBody(), new TypeReference<List<EntityTask<Customer>>>() {});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		Assertions.assertTrue(customerTasks.size() == 0);

		response = restTemplate.withBasicAuth("4444444", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks?taskTaskStatusInc=ACTIVE&taskIdEql=" + task.getId(), String.class);
		logger.info("Get Customer Tasks Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());
		try {
			customerTasks = objectMapper.readValue(response.getBody(), new TypeReference<List<EntityTask<Customer>>>() {});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		Assertions.assertTrue(customerTasks.size() == 0);

	}


	public void completeTasks(String action) {
		createCustomer();
		logger.info("Get Customer Tasks Assigned to LM");
		ResponseEntity<String> response = restTemplate.withBasicAuth("2222222", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks/assignee", String.class);
		Assertions.assertEquals(200, response.getStatusCodeValue());

		ObjectMapper objectMapper = new ObjectMapper();
		List<EntityTask<Customer>> customerTasks = null;
		try {
			customerTasks = objectMapper.readValue(response.getBody(), new TypeReference<List<EntityTask<Customer>>>() {});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		logger.info("customerTasks: " + customerTasks.toString());
		customerTasks.forEach(cusWfTask -> {
			Customer cus = cusWfTask.getEntity();
			Task task = cusWfTask.getTask();
			logger.info("Customer ID: " + cus.getId() + ", Name: " + cus.getFirstName() + " " + cus.getLastName() + ", Task ID: " + task.getId() + ", Name: " + task.getName() + ", Status: " + task.getTaskStatus());
		});
		EntityTask<Customer> customerWorkflowTask = customerTasks.get(0);
		Task task = customerWorkflowTask.getTask();

		logger.info("Get Customer Task (" + task.getId() + ")");
		response = restTemplate.withBasicAuth("2222222", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks/" + task.getId(), String.class);
		logger.info("Get Customer Tasks Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());

		logger.info("Complete Task (" + action + ")");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>("{}", headers);
		response = restTemplate.withBasicAuth("2222222", "password").postForEntity("http://localhost:" + port + "/rest/customers/tasks/" + task.getId() + "/" + action, request, String.class);
		logger.info("Complete Tasks (" + action + ") Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());

		logger.info("Get Customer Task" + task.getId());
		response = restTemplate.withBasicAuth("2222222", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks/" + task.getId(), String.class);
		logger.info("Get Customer Tasks Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());

		logger.info("Check Task no longer with LM");
		response = restTemplate.withBasicAuth("2222222", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks?taskIdEql=" + task.getId() + "&taskTaskStatusInc=ACTIVE", String.class);
		logger.info("Search Customer Tasks Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());
		try {
			customerTasks = objectMapper.readValue(response.getBody(), new TypeReference<List<EntityTask<Customer>>>() {});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		Assertions.assertTrue(customerTasks.size() == 0);

		logger.info("Get Customer Tasks Assigned to User with CUS_FR_GROUP role");
		response = restTemplate.withBasicAuth("3333333", "password").getForEntity("http://localhost:" + port + "/rest/customers/tasks/assignee", String.class);
		logger.info("Search Customer Tasks Response: " + response.getBody());
		Assertions.assertEquals(200, response.getStatusCodeValue());

		try {
			customerTasks = objectMapper.readValue(response.getBody(), new TypeReference<List<EntityTask<Customer>>>() {});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		logger.info("customerTasks: " + customerTasks.toString());
		customerTasks.forEach(cusTask -> {
			Customer cus = cusTask.getEntity();
			Task cTask = cusTask.getTask();
			logger.info("Customer ID: " + cus.getId() + ", Name: " + cus.getFirstName() + " " + cus.getLastName() + ", Task ID: " + cTask.getId() + ", Name: " + cTask.getName() + ", Status: " + cTask.getTaskStatus());
		});
	}



}
