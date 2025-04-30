package com.github.phfbueno.hubspotintegration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phfbueno.hubspotintegration.auth.TokenManager;
import com.github.phfbueno.hubspotintegration.config.HubspotConfig;

import com.github.phfbueno.hubspotintegration.exception.OAuthException;
import com.github.phfbueno.hubspotintegration.model.AuthTokenResponse;
import org.springframework.stereotype.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final HubspotConfig hubspotConfig;
    private final TokenManager tokenManager;

    public AuthService(HubspotConfig hubspotConfig, TokenManager tokenManager) {
        this.hubspotConfig = hubspotConfig;
        this.tokenManager = tokenManager;
    }

    public String generateAuthorizationUrl() {
        log.info("Gerando URL de autorização com client_id, redirect_uri, e scopes.");


        String authUrl = hubspotConfig.getAuthUrl() +
                "?client_id=" + hubspotConfig.getClientId() +
                "&redirect_uri=" + hubspotConfig.getRedirectUri() +
                "&scope=" +hubspotConfig.getScopes() +
                "&response_type=code";

        log.info("Generated Authorization URL: {}", authUrl);

        return authUrl;
    }


    public String exchangeCodeForToken(String code) {
        log.info("Iniciando troca do código de autorização por token para o código: {}", code);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", hubspotConfig.getClientId());
        body.add("client_secret", hubspotConfig.getClientSecret());
        body.add("redirect_uri", hubspotConfig.getRedirectUri());
        body.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(hubspotConfig.getTokenUrl(), HttpMethod.POST, request, String.class);
            log.info("Resposta recebida do HubSpot: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                AuthTokenResponse tokenResponse = objectMapper.readValue(response.getBody(), AuthTokenResponse.class);
                log.info("Token de acesso obtido: {}", tokenResponse.getAccessToken());

                tokenManager.setAccessToken(tokenResponse.getAccessToken(), tokenResponse.getExpiresIn());

                return tokenResponse.getAccessToken();
            } else {
                log.error("Erro ao trocar código por token, status: {}", response.getStatusCode());
                throw new OAuthException("Erro ao trocar código por token: " + response.getStatusCode(), "INVALID_GRANT");
            }
        } catch (Exception e) {
            log.error("Erro ao processar a requisição para obter o token: {}", e.getMessage(), e);
            throw new OAuthException("Erro ao trocar código por token: " + e.getMessage(), "EXCHANGE_FAILED", e);
        }
    }
}

