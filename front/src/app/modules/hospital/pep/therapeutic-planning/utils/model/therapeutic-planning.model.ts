export class TherapeuticPlanningModel {
    item?: string;
    procedimentonome?: string;
    descricao?: string;
    quantidade?: string;
    quantidadeindividual?: string;

    dose?: string;
    via?: string;
    intervalo?: string;

    data?: string;
    hora?: string;
    

    registro?: string;
    procedimento?: string;
    prescricao?: string;
    sequencia?: string;

    necessario?: string;
    medicacaoadministrada?: string;
    viaadministrativo?: string;

    suspenso?: string;
    datasuspensao?: string;
    horasuspensao?: string;
    usuariosuspensao?: string;

    nome?: string;
    atualizacao?: string;
    obsnaoenviado?: string;
    observacaoprescricao?: string;

    datas?: any[] = [];
    /**
    * Constructor
    */
    constructor(modal?: TherapeuticPlanningModel) {
        this.item = modal?.procedimentonome || null;
        this.procedimentonome = modal?.procedimentonome || null;
        this.descricao = modal?.descricao || null;
        this.quantidade = modal?.quantidade || null;
        this.quantidadeindividual = modal?.quantidadeindividual || null;

        this.dose = modal?.dose || null;
        this.via = modal?.via || null;
        this.intervalo = modal?.intervalo || null;
        

        this.data = modal?.data || null;
        this.hora = modal?.hora || null;

        this.registro = modal?.registro || null;
        this.procedimento = modal?.procedimento || null;
        this.prescricao = modal?.prescricao || null;
        this.sequencia = modal?.sequencia || null;

        this.necessario = modal?.necessario || null;
        this.medicacaoadministrada = modal?.medicacaoadministrada || null;
        this.viaadministrativo = modal?.viaadministrativo || null;

        
        this.suspenso = modal?.suspenso || null;
        this.datasuspensao = modal?.datasuspensao || null;
        this.horasuspensao = modal?.horasuspensao || null;
        this.usuariosuspensao = modal?.usuariosuspensao || null;

        this.nome = modal?.nome || null;
        this.atualizacao = modal?.atualizacao || null;
        this.obsnaoenviado = modal?.obsnaoenviado || null; 
        this.observacaoprescricao = modal?.observacaoprescricao || null; 
        this.datas = modal?.datas || [];
    }
}