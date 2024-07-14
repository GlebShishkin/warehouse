package ru.stepup.warehouse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import ru.stepup.warehouse.repository.InstanceArrangementRepo;
import ru.stepup.warehouse.repository.ProductRegisterRepo;
import ru.stepup.warehouse.repository.ProductRepo;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class WarehouseApplicationTests {

	@Autowired
	private ProductRegisterRepo productRegisterRepo;
	@Autowired
	private ProductRepo productRepo;
	@Autowired
	private InstanceArrangementRepo instanceArrangementRepo;

	@BeforeEach
	void setUp() {
		// перед тестами очистим таблицы, кот. будем заполнять (продуктов, регистра и соглашений)
		instanceArrangementRepo.deleteAll();
		productRegisterRepo.deleteAll();
		productRepo.deleteAll();
	}

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	@DisplayName("Test request corporate_settlemet_account/create")
	void sendPost_Account() throws URISyntaxException {
		final String baseUrl = "http://localhost:8080/corporate_settlemet_account/create";
		URI uri = new URI(baseUrl);

		String message = "{\"data\":{\n" +
				"    \"instanceid\":10012,\n" +
				"    \"registryTypeCode\": \"03.012.002_47533_ComSoLd\",\n" +
				"    \"accountType\": \"Клиентский\",\n" +
				"    \"currencyCode\": \"800\",\n" +
				"    \"branchCode\": \"0022\",\n" +
				"    \"priorityCode\": \"00\",\n" +
				"    \"mdmCode\": 15,\n" +
				"    \"clientCode\": \"\",\n" +
				"    \"trainRegion\": \"\",\n" +
				"    \"counter\": \"\",\n" +
				"    \"salesCode\": \"001\"\n" +
				"}\n" +
				"}";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<>(message, headers);
		ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

		System.out.println("########## result.getStatusCode() = " + result.getStatusCode());
		System.out.println(result.getBody().toString());
		Assertions.assertEquals(result.getStatusCode(), HttpStatus.OK);	// проверим статус ответа

		ObjectMapper mapper = new ObjectMapper();
		try {
			// проверим содержание полей json в ответе
			JsonNode rootNode = mapper.readTree(result.getBody());
			Assertions.assertFalse(rootNode.path("data").path("accountid").isMissingNode());
			Assertions.assertNotNull(rootNode.path("data").path("accountid").asText());
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Test
	@DisplayName("Test request corporate_settlemet_instance/create. Create new instance")
	void sendPost_New_Instance() throws URISyntaxException {
		final String baseUrl = "http://localhost:8080/corporate_settlemet_instance/create";
		URI uri = new URI(baseUrl);

		String message = "{\"data\":\n" +
				"    {\n" +
				"    \"instanceId\": null,\n" +
				"    \"productType\": \"НСО\",\n" +
				"    \"productCode\": \"03.012.002\",\n" +
				"    \"contractDate\": \"2024-07-10\",\n" +
				"    \"openingDate\": \"2024-07-10\",\n" +
				"    \"mdmCode\": \"15\",\n" +
				"    \"BranchCode\": \"0022\",\n" +
				"    \"priorityCode\": \"00\",\n" +
				"    \"currencyCode\": \"800\",\n" +
				"    \"contractNumber\": \"A223344\",\n" +
				"    \"priority\": \"10\",\n" +
				"    \"registryTypeCode\": \"03.012.002_47533_ComSoLd\",\n" +
				"    \"instanceArrangement\": [\n" +
				"        {\n" +
				"        \"SupplementaryAgreementId\": \"AgreementN1\",\n" +
				"        \"Number\": \"5\",\n" +
				"        \"openingDate\": \"2024-07-10\",\n" +
				"        \"cancellationReason\": \"cancellationReason5\"\n" +
				"        },\n" +
				"        {\n" +
				"        \"SupplementaryAgreementId\": \"AgreementN2\",\n" +
				"        \"Number\": \"6\",\n" +
				"        \"openingDate\": \"2024-07-11\",\n" +
				"        \"cancellationReason\": \"cancellationReason6\"        \n" +
				"        }\n" +
				"        ],\n" +
				"    \"additionalPropertiesVip\": [\n" +
				"        {\n" +
				"            \"key\": \"RailwayRegionOwn\",\n" +
				"            \"value\": \"ABC\", \n" +
				"            \"name\": \"Регион принадлежности железной дороги\" \n" +
				"        }\n" +
				"        ]\n" +
				"    }\n" +
				"}";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<>(message, headers);
		ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

		System.out.println("########## result.getStatusCode() = " + result.getStatusCode());
		System.out.println(result.getBody().toString());
		Assertions.assertEquals(result.getStatusCode(), HttpStatus.OK);	// проверим статус ответа

		ObjectMapper mapper = new ObjectMapper();
		try {
			// проверим содержание полей json в ответе
			JsonNode rootNode = mapper.readTree(result.getBody());
			Assertions.assertFalse(rootNode.path("data").path("instanceId").isMissingNode());
			// проверим, что добавилось 2 соглашения
			Assertions.assertEquals(rootNode.path("data").path("supplementaryAgreementId").size(), 2);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}

