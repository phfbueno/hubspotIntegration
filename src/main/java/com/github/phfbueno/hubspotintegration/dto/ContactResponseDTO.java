package com.github.phfbueno.hubspotintegration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta após criação do contato")
public record ContactResponseDTO(

        @Schema(description = "ID do contato no HubSpot", example = "123456789")
        String contactId,

        @Schema(description = "Primeiro nome do contato", example = "João")
        String firstName,

        @Schema(description = "Último nome do contato", example = "Silva")
        String lastName,

        @Schema(description = "Email do contato", example = "joao.silva@example.com")
        String email,

        @Schema(description = "Telefone do contato", example = "+55 11 91234-5678")
        String phone

) {}