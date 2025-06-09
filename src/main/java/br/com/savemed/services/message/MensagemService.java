package br.com.savemed.services.message;

import br.com.savemed.dto.message.ContatoDTO;
import br.com.savemed.dto.message.EntityReference;
import br.com.savemed.dto.message.MensagemRequestDTO;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.enums.*;
import br.com.savemed.model.message.Mensagem;
import br.com.savemed.model.scheduler.Agendamento;
import br.com.savemed.model.ws.SendMessageRequest;
import br.com.savemed.repositories.message.MensagemRepository;
import br.com.savemed.repositories.scheduler.AgendamentoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.savemed.dto.message.MensagemChatDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.savemed.util.CommonUtils.sanitizePhoneNumbers;

@Service
@Transactional
public class MensagemService {

    private final MensagemRepository mensagemRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final GenericMessageSender genericMessageSender;


    public List<ContatoDTO> listarContatosComHistorico(CanalType canal, Sort sort) {
        return mensagemRepository.findContatosAgrupados(canal, sort)
                .stream()
                .map(identificador -> {
                    List<Mensagem> mensagens = mensagemRepository.findByDestinatario(identificador, canal, sort);
                    return mapToContatoDTO(identificador, mensagens);
                })
                .collect(Collectors.toList());
    }

    public ContatoDTO buscarHistoricoContato(String identificador, CanalType canal, Sort sort) {
        List<Mensagem> mensagens = mensagemRepository.findByDestinatario(identificador, canal, sort);
        return mapToContatoDTO(identificador, mensagens);
    }

