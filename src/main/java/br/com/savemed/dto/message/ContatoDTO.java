package br.com.savemed.dto.message;
import br.com.savemed.model.enums.CanalType;
import java.time.LocalDateTime;
import java.util.List;

public class ContatoDTO {

    private String identificador;
    private CanalType canal;
    private LocalDateTime ultimaInteracao;
    private int mensagensNaoLidas;
    private List<MensagemChatDTO> historico;

    public ContatoDTO(String identificador, CanalType canal, LocalDateTime ultimaInteracao, int mensagensNaoLidas, List<MensagemChatDTO> historico) {
        this.identificador = identificador;
        this.canal = canal;
        this.ultimaInteracao = ultimaInteracao;
        this.mensagensNaoLidas = mensagensNaoLidas;
        this.historico = historico;
    }

    public String getIdentificador() {
        return identificador;
    }

    public CanalType getCanal() {
        return canal;
    }

    public LocalDateTime getUltimaInteracao() {
        return ultimaInteracao;
    }

    public int getMensagensNaoLidas() {
        return mensagensNaoLidas;
    }

    public List<MensagemChatDTO> getHistorico() {
        return historico;
    }
}
