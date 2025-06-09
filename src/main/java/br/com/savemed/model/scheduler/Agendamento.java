package br.com.savemed.model.scheduler;

import br.com.savemed.model.enums.EntidadeType;
import br.com.savemed.model.message.Mensagem;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "Agendamento")
public class Agendamento implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "AgendamentoID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MedicoID")
    private Integer medicoID;

    @Column(name = "CentroCustoID")
    private Integer centroCustoID;

    @Column(name = "ConvenioID")
    private Integer convenioID;

    @Column(name = "SecretariaID")
    private Integer secretariaID;

    @Column(name = "Ficha")
    private Integer ficha;

    @Column(name = "TipoAgendamento")
    private String tipoAgendamento;

    @Column(name = "Title")
    private String title;

    @Column(name = "Color")
    private String color;

    @Column(name = "Actions")
    private String actions;

    @Column(name = "CssClass")
    private String cssClass;

    @Column(name = "Meta")
    private String meta;

    @Column(name = "Paciente")
    private String paciente;

    @Column(name = "Contato1")
    private String contato1;

    @Column(name = "Contato2")
    private String contato2;

    @Column(name = "Observacoes")
    private String observacoes;

    @Column(name = "Status")
    private String status;

    @Column(name = "whatsapp", columnDefinition = "boolean default false")
    private boolean whatsapp;

    @Column(name = "ENCAIXEHORARIO", columnDefinition = "boolean default false")
    private boolean encaixeHorario;

    @Column(name = "Draggable", columnDefinition = "boolean default false")
    private boolean draggable;

    @Column(name = "ResizableBeforeStart", columnDefinition = "boolean default false")
    private boolean resizableBeforeStart;

    @Column(name = "ResizableAfterEnd", columnDefinition = "boolean default false")
    private boolean resizableAfterEnd;

    @Column(name = "AllDay", columnDefinition = "boolean default false")
    private boolean allDay;

    @Column(name = "Nascimento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nascimento;

    @Column(name = "HoraInicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date horaInicio;

    @Column(name = "HoraTermino")
    @Temporal(TemporalType.TIMESTAMP)
    private Date horaTermino;

    @Transient
    private List<Mensagem> mensagens;

    @Transient
    public List<Mensagem> getMensagensRelacionadas(List<Mensagem> todasMensagens) {
        return todasMensagens.stream()
                .filter(m -> m.getEntidadeType() == EntidadeType.AGENDAMENTO &&
                        m.getEntidadeId() != null &&
                        m.getEntidadeId().equals(this.getId()))
                .collect(Collectors.toList());
    }
}

//CREATE TABLE Agendamento (
//      AgendamentoID INT PRIMARY KEY IDENTITY,         -- Identificador �nico do agendamento
//      TipoAgendamento NVARCHAR(50) NOT NULL DEFAULT 'Consulta', -- Consulta, Exame, Cirurgia
//      HoraInicio DATETIME NOT NULL,                        -- Data e hora de in�cio
//      HoraTermino DATETIME NOT NULL,                          -- Data e hora de t�rmino
//      Title NVARCHAR(255) NOT NULL,                   -- T�tulo ou descri��o do evento
//      Color NVARCHAR(50) NULL,                        -- Cor para representa��o no calend�rio
//      Actions NVARCHAR(MAX) NULL,                     -- A��es associadas (em formato JSON)
//      AllDay BIT DEFAULT 0,                           -- Indica se o evento dura o dia inteiro
//      CssClass NVARCHAR(255) NULL,                    -- Classe CSS personalizada para o evento
//      ResizableBeforeStart BIT DEFAULT 0,             -- Permite redimensionar antes do in�cio
//      ResizableAfterEnd BIT DEFAULT 0,                -- Permite redimensionar ap�s o t�rmino
//      Draggable BIT DEFAULT 0,                        -- Indica se o evento pode ser arrastado
//      Meta NVARCHAR(MAX) NULL,                        -- Informa��es adicionais (em formato JSON)
//MedicoID smallint NULL FOREIGN KEY REFERENCES Medicos(Medico), -- M�dico respons�vel (null para exames autom�ticos)
//CentroCustoID smallint NOT NULL FOREIGN KEY REFERENCES CentroCusto(CentroCusto), -- Centro de custo ou setor
//SecretariaID INT NOT NULL FOREIGN KEY REFERENCES Usuario(Usuario), -- Secret�ria que criou o agendamento
//Paciente NVARCHAR(800) NOT NULL,                -- Nome do paciente
//Ficha INT NULL FOREIGN KEY REFERENCES Fichas(Ficha),	-- Identifica��o da ficha do paciente
//Contato1 NVARCHAR(50) NOT NULL,                -- Contato telefonico
//Contato2 NVARCHAR(50) NOT NULL,                -- Contato telefonico
//whatsapp BIT DEFAULT 0,               -- � whatsapp
//ENCAIXEHORARIO BIT DEFAULT 0,               -- permitir agendar no mesmo horario
//Observacoes NVARCHAR(800) NULL,                 -- Observa��es adicionais
//Status NVARCHAR(50) NOT NULL DEFAULT 'Pendente',-- Status: Pendente, Confirmado, Cancelado
//SalaID INT NULL FOREIGN KEY REFERENCES Recurso(RecursoID), -- Sala associada (cirurgia/exame)
//EquipamentoID INT NULL FOREIGN KEY REFERENCES Recurso(RecursoID), -- Equipamento espec�fico, se necess�rio
//CONSTRAINT UK_Agendamento UNIQUE (TipoAgendamento, HoraInicio, HoraTermino, SalaID,ENCAIXEHORARIO) -- Garante aus�ncia de conflito
//);
