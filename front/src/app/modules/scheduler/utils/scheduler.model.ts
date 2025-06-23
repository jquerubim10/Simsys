import { UntypedFormGroup } from "@angular/forms";

export class SchedulerModel {
    id: number | null;
    medicoID: number | null;
    centroCustoID: number | null;
    convenioID: number | null;
    secretariaID: number | null;
    ficha: number | null;

    tipoAgendamento: string | 'consulta';
    title: string | '#1e90ff';
    color: string | null;
    actions: string | null;
    cssClass: string | null;
    meta: string | null;
    paciente: string | null;
    contato1: string | null;
    contato2: string | null;
    observacoes: string | null;
    status: string | 'agendada';

    whatsapp: boolean | false;
    encaixeHorario: boolean | false;
    draggable: boolean | false;
    resizableBeforeStart: boolean | false;
    resizableAfterEnd: boolean | false;
    allDay: boolean | false;
    
    horaInicio: any;
    horaTermino: any;
    nascimento: any;


    /**
     * Constructor
     */
    constructor(item: any) {
        this.id = item.id || null;
        this.medicoID = item.medicoID || null;
        this.centroCustoID = item.centroCustoID || null;
        this.convenioID = item.convenioID || null;
        this.secretariaID = item.secretariaID || null;
        this.ficha = item.ficha || null;

        this.tipoAgendamento = item.tipoAgendamento || 'consulta';
        this.title = item.title || '';
        this.color = item.color || '#1e90ff';
        this.actions = item.actions || '';
        this.cssClass = item.cssClass || '';
        this.meta = item.meta || '';
        this.paciente = item.paciente || '';
        this.contato1 = item.contato1 || '';
        this.contato2 = item.contato2 || '';
        this.observacoes = item.observacoes || '';
        this.status = item.status || 'agendada';

        this.whatsapp = item.whatsapp || false;
        this.encaixeHorario = item.encaixeHorario || false;
        this.draggable = item.draggable || false;
        this.resizableBeforeStart = item.resizableBeforeStart || false;
        this.resizableAfterEnd = item.resizableAfterEnd || false;
        this.allDay = item.allDay || false;


        this.horaInicio = item.horaInicio || '';
        this.horaTermino = item.horaTermino || '';
        this.nascimento = item.nascimento || '';
    }
}