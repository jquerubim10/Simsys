export interface EvolutionRecord {
  evolucao: number;
  registro: number;
  nome: string;
  data: string;
  usuario: number;
  hora: string;
  signed_docs: number | null;
  evolucao_txt: string;
  nome_profissional: string;
  conselho: string;
  tutor: string;
  prontuario: number;
  datainternacao: string;
  dataalta: string | null;
  usuario_autorizador: number;
  nome_autorizador: string;
  tutor_autorizador: string;
  conselho_autorizador: string | null;
}

export interface Tutor {
  id: number;
  value: string;
}

export interface TutorApiResponse {
  columns: string[];
  values: any[];
}