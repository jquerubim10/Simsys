package br.com.savemed.services.message;

import br.com.savemed.controllers.ws.WebServiceClient;
import br.com.savemed.model.message.EntityReference;
import br.com.savemed.model.enums.CanalType;
import br.com.savemed.model.enums.DirecaoType;
import br.com.savemed.model.enums.StatusMensagem;
import br.com.savemed.model.enums.TipoMensagem;
import br.com.savemed.model.message.Mensagem;
import br.com.savemed.model.scheduler.ConfiguracaoNotificacao;
import br.com.savemed.model.ws.SendMessageRequest;
import br.com.savemed.repositories.message.MensagemRepository;
import br.com.savemed.services.scheduler.ConfiguracaoNotificacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static br.com.savemed.util.MessageSenderHelper.applyDefaultFormatting;

@Service
public class GenericMessageSender {

    private final WebServiceClient webServiceClient;
    private final ConfiguracaoNotificacaoService configService;
    private final MensagemRepository mensagemRepository;

    public GenericMessageSender(
            WebServiceClient webServiceClient,
            ConfiguracaoNotificacaoService configService,
            MensagemRepository mensagemRepository
    ) {
        this.webServiceClient = webServiceClient;
        this.configService = configService;
        this.mensagemRepository = mensagemRepository;
    }

