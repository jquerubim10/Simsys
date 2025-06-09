package br.com.savemed.controllers.ws;

import br.com.savemed.model.enums.CanalType;
import br.com.savemed.model.ws.SendMessageRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import static br.com.savemed.util.CommonUtils.generateNewUniqueId;
import static br.com.savemed.util.MessageSenderHelper.processToken;

@Service
public class WebServiceClient {

    private final RestTemplate restTemplate;

    public WebServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    /*
        // Enviar mensagem via WhatsApp
        public ResponseEntity<Map<String, Object>> sendWhatsapp(SendMessageRequest request) {
            try {
                // Processar o token de autenticação
                String token = processToken(request.getToken());

                // Enviar a mensagem via REST
                ResponseEntity<Map<String, Object>> response = sendMessage(request, token);

                // Retornar a resposta da API
                return response;
            } catch (Exception e) {
                // Tratar erro específico e retornar resposta de falha
                return buildErrorResponse(e);
            }
        }

        // Enviar mensagem via SMS
        public ResponseEntity<Map<String, Object>> sendSms(SendMessageRequest request) {
            int maxRetries = 3; // Máximo de tentativas
            int attempt = 0;

            while (attempt < maxRetries) {
                try {
                    String token = processToken(request.getToken());
                    ResponseEntity<Map<String, Object>> response = sendMessage(request, token);
                    return response;

                } catch (Exception e) {
                    if (isDuplicateIdError(e)) { // Verifica se o erro é de ID duplicado
                        attempt++;
                        request.setId( request.getId()+ "-"+UUID.randomUUID().toString().substring(0, 3) ); // Atualiza o ID
                        if (attempt == maxRetries) {
                            return buildErrorResponse(new RuntimeException("Falha após " + maxRetries + " tentativas"));
                        }
                    } else {
                        return buildErrorResponse(e); // Outros erros
                    }
                }
            }
            return buildErrorResponse(new RuntimeException("Erro inesperado"));
        }
    */
    public ResponseEntity<Map<String, Object>> sendWhatsapp(SendMessageRequest request) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                String token = processToken(request.getToken());
                ResponseEntity<Map<String, Object>> response = sendMessage(request, token);
                return response;
            } catch (Exception e) {
                if (isDuplicateIdError(e)) {
                    attempt++;
                    request.setId(generateNewUniqueId(request.getId()));
                    if (attempt == maxRetries) {
                        return buildErrorResponse(new RuntimeException("Falha após " + maxRetries + " tentativas"));
                    }
                } else {
                    return buildErrorResponse(e);
                }
            }
        }
        return buildErrorResponse(new RuntimeException("Erro inesperado"));
    }
    public ResponseEntity<Map<String, Object>> sendSms(SendMessageRequest request) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                String token = processToken(request.getToken());
                ResponseEntity<Map<String, Object>> response = sendMessage(request, token);
                return response;

            } catch (Exception e) {
                if (isDuplicateIdError(e)) {
                    attempt++;
                    // Gera novo ID mantendo histórico
                    String newId = generateNewUniqueId(request.getId());
                    request.setId(newId);

                    if (attempt == maxRetries) {
                        return buildErrorResponse(new RuntimeException("Falha após " + maxRetries + " tentativas"));
                    }
                } else {
                    return buildErrorResponse(e);
                }
            }
        }
        return buildErrorResponse(new RuntimeException("Erro inesperado"));
    }
    // Verifica se o erro é "duplicate id"
    private boolean isDuplicateIdError(Exception e) {
        return e.getMessage() != null && e.getMessage().contains("duplicate id");
    }

    // Método genérico para enviar mensagens via REST
    private ResponseEntity<Map<String, Object>> sendMessage(SendMessageRequest request, String token) {
        // Cabeçalhos HTTP, incluindo o token de autenticação
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Criar a entidade de requisição (body + cabeçalhos)
        HttpEntity<SendMessageRequest> entity = new HttpEntity<>(request, headers);

        // Fazer a requisição POST com o RestTemplate
        return restTemplate.exchange(
                request.getEnderecoHttp(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
    }

    // Criar uma resposta de erro genérica
    private ResponseEntity<Map<String, Object>> buildErrorResponse(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", "FALHA",
                        "erro", e.getMessage()
                ));
    }
}
