import { TitleCasePipe } from "@angular/common";

// -----------------------------------------------------------------------------------------------------
export class DynamicModal {
    nameAndEvolution: string | null;
    atendimento: string | null;
    nomepaciente: string | null;

    nameMother: string | null;
    nomemae: string | null;
    age: string | null;
    nascimento: string | null;
    convenio: string | null;
    alergia: string | null;
    pron: string | null;
    prontuario: string | null;
    tutor: string | null;

    tpatendimento: string | null;
    cpf: string | null;
    hygia: string | null;
    leito: string | null;

    /**
     * Constructor
     */
    constructor(modal?: DynamicModal) {
        this.nameAndEvolution = `${modal?.atendimento} ${modal?.nomepaciente}` || null;
        this.atendimento = modal.atendimento || null;

        this.nameMother = modal?.nameMother || modal?.nomemae || null;
        this.age = modal?.age || modal?.nascimento || null;
        this.convenio = this.validateNameConvenio(modal); 
        this.alergia = this.validateAlergia(modal?.alergia); 
        this.pron = modal?.pron || modal?.prontuario || null;
        this.tutor = modal?.tutor || null;
        this.tpatendimento = modal?.tpatendimento || null;

        this.cpf = this.validateNull(modal?.cpf);
        this.hygia = this.validateNull(modal?.hygia);
        this.leito = this.validateNull(modal?.leito);
    }

    validateNameConvenio(modal) {
        return ` ${modal.nomeconvenio}  ${modal.setor}`;
    }

    validateAlergia(alergia) {
        
        this.validateNull(alergia);

        let titleCasePipe = new TitleCasePipe()
        let sampleTxt = titleCasePipe.transform(alergia).replace(/<[^>]*>/g, '')
        return sampleTxt;
    }
    

    validateNull(value) {
        if (value == "null" || value == "" || value == null) {
            return null;
        }
        return value;
    }
}