    public ResponseEntity<Map<String, Object>> sendGenericMessage(
            SendMessageRequest request,
            CanalType canal,
            EntityReference entidadeRef,
            TipoMensagem tipoMensagem
    ) {
        Mensagem mensagem = null;
        try {
            // 1. Buscar configurações específicas do canal
            ConfiguracaoNotificacao config = configService.findByTipoEnvio(canal);
            // 2. Aplicar configurações ao request
            applyConfigurations(request, config);

            // 3. Validar request
            validateRequest(request);

            // 4. Formatar números de telefone
            applyDefaultFormatting(request, canal);

            // 5. Enviar mensagem via canal específico
            ResponseEntity<Map<String, Object>> response = sendViaChannel(request, canal);
            request.setEntityRef(entidadeRef);
            request.setTipoMensagem(tipoMensagem);
            // 6. Persistir mensagem com status
            mensagem = createAndSaveMessageEntity(request, canal, response);


            Map<String, Object> successResponse = Map.of(
                    "status", "ENVIADO",
                    "mensagemId", mensagem.getId(),
                    "detalhes", response
            );
            // 7. Montar resposta de sucesso
            return ResponseEntity.ok(successResponse); // Status 200

        } catch (Exception e) {
            // 8. Persistir mensagem com erro
            // 9. Montar resposta de erro
            Map<String, Object> errorResponse = Map.of(
                    "status", "FALHA",
                    "mensagemId", request.getId(),
                    "erro", e.getMessage()
            );
            // Cria um ResponseEntity com o corpo do erro
            ResponseEntity<Map<String, Object>> errorResponseEntity =
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

            Mensagem failedMessage = createAndSaveMessageEntity(request, canal, errorResponseEntity);
            failedMessage.setObservacoes("Erro: " + e.getMessage());


            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private void applyConfigurations(SendMessageRequest request, ConfiguracaoNotificacao config) {
        request.setEnderecoHttp(config.getEnderecoHttp())
                .setToken(config.getToken())
                .setInstance(config.getInstancia());
    }

    private void validateRequest(SendMessageRequest request) {
        if (request.getPhoneNumbers() == null || request.getPhoneNumbers().isEmpty()) {
            throw new IllegalArgumentException("Lista de destinatários vazia");
        }
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new IllegalArgumentException("Conteúdo da mensagem não pode ser vazio");
        }
    }



    private ResponseEntity<Map<String, Object>> sendViaChannel(SendMessageRequest request, CanalType canal) {
        return switch (canal) {
            case SMS -> webServiceClient.sendSms(request);
            case WHATSAPP -> webServiceClient.sendWhatsapp(request);
            default -> throw new IllegalArgumentException("Canal não implementado: " + canal);
        };
    }
    private Mensagem createNewMessageEntity(SendMessageRequest request, CanalType canal) {
        Mensagem mensagem = new Mensagem();
        mensagem.setTipoCanal(canal);
        mensagem.setConteudo(request.getMessage());
        mensagem.setDirecao(DirecaoType.ENVIO);
        mensagem.setDataHoraEnvio(LocalDateTime.now());
        mensagem.setNumeroTelefone(request.getPhoneNumbers().getFirst());
        mensagem.setPrioridade(request.getPriority());
        mensagem.setSistemaExternoId(request.getId());

        if (request.getEntityRef() != null) {
            mensagem.setEntidadeType(request.getEntityRef().getType());
            mensagem.setEntidadeId(request.getEntityRef().getId());
        }

        return mensagemRepository.save(mensagem);
    }
    private Mensagem updateExistingMessageEntity(Mensagem mensagemExistente, ResponseEntity<Map<String, Object>> response) {
        if (response != null) {
            mensagemExistente.setDataHoraResposta(LocalDateTime.now());
            mensagemExistente.setProviderResponse(response.getBody() != null ?
                    response.getBody().toString() : "Sem resposta");
            mensagemExistente.setStatus(response.getStatusCode().is2xxSuccessful() ?
                    StatusMensagem.ENVIADO : StatusMensagem.FALHA);
        }
        return mensagemRepository.save(mensagemExistente);
    }
    private Mensagem createAndSaveMessageEntity(SendMessageRequest request, CanalType canal, ResponseEntity<Map<String, Object>> response) {
        // 1. Busca ou cria nova entidade
        Mensagem mensagem = mensagemRepository.findBySistemaExternoId(request.getId())
                .map(existing -> updateExistingMessageEntity(existing, response))
                .orElseGet(() -> createNewMessageEntity(request, canal));

        // 2. Debug
        System.out.println("=== DETALHES DA MENSAGEM ===");
        System.out.println(mensagem);
        System.out.println("============================");

        return mensagem;
    }

/*
    private Mensagem createAndSaveMessageEntity(SendMessageRequest request, CanalType canal, ResponseEntity<Map<String, Object>> response) {

        // Validação para garantir que EntityRef não é nulo
        if (request.getEntityRef() == null) {
            throw new IllegalArgumentException("EntityRef não pode ser nulo.");
        }

        Mensagem mensagem = mensagemRepository.findBySistemaExternoId(request.getId())
                .orElse(new Mensagem()); // Cria nova se não existir
        mensagem.setTipoCanal(canal);
        mensagem.setConteudo(request.getMessage());
        mensagem.setDirecao(DirecaoType.ENVIO);
        mensagem.setDataHoraEnvio(LocalDateTime.now());
        mensagem.setNumeroTelefone(request.getPhoneNumbers().getFirst());
        mensagem.setPrioridade(request.getPriority());
        mensagem.setSistemaExternoId(request.getId());
        mensagem.setEntidadeType(request.getEntityRef().getType());
        mensagem.setEntidadeId(request.getEntityRef().getId());
        mensagem.setTipoMensagem(request.getTipoMensagem());
        if (response != null) {
            mensagem.setDataHoraResposta(LocalDateTime.now());
            mensagem.setProviderResponse(response.getBody() != null ? response.getBody().toString() : "Sem resposta");
            mensagem.setStatus(response.getStatusCode().is2xxSuccessful() ? StatusMensagem.ENVIADO : StatusMensagem.FALHA);
        }
        // Debug: Imprime a mensagem completa
        System.out.println("=== DETALHES DA MENSAGEM ANTES DE SALVAR ===");
        System.out.println(mensagem); // Usa o toString() sobrescrito
        System.out.println("============================================");
        return mensagemRepository.save(mensagem);
    }*/
}
