export interface Agendamento {
  agendamentoid: string;
  tipoagendamento: string;
  centrocusto: string;
  convenio: string;
  profissional: string;
  ficha: string;
  paciente: {
    nome: string;
    nascimento: string;
    contato1: string;
    contato2: string;
    whatsapp: string;
  };
  data: string;
  tempo: string;
  usuario: string;
  observacoes: string;
  status: string;
  encaixe: string;
  recursos: {
    codigo: string;
    nome: string;
    quantidade: string;
  }[];
  equipe: {
    funcao: string;
    nome: string;
    id: string;
  }[];
}

export interface MapItem {
  [key: string]: any;
  persons?: any[];
}

export interface ApiResponse {
  columns: string[];
  values: any[];
}

export interface AgendamentoAgrupado {
  data: string;
  centrocusto: string;
  agendamentos: any; // Usando Map para agendamentoid como chave
}