package com.github.phfbueno.hubspotintegration.controller;


import com.github.phfbueno.hubspotintegration.exception.OAuthException;
import com.github.phfbueno.hubspotintegration.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/hubspot")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/auth-url")
    @Operation(
            summary = "Gera a URL de autorização OAuth para o HubSpot",
            description = "Constrói e retorna a URL que inicia o fluxo OAuth 2.0 (Authorization Code Flow)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL gerada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<String> getAuthorizationUrl() {
        try {
            log.info("Solicitação recebida para gerar URL de autorização.");

            String authUrl = authService.generateAuthorizationUrl();
            return ResponseEntity.ok(authUrl);

        } catch (Exception e) {
            log.error("Erro ao gerar URL de autorização: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao gerar a URL de autorização: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Trocar código de autorização por um token de acesso",
            description = "Este endpoint recebe o código de autorização e troca por um token de acesso para interagir com a API do HubSpot."
    )
    @GetMapping("/callback")
    public ResponseEntity<String> handleOAuthCallback(@RequestParam("code") String code) {
        log.info("Iniciando processo de troca de código por token. Código recebido: {}", code);
        try {
            String accessToken = authService.exchangeCodeForToken(code);
            log.info("Token de acesso recebido: {}", accessToken);
            return ResponseEntity.ok("Access token recebido: " + accessToken);
        } catch (OAuthException e) {
            log.error("Erro ao obter o token de acesso: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao obter token: " + e.getMessage());
        }
    }


    @ExceptionHandler(OAuthException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleOAuthException(OAuthException e) {
        log.error("Erro OAuthException: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Erro: " + e.getMessage());
    }
}
