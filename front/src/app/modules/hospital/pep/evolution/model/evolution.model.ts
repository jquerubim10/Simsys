export class EvolutionModel {
    evolucao: string | null;
    evolucaoTxt: string | null;
    data: string | null;
    hora: string | null;
    
    tutor: string | null;
    conselho: string | null;
    conselho_autorizador: string | null;

    nome_profissional: string | null;
    nome_autorizador: string | null;

    signed_docs: string | null;
    user_authority: any | null;


    /**
     * Constructor
     */
    constructor(modal?) {
        this.evolucao = this.validateNull(modal?.evolucao);
        this.evolucaoTxt = this.validateNull(modal?.evolucaotxt);
        this.data = this.validateNull(modal?.data);
        this.hora = this.validateNull(modal?.hora);

        this.tutor = this.validateNull(modal?.tutor);
        this.conselho = this.validateNull(modal?.conselho);
        this.conselho_autorizador = this.validateNull(modal?.conselhoautorizador);

        this.nome_profissional = this.validateNull(modal?.nomeprofissional);
        this.nome_autorizador = this.validateNull(modal?.nomeautorizador);

        this.signed_docs = this.validateNull(modal?.signeddocs);
        this.user_authority = this.validateNull(modal?.usuario);
    }

    validateNull(value) {
		if (value == 'null' || value == null || value == "") {
			return "";
		}
		return value;
	}
}