    private ContatoDTO mapToContatoDTO(String identificador, List<Mensagem> mensagens) {
        return new ContatoDTO(
                identificador,
                mensagens.get(0).getTipoCanal(), // Canal do primeiro registro
                mensagens.stream()
                        .map(Mensagem::getDataHoraEnvio)
                        .max(LocalDateTime::compareTo)
                        .orElse(null),
                (int) mensagens.stream()
                        .filter(m -> m.getStatus() == StatusMensagem.ENVIADO)
                        .count(),
                mensagens.stream()
                        .map(MensagemChatDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    public List<Mensagem> getHistoricoMensagens(EntidadeType tipo, Long id) {
        return mensagemRepository.findByEntidadeRefTypeAndEntidadeRefId(tipo, id);
    }

    public Mensagem criarMensagemEEnviar(MensagemRequestDTO dto) {
        // Cria a mensagem inicial
        Mensagem mensagem = criarMensagem(dto);

        // Dispara o envio assíncrono
        processarMensagemAssincrono(mensagem.getId());

        return mensagem;
    }

    public Mensagem criarMensagem(@Valid @RequestBody MensagemRequestDTO dto) {
        validarDTO(dto);

        Mensagem mensagem = new Mensagem();

        // Campos obrigatórios
        mensagem.setTipoCanal(dto.getCanal());
        mensagem.setConteudo(dto.getConteudo());
        mensagem.setDirecao(DirecaoType.ENVIO);
        mensagem.setDataHoraEnvio(LocalDateTime.now());
        mensagem.setStatus(StatusMensagem.PENDENTE); // Status inicial
        mensagem.setTipoMensagem(dto.getTipoMensagem());
        mensagem.setPrioridade(dto.getPrioridade());
        // Destinatário baseado no canal
        switch (dto.getCanal()) {
            case WHATSAPP, SMS -> mensagem.setNumeroTelefone(dto.getDestinatario());
            case EMAIL -> mensagem.setEmail(dto.getDestinatario());
            case SISTEMA -> mensagem.setSistemaExternoId(dto.getDestinatario());
            default -> throw new IllegalArgumentException("Canal inválido: " + dto.getCanal());
        }

        // Entidade relacionada (obrigatória)
        if (dto.getEntidadeRef() != null) {
            mensagem.setEntidadeType(dto.getEntidadeRef().getType());
            mensagem.setEntidadeId(dto.getEntidadeRef().getId());
        } else {
            throw new IllegalArgumentException("EntityReference é obrigatório");
        }

        return mensagemRepository.save(mensagem);
    }

    private void validarDTO(MensagemRequestDTO dto) {
        if (dto.getCanal() == null) {
            throw new IllegalArgumentException("Canal é obrigatório");
        }
        if (dto.getEntidadeRef() == null) {
            throw new IllegalArgumentException("EntityReference é obrigatório");
        }
    }

    public Mensagem consultarMensagem(Long id) {
        return mensagemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mensagem não encontrada com ID: " + id));
    }
    @Async
    public void processarMensagemAssincrono(Long mensagemId) {
        Mensagem mensagem = mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> new ResourceNotFoundException("Mensagem não encontrada"));

        try {
            // Converte Mensagem para SendMessageRequest
            SendMessageRequest request = mapToSendMessageRequest(mensagem);

            // Envia via sender genérico
            genericMessageSender.sendGenericMessage(
                    request,
                    mensagem.getTipoCanal(),
                    mensagem.getEntidadeRef(),
                    mensagem.getTipoMensagem()
            );

            // Atualiza status para ENVIADO
            mensagem.setStatus(StatusMensagem.ENVIADO);

        } catch (Exception e) {
            // Atualiza status para FALHA e registra erro
            mensagem.setStatus(StatusMensagem.FALHA);
            mensagem.setObservacoes("Erro: " + e.getMessage());
        } finally {
            mensagemRepository.save(mensagem);
        }
    }
    private SendMessageRequest mapToSendMessageRequest(Mensagem mensagem) {
        return SendMessageRequest.builder()
                .id(mensagem.getSistemaExternoId())
                .message(mensagem.getConteudo())
                .phoneNumbers(List.of(mensagem.getNumeroTelefone()))
                .priority(mensagem.getPrioridade())
                .tipoMensagem(mensagem.getTipoMensagem())
                .entityRef(new EntityReference(
                        mensagem.getEntidadeRef().getType(),
                        mensagem.getEntidadeRef().getId()
                ))
                .build();
    }
    @Autowired
    public MensagemService(MensagemRepository repository, AgendamentoRepository agendamentoRepository, GenericMessageSender genericMessageSender) {
        this.mensagemRepository = repository;
        this.agendamentoRepository = agendamentoRepository;
        this.genericMessageSender = genericMessageSender;
    }

    @Transactional
    public Mensagem registrarMensagem(SendMessageRequest request, Agendamento agendamento, String status) {
        Mensagem mensagem = new Mensagem();

        // Mapeamento dos dados
        mensagem.setEntidadeType(EntidadeType.AGENDAMENTO);
        mensagem.setEntidadeId(agendamento.getId());
        mensagem.setTipoCanal(mapperTipoCanal(request));
        mensagem.setTipoMensagem(TipoMensagem.LEMBRETE);
        mensagem.setConteudo(request.getMessage());
        mensagem.setDirecao(DirecaoType.ENVIO);
        mensagem.setDataHoraEnvio(LocalDateTime.now());
        mensagem.setStatus(mapperStatus(status));
        mensagem.setNumeroTelefone(request.getPhoneNumbers().get(0));
        mensagem.setPrioridade(request.getPriority());

        if(request.getEntityRef() != null) {
            mensagem.setEntidadeRef(new EntityReference(
                    request.getEntityRef().getType(),
                    request.getEntityRef().getId()
            ));
        }

        // if(e != null) {
        //      mensagem.setObservacoes("Erro: " + e.getMessage());
        //}
        // Relacionamento com agendamento
        //Agendamento agendamentoRef = agendamentoRepository.getReferenceById(agendamento.getId());
        //mensagem.setAgendamento(agendamentoRef);

        return mensagemRepository.save(mensagem);
    }

    private CanalType mapperTipoCanal(SendMessageRequest request) {
        return request.getEnderecoHttp().contains("whatsapp") ? CanalType.WHATSAPP : CanalType.SMS;
    }

    private StatusMensagem mapperStatus(String statusExterno) {
        return statusExterno.contains("SUCESSO") ? StatusMensagem.ENVIADO : StatusMensagem.FALHA;
    }

    @Transactional
    public Mensagem createMensagem(Mensagem mensagem) {
        mensagem.setDataHoraEnvio(LocalDateTime.now());
        return mensagemRepository.save(mensagem);
    }

    @Transactional
    public Mensagem updateStatus(Long id, StatusMensagem status) {
        Mensagem mensagem = mensagemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mensagem não encontrada"));
        mensagem.setStatus(status);
        return mensagemRepository.save(mensagem);
    }

    public Page<Mensagem> findByEntidade(
            EntidadeType entidadeType,
            Long entidadeId,
            Pageable pageable
    ) {
        return mensagemRepository.findByEntidadeTypeAndEntidadeId(
                entidadeType,
                entidadeId,
                pageable
        );
    }

    public Page<Mensagem> findConversa(Long mensagemPaiId, Pageable pageable) {
        return mensagemRepository.findByMensagemPaiId(mensagemPaiId, pageable);
    }

    @Transactional
    public void excluirMensagem(Long id) {
        Mensagem mensagem = consultarMensagem(id);
        mensagemRepository.delete(mensagem);
    }

    @Transactional
    public Map<String, Object> registerMessageResult(
            SendMessageRequest request,
            ResponseEntity<Map<String, Object>> response,
            CanalType canal
    ) {
        Mensagem mensagem = createMessageEntity(request, canal, response.getStatusCode().is2xxSuccessful());
        return Map.of(
                "status", "ENVIADO",
                "mensagemId", mensagem.getId(),
                "providerResponse", response.getBody()
        );
    }

    public Map<String, Object> registerFailedMessage(
            SendMessageRequest request,
            CanalType canal,
            Exception e
    ) {
        Mensagem mensagem = createMessageEntity(request, canal, false);
        mensagem.setObservacoes("Erro: " + e.getMessage());
        return Map.of(
                "status", "FALHA",
                "mensagemId", mensagem.getId(),
                "erro", e.getMessage()
        );
    }

    private Mensagem createMessageEntity(SendMessageRequest request, CanalType canal, boolean success) {
        Mensagem mensagem = new Mensagem();

        // 1. Mapeamento obrigatório
        mensagem.setTipoCanal(canal);
        mensagem.setConteudo(request.getMessage());
        mensagem.setDirecao(DirecaoType.ENVIO);
        mensagem.setDataHoraEnvio(LocalDateTime.now());
        mensagem.setStatus(success ? StatusMensagem.ENVIADO : StatusMensagem.FALHA);

        // 2. Destinatário (com null safety)
        if (request.getPhoneNumbers() != null && !request.getPhoneNumbers().isEmpty()) {
            mensagem.setNumeroTelefone(sanitizePhoneNumbers(request.getPhoneNumbers()).getFirst() );
        }

        // 3. Metadados do request
        mensagem.setPrioridade(request.getPriority());
        mensagem.setSistemaExternoId(request.getId());

        // 4. Relação com entidade (usando EntityReference)
        if (request.getEntityRef() != null) {
            mensagem.setEntidadeType(request.getEntityRef().getType());
            mensagem.setEntidadeId(request.getEntityRef().getId());
        }

        // 5. Observações padrão
        mensagem.setObservacoes("Envio via " + canal + " em " + LocalDateTime.now());

        return mensagemRepository.save(mensagem);
    }

}