package com.github.phfbueno.hubspotintegration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HubspotIntegrationApplicationTests {

	@Autowired
	private RestTemplate restTemplate;


	@Test
	void contextLoads() {
		assertThat(restTemplate).isNotNull();
	}

}
