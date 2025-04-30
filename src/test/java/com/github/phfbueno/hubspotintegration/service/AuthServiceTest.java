package com.github.phfbueno.hubspotintegration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phfbueno.hubspotintegration.auth.TokenManager;
import com.github.phfbueno.hubspotintegration.config.HubspotConfig;
import com.github.phfbueno.hubspotintegration.exception.OAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private HubspotConfig hubspotConfig;

    @Mock
    private TokenManager tokenManager;

    @InjectMocks
    private AuthService authService;

    @Mock
    private RestTemplate restTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        when(hubspotConfig.getClientId()).thenReturn("test-client-id");
        when(hubspotConfig.getClientSecret()).thenReturn("test-secret");
        when(hubspotConfig.getRedirectUri()).thenReturn("http://localhost/callback");
        when(hubspotConfig.getTokenUrl()).thenReturn("https://api.hubapi.com/oauth/v1/token");
    }

    @Test
    void deveTrocarCodigoPorTokenComSucesso() throws Exception {


        String code = "auth-code";
        String expectedToken = "access-token-123";
        String fakeResponseJson = """
            {
              "access_token": "%s",
              "expires_in": 3600,
              "refresh_token": "refresh-token"
            }
        """.formatted(expectedToken);

        ResponseEntity<String> response = new ResponseEntity<>(fakeResponseJson, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://api.hubapi.com/oauth/v1/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(response);


        String token = authService.exchangeCodeForToken(code);


        assertEquals(expectedToken, token);
        verify(tokenManager).setAccessToken(expectedToken, 3600);
    }

    @Test
    void deveLancarOAuthExceptionQuandoStatusNaoFor200() {

        String code = "invalid-code";
        ResponseEntity<String> response = new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                eq("https://api.hubapi.com/oauth/v1/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(response);


        OAuthException exception = assertThrows(OAuthException.class, () -> {
            authService.exchangeCodeForToken(code);
        });

        assertTrue(exception.getMessage().contains("Erro ao trocar código por token"));
        verify(tokenManager, never()).setAccessToken(any(), anyInt());
    }
}
