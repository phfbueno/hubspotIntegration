package com.github.phfbueno.hubspotintegration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Objeto para criação de um novo contato.")
public record ContactRequestDTO(

        @Schema(description = "Primeiro nome do contato", example = "João")
        @NotBlank(message = "O primeiro nome é obrigatório.")
        String firstName,

        @Schema(description = "Último nome do contato", example = "Silva")
        @NotBlank(message = "O sobrenome é obrigatório.")
        String lastName,

        @Schema(description = "Email do contato", example = "joao.silva@example.com")
        @Email(message = "O email deve ser válido.")
        @NotBlank(message = "O email é obrigatório.")
        String email,

        @Schema(description = "Telefone do contato", example = "+55 11 91234-5678")
        @NotBlank(message = "O telefone é obrigatório.")
        String phone

) {}