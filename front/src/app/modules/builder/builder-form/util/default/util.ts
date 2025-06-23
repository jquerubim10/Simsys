import { Injectable } from '@angular/core';
import { UntypedFormGroup, Validators } from '@angular/forms';
import { SchedulerModel } from 'app/modules/scheduler/utils/scheduler.model';

@Injectable({ providedIn: 'root' })
export class UtilsMethod {

    getDateFormated() {
       return this.formatDate(new Date());
    }

	getDateFormatedReverse() {
		return this.formatDateReverse(new Date());
	 }

    getHours(): any {
		const now = new Date();
		return now.getHours() + ':' + now.getMinutes();
	}

	getDate(): any {
		const now = new Date();
		return now.toLocaleDateString();
	}

	getFormatedAge(data) {
		const today 	= new Date();
        const birthDate = new Date(data);
        
        let yearsDiff 		= today.getFullYear() - birthDate.getFullYear();
        let monthsDiff 		= today.getMonth() - birthDate.getMonth();
        let daysDiff 		= today.getDate() - birthDate.getDate();

        // Se a diferença em meses for negativa, ajuste os anos e meses
        if (monthsDiff < 0 || (monthsDiff === 0 && daysDiff < 0)) {
        	yearsDiff--;
        }

        if (monthsDiff < 0) { monthsDiff += 12; }

        if (daysDiff < 0) {
            const prevMonthLastDay = new Date(today.getFullYear(), today.getMonth(), 0).getDate();
            daysDiff += prevMonthLastDay;
            monthsDiff--;
        }

        return `${yearsDiff}a ${monthsDiff}m ${daysDiff}d`;
	}

	formatHours(date: Date) {
		return (
			[
				this.padTo2Digits(date.getHours()),
				this.padTo2Digits(date.getMinutes())
			].join(':')
		);
	}

    getDateAndHour(): any {
		const now = new Date();
		return now;
	}

	padTo2Digits(num: number) {
		return num.toString().padStart(2, '0');
	}

	formatDate(date: Date) {
		return (
			[
				date.getFullYear(),
				this.padTo2Digits(date.getMonth() + 1),
				this.padTo2Digits(date.getDate()),
			].join('-') +
			' ' +
			[
				this.padTo2Digits(date.getHours()),
				this.padTo2Digits(date.getMinutes()),
				this.padTo2Digits(date.getSeconds()),
			].join(':')
		);
	}

	formatDateReverse(date: Date) {
		return (
			[
				this.padTo2Digits(date.getDate()), 
				this.padTo2Digits(date.getMonth() + 1),
				date.getFullYear(),
				
			].join('/') +
			' ' +
			[
				this.padTo2Digits(date.getHours()),
				this.padTo2Digits(date.getMinutes()),
			].join(':')
		);
	}

	editConstructor(form: UntypedFormGroup, dataEdit: SchedulerModel, function_type) {

        if (function_type == 'edit') {
            form.get('id').setValue(dataEdit.id);
        }

        form.get('medicoID').setValue(dataEdit.medicoID);
        form.get('centroCustoID').setValue(dataEdit.centroCustoID);
        form.get('convenioID').setValue(dataEdit.convenioID);
        form.get('secretariaID').setValue(dataEdit.secretariaID);
        form.get('ficha').setValue(dataEdit.ficha);

        form.get('tipoAgendamento').setValue(dataEdit.tipoAgendamento);
        form.get('title').setValue(dataEdit.title);
        form.get('color').setValue(dataEdit.color);
        form.get('actions').setValue(dataEdit.actions);
        form.get('cssClass').setValue(dataEdit.cssClass);
        form.get('meta').setValue(dataEdit.meta);
        form.get('paciente').setValue(dataEdit.paciente);
        form.get('contato1').setValue(dataEdit.contato1);
        form.get('contato2').setValue(dataEdit.contato2);
        form.get('observacoes').setValue(dataEdit.observacoes);
        
		if (function_type == 'duplicado') {
            form.get('status').setValue('agendada');
        } else {
			form.get('status').setValue(dataEdit.status);
		}
		
        form.get('whatsapp').setValue(dataEdit.whatsapp);
        form.get('encaixeHorario').setValue(dataEdit.encaixeHorario);
        form.get('draggable').setValue(dataEdit.draggable);
        form.get('resizableBeforeStart').setValue(dataEdit.resizableBeforeStart);
        form.get('resizableAfterEnd').setValue(dataEdit.resizableAfterEnd);
        form.get('allDay').setValue(dataEdit.allDay);


		if (function_type == 'duplicado') {
            form.get('horaInicio').setValue(null);
			form.get('horaTermino').setValue(null);
        } else {
			form.get('horaInicio').setValue(dataEdit.horaInicio);
			form.get('horaTermino').setValue(dataEdit.horaTermino);
		}
        
        form.get('nascimento').setValue(dataEdit.nascimento);

		form.get('tipoAgendamento').disable();
		form.get('medicoID').disable();;
		form.get('centroCustoID').disable();

        return form;
    }

}
