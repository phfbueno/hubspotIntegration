# Integração com HubSpot - API REST

## Objetivo

Este projeto tem como objetivo desenvolver uma API REST em Java para integrar com a API do HubSpot utilizando o fluxo OAuth 2.0 (Authorization Code Flow). A API implementa os seguintes endpoints:

1. **Geração da Authorization URL** - Endpoint que gera a URL de autorização para iniciar o fluxo OAuth com o HubSpot.
2. **Processamento do Callback OAuth** - Endpoint que recebe o código de autorização do HubSpot e realiza a troca pelo token de acesso.
3. **Criação de Contatos** - Endpoint que cria um contato no CRM do HubSpot através da API.
4. **Recebimento de Webhook para Criação de Contatos** - Endpoint que escuta e processa eventos do tipo "contact.creation" enviados pelo webhook do HubSpot.

## Tecnologias Utilizadas

- **Java 21** (ou versão superior)
- **Spring Boot** (framework para o desenvolvimento da API REST)
- **OAuth 2.0** (para autenticação com a API do HubSpot)
- **Maven** (gerenciador de dependências e construção do projeto)
- **Logback** (para logging)
- **Spring Security** (para garantir a segurança da aplicação)

## Requisitos

1. **Conta de Desenvolvedor no HubSpot**:
   Para utilizar a API do HubSpot, você precisará de uma conta de desenvolvedor. Você pode criar uma conta [aqui](https://app.hubspot.com/signup).
   
2. **Configuração de OAuth 2.0**:
   O projeto faz uso do fluxo de autorização **Authorization Code Flow**. Para isso, você precisará criar uma aplicação no painel de desenvolvedor do HubSpot e configurar as credenciais (client_id, client_secret).

## Instruções para Execução

### 1. Clone o Repositório

Clone este repositório em sua máquina local:

```bash
git clone [<hubspotIntegration>](https://github.com/phfbueno/hubspotIntegration.git)
cd <hubspotIntegration>
```
### 2. **Processamento do Callback OAuth**
   - **Método**: `GET`
   - **Endpoint**: `/oauth/callback`
   - **Descrição**: Recebe o código de autorização do HubSpot e realiza a troca pelo token de acesso.
   
   **Exemplo de requisição**:

   ```bash
    GET http://localhost:8080/oauth/callback?code=<authorization_code>
```

### 3. **Criação de Contatos**
   - **Método**: `POST`
   - **Endpoint**: `/api/contacts`
   - **Descrição**: Cria um contato no HubSpot através da API. O endpoint deve respeitar as políticas de rate limit definidas pela API do HubSpot.
   
   **Corpo da requisição**:

   ```json
   {
     "firstName": "John",
     "lastName": "Doe",
     "email": "john.doe@example.com"
   }
```
### 4. **Recebimento de Webhook para Criação de Contatos**
   - **Método**: `POST`
   - **Endpoint**: `/webhook`
   - **Descrição**: Endpoint que escuta e processa notificações de criação de contatos enviadas pelo HubSpot via webhook. O tipo de evento esperado é "contact.creation".
   
   **Corpo da requisição**:

   ```json
   {
     "event": "contact.creation",
     "contactId": "<contact_id>"
   }
  ```

## Para visualizar todas as informações que podem ser enviadas para criação do contato, consulte a documentação da API disponível no Swagger `http://localhost:8080/swagger-ui/index.html#/`.

## Testes Unitários e Integração
O projeto conta com uma cobertura de testes unitários e de integração .

#### Execução:
```bash
   ./mvnw test
```
