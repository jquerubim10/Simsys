package br.com.savemed.controllers.message;

import br.com.savemed.model.dto.message.ContatoDTO;
import br.com.savemed.model.dto.message.MensagemChatDTO;
import br.com.savemed.model.dto.message.MensagemRequestDTO;
import br.com.savemed.exceptions.ResourceNotFoundException;
import br.com.savemed.model.enums.CanalType;
import br.com.savemed.model.enums.EntidadeType;
import br.com.savemed.model.message.Mensagem;
import br.com.savemed.services.message.GenericMessageSender;
import br.com.savemed.services.message.MensagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mensagens")
@Tag(name = "Mensagens", description = "Endpoints para Gerenciamento de Mensagens")
public class MensagemController {

    private final MensagemService mensagemService;
    private GenericMessageSender genericMessageSender;
    private static final Logger logger = LoggerFactory.getLogger(MensagemController.class);
    public MensagemController(MensagemService service) {
        this.mensagemService = service;
    }

    @Operation(summary = "Lista todos os contatos com histórico de comunicação")
    @GetMapping("/contatos")
    public ResponseEntity<List<ContatoDTO>> listarContatosComHistorico(
            @RequestParam(required = false) CanalType canal,
            @RequestParam(defaultValue = "DESC") String ordemCronologica
    ) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(ordemCronologica), "dataHoraEnvio");
            List<ContatoDTO> contatos = mensagemService.listarContatosComHistorico(canal, sort);
            return ResponseEntity.ok(contatos);
        } catch (Exception e) {
            logger.error("Erro ao listar contatos: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Busca histórico de um contato específico")
    @GetMapping("/contatos/{identificador}")
    public ResponseEntity<ContatoDTO> buscarHistoricoContato(
            @PathVariable String identificador,
            @RequestParam(required = false) CanalType canal,
            @RequestParam(defaultValue = "DESC") String ordemCronologica
    ) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(ordemCronologica), "dataHoraEnvio");
            ContatoDTO contato = mensagemService.buscarHistoricoContato(identificador, canal, sort);
            return ResponseEntity.ok(contato);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Erro ao buscar contato: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Lista mensagens por entidade (paginado e ordenado)")
    @GetMapping("/entidade/{entidadeType}/{entidadeId}")
    public ResponseEntity<Page<MensagemChatDTO>> listByEntidade(
            @PathVariable String entidadeType,
            @PathVariable Long entidadeId,
            @PageableDefault(sort = "dataHoraEnvio", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        try {
            EntidadeType tipo = EntidadeType.valueOf(entidadeType.toUpperCase());
            Page<Mensagem> mensagens = mensagemService.findByEntidade(tipo, entidadeId, pageable);
            Page<MensagemChatDTO> response = mensagens.map(MensagemChatDTO::fromEntity);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtém a conversa completa ordenada cronologicamente")
    @GetMapping("/conversa/{mensagemPaiId}")
    public ResponseEntity<Page<MensagemChatDTO>> getConversa(
            @PathVariable Long mensagemPaiId,
            @PageableDefault(sort = "dataHoraEnvio", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Mensagem> mensagens = mensagemService.findConversa(mensagemPaiId, pageable);
        return ResponseEntity.ok(mensagens.map(MensagemChatDTO::fromEntity));
    }

    @Operation(summary = "Cria uma nova mensagem a partir de dados brutos (MensagemRequestDTO)")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mensagem> criarMensagem(
            @Valid @RequestBody MensagemRequestDTO dto
    ) {
        try {
            Mensagem mensagem = mensagemService.criarMensagem(dto);
            mensagemService.processarMensagemAssincrono(mensagem.getId());
            // Log de sucesso
            logger.info("Mensagem criada: canal={}, destinatario={}, id={}",
                    mensagem.getTipoCanal(),
                    mensagem.getNumeroTelefone(),
                    mensagem.getId()
            );
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(mensagem);
        } catch (Exception e) {
            logger.error("Erro ao criar mensagem: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    @PostMapping
    public ResponseEntity<Mensagem> criarEEnviarMensagem(
            @Valid @RequestBody MensagemRequestDTO dto
    ) {
        Mensagem mensagem = mensagemService.criarMensagemEEnviar(dto);
        return ResponseEntity.accepted().body(mensagem); // HTTP 202 Accepted
    }

    @Operation(summary = "Consulta uma mensagem por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> consultarMensagem(@PathVariable Long id) {
        try {
            Mensagem mensagem = mensagemService.consultarMensagem(id);
            return ResponseEntity.ok(mensagem);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("erro", ex.getMessage()));
        }
    }

    @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso")
    @ApiResponse(responseCode = "400", description = "Tipo de entidade inválido")
    @ApiResponse(responseCode = "500", description = "Erro interno")
    @Operation(summary = "Consulta histórico de mensagens por tipo de entidade e ID (paginado)")
    @GetMapping("/historico/{tipoEntidade}/{entidadeId}")
    public ResponseEntity<Page<MensagemChatDTO>> getHistorico(
            @PathVariable String tipoEntidade,
            @PathVariable Long entidadeId,
            @PageableDefault(sort = "dataHoraEnvio", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        try {
            EntidadeType entidadeType = EntidadeType.valueOf(tipoEntidade.toUpperCase());
            Page<Mensagem> mensagens = mensagemService.findByEntidade(entidadeType, entidadeId, pageable);
            Page<MensagemChatDTO> response = mensagens.map(MensagemChatDTO::fromEntity);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Page.empty());
        }
    }

    @Operation(summary = "Exclui uma mensagem por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> excluirMensagem(@PathVariable Long id) {
        try {
            mensagemService.excluirMensagem(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException ex) {
            // Idempotência: Retorna 204 mesmo se o recurso não existir
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao excluir a mensagem"));
        }
    }


}