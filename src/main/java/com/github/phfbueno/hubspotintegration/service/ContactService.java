package com.github.phfbueno.hubspotintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phfbueno.hubspotintegration.auth.TokenManager;
import com.github.phfbueno.hubspotintegration.config.HubspotConfig;
import com.github.phfbueno.hubspotintegration.dto.ContactRequestDTO;
import com.github.phfbueno.hubspotintegration.dto.ContactResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);

    private final HubspotConfig hubspotConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final TokenManager tokenManager;

    public ContactService(HubspotConfig hubspotConfig, RestTemplate restTemplate, ObjectMapper objectMapper, TokenManager tokenManager) {
        this.hubspotConfig = hubspotConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.tokenManager = tokenManager;
    }


    public ContactResponseDTO createContact(ContactRequestDTO dto) {
        log.info("Iniciando criação de contato no HubSpot");

        String url = "https://api.hubapi.com/crm/v3/objects/contacts";


        Map<String, Object> properties = new HashMap<>();
        properties.put("firstname", dto.firstName());
        properties.put("lastname", dto.lastName());
        properties.put("email", dto.email());
        properties.put("phone", dto.phone());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("properties", properties);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.setBearerAuth(tokenManager.getAccessToken());
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String id = root.path("id").asText();

                log.info("Contato criado com sucesso. ID: {}", id);
                return new ContactResponseDTO(
                        id,
                        dto.firstName(),
                        dto.lastName(),
                        dto.email(),
                        dto.phone()
                );
            } else {
                log.error("Erro ao criar contato no HubSpot: status {}", response.getStatusCode());
                throw new RuntimeException("Erro ao criar contato no HubSpot.");
            }
        } catch (Exception e) {
            log.error("Exceção ao criar contato: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na integração com o HubSpot.");
        }
    }
}