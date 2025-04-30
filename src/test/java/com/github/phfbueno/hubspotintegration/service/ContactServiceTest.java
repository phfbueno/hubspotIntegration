package com.github.phfbueno.hubspotintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phfbueno.hubspotintegration.auth.TokenManager;
import com.github.phfbueno.hubspotintegration.config.HubspotConfig;
import com.github.phfbueno.hubspotintegration.dto.ContactRequestDTO;
import com.github.phfbueno.hubspotintegration.dto.ContactResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private HubspotConfig hubspotConfig;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TokenManager tokenManager;

    @InjectMocks
    private ContactService contactService;

    @Test
    void deveCriarContatoComSucesso() throws Exception {

        ContactRequestDTO dto = new ContactRequestDTO("John", "Doe", "john.doe@example.com", "123456789");
        String token = "mock-token";
        String expectedId = "123456";

        String responseBody = """
            {
                "id": "%s"
            }
        """.formatted(expectedId);

        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.CREATED);

        when(tokenManager.getAccessToken()).thenReturn(token);
        when(hubspotConfig.getContactUrl()).thenReturn("https://api.hubapi.com/crm/v3/objects/contacts");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class))).thenReturn(response);

        JsonNode jsonNode = new ObjectMapper().readTree(responseBody);
        when(objectMapper.readTree(responseBody)).thenReturn(jsonNode);

        ContactResponseDTO result = contactService.createContact(dto);

        assertEquals(expectedId, result.contactId());
        assertEquals(dto.firstName(), result.firstName());
        assertEquals(dto.lastName(), result.lastName());
        assertEquals(dto.email(), result.email());
        assertEquals(dto.phone(), result.phone());
    }

    @Test
    void deveLancarExcecaoQuandoStatusNaoFor2xx() {

        ContactRequestDTO dto = new ContactRequestDTO("Jane", "Doe", "jane.doe@example.com", "987654321");
        String token = "mock-token";

        ResponseEntity<String> response = new ResponseEntity<>("Erro", HttpStatus.BAD_REQUEST);

        when(tokenManager.getAccessToken()).thenReturn(token);
        when(hubspotConfig.getContactUrl()).thenReturn("https://api.hubapi.com/crm/v3/objects/contacts");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class))).thenReturn(response);


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contactService.createContact(dto);
        });

        assertEquals("Falha na integração com o HubSpot.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoOcorreErroNaRequisicao() {

        ContactRequestDTO dto = new ContactRequestDTO("Ana", "Silva", "ana@example.com", "111222333");
        String token = "mock-token";

        when(tokenManager.getAccessToken()).thenReturn(token);
        when(hubspotConfig.getContactUrl()).thenReturn("https://api.hubapi.com/crm/v3/objects/contacts");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Erro inesperado"));


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contactService.createContact(dto);
        });

        assertEquals("Falha na integração com o HubSpot.", exception.getMessage());
    }
}
