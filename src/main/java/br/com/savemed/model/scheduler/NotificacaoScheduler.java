package br.com.savemed.model.scheduler;

import br.com.savemed.dto.message.EntityReference;
import br.com.savemed.model.enums.*;
import br.com.savemed.model.message.Mensagem;
import br.com.savemed.model.ws.SendMessageRequest;
import br.com.savemed.repositories.message.MensagemRepository;
import br.com.savemed.services.message.GenericMessageSender;
import br.com.savemed.services.scheduler.AgendamentoService;
import br.com.savemed.services.scheduler.ConfiguracaoNotificacaoService;
import br.com.savemed.services.scheduler.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import static br.com.savemed.util.CommonUtils.sanitizePhoneNumbers;
import static br.com.savemed.util.MessageSenderHelper.*;

@Component
@EnableScheduling
public class NotificacaoScheduler {
    private final AgendamentoService agendamentoService;
    private final ConfiguracaoNotificacaoService configService;
    private final NotificacaoService notificacaoService;
    private final GenericMessageSender genericMessageSender;
    @Autowired
    private MensagemRepository mensagemRepository;
    private final Logger logger = Logger.getLogger(NotificacaoScheduler.class.getName());

    // Canais a serem processados
    private static final List<CanalType> CANAIS = List.of(CanalType.SMS, CanalType.WHATSAPP);

    public NotificacaoScheduler(AgendamentoService agendamentoService,
                                ConfiguracaoNotificacaoService configService,
                                NotificacaoService notificacaoService,
                                GenericMessageSender genericMessageSender) {
        this.agendamentoService = agendamentoService;
        this.configService = configService;
        this.notificacaoService = notificacaoService;
        this.genericMessageSender = genericMessageSender;
    }

    @Scheduled(fixedRate = 1800000) // A cada 30 minutos
    public void verificarAgendamentosParaNotificacao() {
        CANAIS.forEach(this::processarCanal);
    }

    private void processarCanal(CanalType canal) {
        try {
            ConfiguracaoNotificacao config = configService.findByTipoEnvio(canal);
            agendamentoService.findAgendamentosParaNotificar(canal).forEach(agendamento ->
                    enviarNotificacao(agendamento, config, canal,new EntityReference(EntidadeType.AGENDAMENTO,
                                                                                    agendamento.getId()
                                                                                    ),TipoMensagem.CONFIRMACAO)
            );
        } catch (Exception e) {
            logger.severe("Erro no processamento do canal " + canal + ": " + e.getMessage());
        }
    }

    private void enviarNotificacao(Agendamento agendamento, ConfiguracaoNotificacao config, CanalType canal,EntityReference entidadeRef,TipoMensagem tipoMensagem) {
        Mensagem mensagem = null;
        try {
            SendMessageRequest request = construirRequestAgendamento(agendamento, config, canal);

            // Cria a mensagem ANTES do envio (status PENDENTE)
             //mensagem = createMessageEntity(request, canal,entidadeRef,tipoMensagem);

            // Atualiza a mensagem com resposta
            var response = genericMessageSender.sendGenericMessage(request, canal,entidadeRef,tipoMensagem);

            // Registra notificação baseado no status da resposta
            if (response.getStatusCode().is2xxSuccessful()) {
                assert response.getBody() != null;
                registrarNotificacaoSucesso(agendamento, response.getBody(), canal);
            } else {
                registrarNotificacaoFalha(agendamento, new Exception("Falha no envio"), canal);
            }

        } catch (Exception e) {
            // Atualiza mensagem com erro
            if (mensagem != null) {
                mensagem.setStatus(StatusMensagem.FALHA);
                mensagem.setObservacoes("Erro: " + e.getMessage());
                mensagemRepository.save(mensagem);
            }
            registrarNotificacaoFalha(agendamento, e, canal);
        }
    }
    private Mensagem createMessageEntity(SendMessageRequest request, CanalType canal, EntityReference entidadeRef, TipoMensagem tipoMensagem) {
        Mensagem mensagem = new Mensagem();
        mensagem.setTipoCanal(canal);
        mensagem.setConteudo(request.getMessage());
        mensagem.setDirecao(DirecaoType.ENVIO);
        mensagem.setDataHoraEnvio(LocalDateTime.now());
        mensagem.setStatus(StatusMensagem.PENDENTE); // Status inicial
        mensagem.setNumeroTelefone(request.getPhoneNumbers().getFirst());
        mensagem.setSistemaExternoId(request.getId());
        mensagem.setEntidadeRef(entidadeRef);
        mensagem.setTipoMensagem(tipoMensagem);
        mensagem.setStatus(StatusMensagem.PENDENTE);
        // Salva no banco
        return mensagemRepository.save(mensagem);
    }

    private SendMessageRequest construirRequestAgendamento(Agendamento agendamento, ConfiguracaoNotificacao config, CanalType canal) {
        // Validação do contato
        String contato = agendamento.getContato1();
        if (contato == null || contato.isBlank()) {
            throw new IllegalArgumentException("Contato do agendamento não pode ser nulo ou vazio.");
        }

        // Sanitiza números
        List<String> numerosSanitizados = sanitizePhoneNumbers(List.of(contato));
        if (numerosSanitizados.isEmpty()) {
            throw new IllegalArgumentException("Nenhum número válido após sanitização.");
        }

        // Usando o Builder do Lombok
        return SendMessageRequest.builder()
                .id(generateIdMensagem(new EntityReference(EntidadeType.AGENDAMENTO, agendamento.getId()), canal))
                .phoneNumbers(numerosSanitizados)
                .message(construirMensagem(
                        config,
                        extrairPrimeiroNome(agendamento.getPaciente()),
                        formatarDataAgendamento(agendamento.getHoraInicio()),
                        agendamento
                ))
                .enderecoHttp(config.getEnderecoHttp())
                .instance(config.getInstancia())
                .token(config.getToken())
                .priority(1)
                .entityRef(new EntityReference(EntidadeType.AGENDAMENTO, agendamento.getId()))
                .simNumber(config.getChip())
                .isEncrypted(false)
                .withDeliveryReport(true)
                .build();
    }

    private void registrarNotificacaoSucesso(Agendamento agendamento, Map<String, Object> resposta, CanalType canal) {
        notificacaoService.create(Notificacao.builder()
                .agendamento(agendamento)
                .meioEnvio(Notificacao.MeioEnvio.valueOf(canal.name()))
                .statusEnvio(Notificacao.StatusEnvio.ENVIADO)
                .resposta(resposta.toString())
                .tentativas(1)
                .build());
    }

    private void registrarNotificacaoFalha(Agendamento agendamento, Exception e, CanalType canal) {
        notificacaoService.create(Notificacao.builder()
                .agendamento(agendamento)
                .meioEnvio(Notificacao.MeioEnvio.valueOf(canal.name()))
                .statusEnvio(Notificacao.StatusEnvio.FALHA)
                .resposta(e.getMessage())
                .tentativas(1)
                .build());
    }
}