package br.com.savemed.model.ws;

import br.com.savemed.dto.message.EntityReference;
import br.com.savemed.model.enums.EntidadeType;
import br.com.savemed.model.enums.TipoMensagem;
import jakarta.persistence.Embedded;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true) // Habilita encadeamento de setters
public class SendMessageRequest {

    // Identificador único da mensagem (gerado pelo sistema externo ou UUID)
    private String id;

    // Indica se a mensagem é criptografada
    private Boolean isEncrypted;

    // Conteúdo da mensagem
    private String message;

    // Lista de números de telefone/emails/destinatários
    private List<String> phoneNumbers;

    // Prioridade de envio (1-5)
    private Integer priority;

    // Número do SIM card (para gateways físicos)
    private Integer simNumber;

    // Tempo de vida da mensagem (em segundos)
    private Integer ttl;

    // Data/hora de validade da mensagem
    private LocalDateTime validUntil;

    // Se deve gerar relatório de entrega
    private Boolean withDeliveryReport;

    // Configurações de integração (transientes, não persistidas)
    private transient String enderecoHttp; // Endpoint do serviço
    private transient String instance;     // Instância do provedor
    private transient String token;        // Token de autenticação

    // Referência à entidade relacionada (ex: Agendamento ID 456)
    private EntityReference entityRef;

    // Tipo da mensagem (CONFIRMACAO, LEMBRETE, etc.)
    private TipoMensagem tipoMensagem;

    public SendMessageRequest linkToEntity(EntidadeType type, Long id) {
        this.entityRef = new EntityReference(type, id);
        return this;
    }
    public SendMessageRequest setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
        return this;
    }

    public SendMessageRequest setIsEncrypted(boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
        return this;
    }

    public SendMessageRequest setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public SendMessageRequest setSimNumber(Integer simNumber) {
        this.simNumber = simNumber;
        return this;
    }

    public SendMessageRequest setWithDeliveryReport(boolean withDeliveryReport) {
        this.withDeliveryReport = withDeliveryReport;
        return this;
    }

    public SendMessageRequest setMessage(String message) {
        this.message = message;
        return this;
    }

    public SendMessageRequest setEnderecoHttp(String enderecoHttp) {
        this.enderecoHttp = enderecoHttp;
        return this;
    }

    public SendMessageRequest setInstance(String instance) {
        this.instance = instance;
        return this;
    }
    public SendMessageRequest setToken(String token) {
        this.token = token;
        return this;
    }
}