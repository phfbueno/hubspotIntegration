package com.github.phfbueno.hubspotintegration.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phfbueno.hubspotintegration.config.HubspotConfig;
import com.github.phfbueno.hubspotintegration.exception.OAuthException;
import com.github.phfbueno.hubspotintegration.model.AuthTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class TokenManager {

    private static final Logger log = LoggerFactory.getLogger(TokenManager.class);

    private final HubspotConfig hubspotConfig;

    public TokenManager(HubspotConfig hubspotConfig) {
        this.hubspotConfig = hubspotConfig;
    }

    private  String accessToken;
    private  long expirationTime;
    private String refreshToken;

    private static final long EXPIRATION_TIME_LIMIT = 3600;




    public  String getAccessToken() {

        if (isTokenExpired()) {
            refreshAccessToken();
        }
        return accessToken;
    }

    public void setAccessToken(String token, long expiresIn) {
        accessToken = token;
        expirationTime = System.currentTimeMillis() + (expiresIn * 1000);
    }


    public  boolean isTokenExpired() {
        return System.currentTimeMillis() >= expirationTime;
    }


    private void refreshAccessToken() {
        log.info("Iniciando a troca do refresh token por um novo access token.");


        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", hubspotConfig.getClientId());
        body.add("client_secret", hubspotConfig.getClientSecret());
        body.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(hubspotConfig.getTokenUrl(), HttpMethod.POST, request, String.class);
            log.info("Resposta recebida do HubSpot: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                AuthTokenResponse tokenResponse = objectMapper.readValue(response.getBody(), AuthTokenResponse.class);
                log.info("Novo token de acesso obtido: {}", tokenResponse.getAccessToken());
            } else {
                log.error("Erro ao trocar refresh token por novo access token, status: {}", response.getStatusCode());
                throw new OAuthException("Erro ao trocar refresh token: " + response.getStatusCode(), "INVALID_GRANT");
            }
        } catch (Exception e) {
            log.error("Erro ao processar a requisição para obter o novo token: {}", e.getMessage(), e);
            throw new OAuthException("Erro ao trocar refresh token: " + e.getMessage(), "EXCHANGE_FAILED", e);
        }
    }

}
