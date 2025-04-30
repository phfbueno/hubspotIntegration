package com.github.phfbueno.hubspotintegration.controller;

import com.github.phfbueno.hubspotintegration.dto.ContactRequestDTO;
import com.github.phfbueno.hubspotintegration.dto.ContactResponseDTO;
import com.github.phfbueno.hubspotintegration.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api")
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @Operation(summary = "Criar um novo contato no HubSpot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contato criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/contacts")
    public ResponseEntity<ContactResponseDTO> createContact(@RequestBody ContactRequestDTO contactRequestDTO) {
        log.info("Iniciando criação de contato para o e-mail: {}", contactRequestDTO.email());
        ContactResponseDTO response = contactService.createContact(contactRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}