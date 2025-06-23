import { DIALOG_DATA } from '@angular/cdk/dialog';
import { DatePipe, JsonPipe, NgFor, NgIf, TitleCasePipe } from '@angular/common';
import {
	ChangeDetectionStrategy,
	ChangeDetectorRef,
	Component,
	Inject,
	OnInit,
	ViewEncapsulation,
} from '@angular/core';
import { addMinutes } from 'date-fns';
import { FormControl, FormsModule, ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MatOptionModule, provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
	MatDialog,
	MatDialogClose,
	MatDialogContent,
	MatDialogModule,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTimepickerModule, MatTimepickerOption } from '@angular/material/timepicker';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { fuseAnimations } from '@fuse/animations';
import { TranslocoModule } from '@ngneat/transloco';
import { SchedulerApi } from '../api/api';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { DateTime } from 'luxon';
import { finalize } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { IMaskModule } from 'angular-imask';
import { ToastrService } from 'ngx-toastr';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { UtilsMethod } from 'app/modules/builder/builder-form/util/default/util';
    

@Component({
    selector			: 'app-modal-scheduler-atendence',
    templateUrl			: './modal-scheduler-atendence.component.html',
    providers			: [provideNativeDateAdapter()],
	encapsulation		: ViewEncapsulation.None,
	changeDetection		: ChangeDetectionStrategy.Default,
    animations			: fuseAnimations,
	standalone			: true,
    imports: [
        FormsModule,
        TitleCasePipe,
        ReactiveFormsModule,
		MatDialogClose,
		MatDialogContent,
		MatDialogModule,
		MatDialogTitle,
        MatIconModule,
        NgIf,
        NgFor,
		IMaskModule,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
		MatTimepickerModule,
    	MatDatepickerModule,
        MatRadioModule,
        MatOptionModule,
		MatAutocompleteModule,
        MatProgressSpinnerModule,
        MatSelectModule,
        MatCheckboxModule,
        JsonPipe,
		TranslocoModule
    ],
    styles: [
        /* language=SCSS */
        `
			.inventory-grid {
				grid-template-columns: 6vw 6vw 30vw auto;
			}

			.inventory-grid-team {
				grid-template-columns: 6vw auto 40px;
			}
			
			.bg-color-header {
				background-color: #61b6f3;
    			color: #fff;
			}

			@screen sm {
				.modal-scheduler-atendence {
					max-width: 50vw !important;
				}
			}

			@screen md {
				.modal-scheduler-atendence {
					max-width: 50vw !important;
				}
			}

			@screen lg {
				.modal-scheduler-atendence {
					max-width: 50vw !important;
				}
			}

			.item-disabled {
				cursor: default;
				opacity: 0.4;
			}

			.status-cicle {
				width: 10px;
				height: 10px;
				border-radius: 100%;
			}
		`,
    ]
})
export class ModalSchedulerAtendenceComponent implements OnInit {

	// Setup the plans
	plans = [
		{
			value: 'consulta',
			label: 'Consulta',
		},
		{
			value: 'exame',
			label: 'Exame',
		},
		{
			value: 'cirurgia',
			label: 'Cirurgia',
		},
	];

	// Setup the status
	status_list = [
		{
			value: 'agendada',
			label: 'Agendada',
			color: '#00ff89'
		},
		{
			value: 'cancelada_pelo_paciente',
			label: 'Cancelada pelo paciente',
			color: '#ff8d00'
		},
		{
			value: 'cancelada_pelo_profissional',
			label: 'Cancelada pelo profissional',
			color: '#a500e9'
		},
		{
			value: 'em_atendimento',
			label: 'Em atendimento',
			color: '#0F0'
		},
		{
			value: 'confirmada',
			label: 'Confirmada',
			color: '#ffd400'
		},
		{
			value: 'falta',
			label: 'Falta',
			color: '#F00'
		},
		{
			value: 'finalizada',
			label: 'Finalizada',
			color: '#00F'
		},
		{
			value: 'paciente_aguardando',
			label: 'Paciente aguardando ',
			color: '#f59e0b'
		},
	];

	typeResourcesInputList: any = [
		{ value: 'equipamento', 	label: 'Equipamento' },
		{ value: 'exame', 			label: 'Exame' },
		{ value: 'cirurgia', 		label: 'Cirurgia' }
	];

	scheduler_list = {
		resources_list:  [],
		teams_list:  []
	};

	scheduler_list_persist = {
		resources_list:  [],
		teams_list:  []
	};

	/* Get the current instant */
	now								: DateTime = DateTime.now();
	datePipe						: DatePipe = new DatePipe('pt-BR');

	paciente_register				: any = '';

	scheduler_type					: any = 'scheduler_new';
	scheduler_item_start			: any = '';
	scheduler_item_end				: any = '';

	scheduler_teams_list			: any[] = [];
	scheduler_teams_list_persist	: any[] = [];

	isLoading						: boolean = true;
	isLoadingDoctorScale			: boolean = true;
	isLoadingDoctor					: boolean = false;
	isLoadingRecurso				: boolean = false;

	userLooged						: any = JSON.parse(localStorage.getItem("loggedUser"));

	zoneInputList					: any = [];
	setorInputList					: any = [];
	doctorInputList					: any = [];
	convenioInputList				: any = [];
	resourcesInputList				: any = [];
	professionalsInputList			: any = [];

	customOptions					: MatTimepickerOption<Date>[] = [];

	selected_time					: any = 15;
	type_resources 					: any = "exame";
	name_resources 					: any = null;
	quantity_resources 				: any = 1;

	type_function 					: any = "anestesista";
	name_professional 				: any = "";

	planBillingForm					: UntypedFormGroup;
	zoneInputControl				: UntypedFormControl = new UntypedFormControl();
	doctorInputControl				: UntypedFormControl = new UntypedFormControl();

	constructor(
		@Inject(DIALOG_DATA) 
		public data						: { scheduler_item: any; scheduler_type: any },
		@Inject(ChangeDetectorRef) 
		private _changeDetectorRef		: ChangeDetectorRef,
		@Inject(UntypedFormBuilder) 
		private _formBuilder			: UntypedFormBuilder, 
		private _toastr                 : ToastrService,
		private _schedulerApi			: SchedulerApi,
		private _utilsMethod			: UtilsMethod,
		public dialog					: MatDialog,
	) {}

	ngOnInit(): void {
		// Create the form
        this.planBillingForm = this._formBuilder.group({
			id: [null],

			medicoID: new FormControl(null, [Validators.required]),
			centroCustoID: new FormControl(null, [Validators.required]),
			secretariaID: new FormControl(this.userLooged.usuario, [Validators.required]),
			convenioID: [null],

			ficha: [],
			tipoAgendamento: ['consulta'],
            title: [''],
			color: ['#1e90ff'],
            actions: [''],
            cssClass: [''],
            meta: [''],
            paciente: new FormControl('', [Validators.required, Validators.minLength(4)]),
            contato1: new FormControl('', [Validators.required]),
            contato2: [''],
            observacoes: [''],
            status: ['agendada'],
			
            whatsapp: [false],
            encaixeHorario: [false],
            draggable: [false],
            resizableBeforeStart: [false],
            resizableAfterEnd: [false],
            allDay: [false],
			
			nascimento: new FormControl(''),
            horaInicio: [this.now.startOf('day').toISO()],
            horaTermino: [this.now.startOf('day').toISO()]
        });

		this.scheduler_type = this.data?.scheduler_type;
		this._changeDetectorRef.markForCheck();

		switch(this.data.scheduler_type) {
			case 'scheduler_duplicated': {
				this.handleDuplicatedOrEdit('duplicado');

				this._changeDetectorRef.markForCheck();
				break;
			}
			case 'scheduler_ready': {
				this.handleDuplicatedOrEdit('edit');

				this._changeDetectorRef.markForCheck();
				break;
			}
		}

		this._schedulerApi.getAllZone().subscribe((data) => {
			this.zoneInputList = data.content;
			this._changeDetectorRef.markForCheck();
		});

		this._schedulerApi.getAllSetor().subscribe((data) => {
			this.setorInputList = data.content;
			this._changeDetectorRef.markForCheck();
		});

		this._schedulerApi.getAllConvenio().subscribe((data) => {
			this.convenioInputList = data.content;
			this._changeDetectorRef.markForCheck();
		});

		this.isLoading = false;
		this._changeDetectorRef.markForCheck();
	}

	handleDuplicatedOrEdit(type) {
		this.planBillingForm = this._utilsMethod.editConstructor(this.planBillingForm, JSON.parse(this.data.scheduler_item), type);

		this.handleFindDoctor();
		this.getDoctorScale();

		this.getAllRecursoByAgendamento(JSON.parse(this.data.scheduler_item).id);
		this.getAllTeamByAgendamento(JSON.parse(this.data.scheduler_item).id);

		this._changeDetectorRef.markForCheck();
	}

	handleChangeTime() {
		this.planBillingForm.get('horaTermino').setValue(addMinutes(new Date(this.planBillingForm.get('horaInicio').value), this.selected_time));
		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	validateTime() {
		let horaInicio = new Date(this.planBillingForm.get('horaInicio').value);
		let horaTermino = new Date(this.planBillingForm.get('horaTermino').value);

		this.selected_time = Math.abs(horaTermino.getTime() - horaInicio.getTime()) / 60000;
		
		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	handleFindDoctor() {
		if (this.planBillingForm.get('centroCustoID').value) {
			this._schedulerApi.getAllDoctorsByCenter(this.planBillingForm.get('centroCustoID').value).subscribe((data) => {
				this.doctorInputList = data.content;

				// Mark for check
				this._changeDetectorRef.markForCheck();
			});
		}
	}

	getStatusDescription(status) {
		return this.status_list.find((item) => item.value == status)?.label || '';
	}

	getStatusColor(status) {
		return this.status_list.find((item) => item.value == status)?.color || '#FFF';
	}

	getDoctorScale() {
		this.isLoadingDoctorScale = true;

		let date = this.datePipe.transform(new Date(this.planBillingForm.get('horaInicio').value), 'yyyy-MM-dd');
		let valueLong = this.planBillingForm.get('centroCustoID').value ? this.planBillingForm.get('centroCustoID').value : null;
		let medicoLong = this.planBillingForm.get('medicoID').value ? this.planBillingForm.get('medicoID').value : null;
		let valueInsider = 0;

		let valueScheduler = " ";
		if (this.scheduler_type == 'scheduler_ready') {
			valueScheduler = " AND A.AgendamentoID <> " + this.planBillingForm.get('id').value;
		}

		if (this.planBillingForm.get('encaixeHorario').value == true) {
			valueInsider = 1;
		}

		this.customOptions = [];

		this._schedulerApi.getAllDoctorScale(date, valueLong, medicoLong, valueScheduler, valueInsider).subscribe((response) => {

			if (response.length == 0) {
				this.customOptions.push({ label: "Sem Escala", value:  new Date() });
				this.isLoadingDoctorScale = false;
					
				// Mark for check
				this._changeDetectorRef.markForCheck();
			}

			response.forEach((item, i) => {
				let itemTranform = [];
				item.columns.forEach((column, index) => {
					itemTranform.push(
						{ columnValue: column.toLowerCase() + '_' + item.values[index] }	
					);

					if (index == (item.columns.length - 1)) {
						let item_consume: any = this.toObject(itemTranform);

						if (this.scheduler_type == 'scheduler_new') {
							this.selected_time = +item_consume.intervalominutos;
						} else {
							this.validateTime();
						}
						

						let data_consulta_inicio = `${this.now.toFormat("yyyy'-'MM'-'dd")} ${item_consume.horario}.000`;
						let data_consulta_fim = DateTime.fromISO(new Date(data_consulta_inicio).toISOString()).toFormat("HH':'mm");

						//2025-02-28 23:20:00.000
						this.customOptions.push({ label: data_consulta_fim, value:  new Date(data_consulta_inicio)});
						// Mark for check
						this._changeDetectorRef.markForCheck();
					}
				});

				if (i == (response.length - 1)) {
					this.isLoadingDoctorScale = false;

					// Mark for check
					this._changeDetectorRef.markForCheck();
				}
			});
		});
	}


	// -----------------------------------------------------------------------------------------------------
    // @ Scheduler Resources methods
    // -----------------------------------------------------------------------------------------------------

	populationSchedulerResources() {

		if (!this.validateToSaveResources()) {
			this.throwError("Erro para adicionar o recursos")
			return;
		}

		this.scheduler_list.resources_list.push({ 
			tipo: this.type_resources, 
			name_resource: this.name_resources + ' - ' + this.getResourcesName(this.name_resources), 
			recursoID: this.name_resources, 
			quantidade: this.quantity_resources 
		});

		this.scheduler_list_persist.resources_list.push({
			agendamentoID: null, 
			recursoID: this.name_resources, 
			quantidade: this.quantity_resources 
		});

		
		this.setDefaultsValue();
		// Mark for check
		this._changeDetectorRef.markForCheck();
	}
	
	removeSchedulerResources(index) {
		this.scheduler_list.resources_list.splice(index, 1);
		this.scheduler_list_persist.resources_list.splice(index, 1);

		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	getAllRecursoByAgendamento(id) {
		this._schedulerApi.getAllResoucesAtendimento(id)
			.pipe( finalize(() => {
				console.log("Sucesso!!");
			})).subscribe((response) => {
				
			if (response?.content.length > 0) {
				response.content.forEach((item) => {
					this.scheduler_list.resources_list.push({ 
						tipo: item[0],
						name_resource: item[1] + ' - '+ item[2], 
						recursoID: item[3], 
						quantidade: item[4]
					});
			
					this.scheduler_list_persist.resources_list.push({
						agendamentoID: id,
						recursoID: item[3],
						quantidade: item[4]
					});
				});
			}

			this._changeDetectorRef.markForCheck();
		});
	}

	handleRecursoValue(value) {
		this.resourcesInputList = [];
		this.planBillingForm.disable();

		let valueLong = this.planBillingForm.get('centroCustoID').value ? this.planBillingForm.get('centroCustoID').value : null;
		let value_tipo = this.type_resources ? this.type_resources : null;

		value_tipo = this.typeResourcesInputList.find((item) => item.value == value_tipo)?.label || null;

		this._schedulerApi.getAllResourcesSql(value, value_tipo, valueLong ).subscribe((resources) => {
			if (resources.content != null && resources.content.length > 0) {

				this.resourcesInputList = resources.content;

				this.isLoadingRecurso = false;

				this.planBillingForm.enable();
				this._changeDetectorRef.markForCheck();
			} else {
				this.planBillingForm.enable();

				this.isLoadingRecurso = false;
				this._changeDetectorRef.markForCheck();
			}
		}, (err) => {
			this.throwError(err.error.message);

			this.planBillingForm.enable();
			this._changeDetectorRef.markForCheck();
		})
	}

	getResourcesName(value) {
		return value != ""  && value != null 
			? this.resourcesInputList.find((item) => item.id == value)?.nome || '' 
			: "";
		
	}

	// -----------------------------------------------------------------------------------------------------
    // @ Scheduler Team methods
    // -----------------------------------------------------------------------------------------------------

	handleTeamValue(value) {
		this.isLoadingDoctor = true;

		this.professionalsInputList = [];
		this.planBillingForm.disable();

		this._schedulerApi.getAllDoctorsSql( value ).subscribe((doctors) => {
			if (doctors.length > 0) {
				doctors.forEach((doctor, i) => {
					let arrayHeader = [];
					doctor.columns.forEach((column, index) => {
						arrayHeader.push({
							columnValue: column.toLowerCase() + '_' + doctor.values[index]
						});

						if (index == (doctor.columns.length - 1)) {
							this.professionalsInputList.push(this.toObject(arrayHeader));
						}
					});

					if (i == (doctors.length - 1)) {
						this.planBillingForm.enable();
						this.isLoadingDoctor = false;

						this._changeDetectorRef.markForCheck();
					} 
				});
			} else {
				this.planBillingForm.enable();
				this.isLoadingDoctor = false;

				this._changeDetectorRef.markForCheck();
			}
		}, (err) => {
			this.throwError(err.error.message);

			this.planBillingForm.enable();
			this._changeDetectorRef.markForCheck();
		})
	}

	getDoctorName(value) {
		return value != ""  && value != null 
			? this.professionalsInputList.find((item) => item.medico == value)?.descricao || '' 
			: "";
		
	}

	getAllTeamByAgendamento(id) {
		this._schedulerApi.getAllTeamByAgendamento(id)
			.pipe( finalize(() => {
				console.log("Sucesso!!");
			})).subscribe((response) => {
				
			if (response?.content.length > 0) {
				response.content.forEach((item) => {
					this.scheduler_list.teams_list.push({ 
						tipo: item[0],
						name_resource: item[1] + ' - '+ item[2], 
						medicoID: item[1], 
						funcao: item[0]
					});
			
					this.scheduler_list_persist.teams_list.push({
						agendamentoID: id,
						medicoID: item[1],
						funcao: item[0]
					});
				});
			}

			this._changeDetectorRef.markForCheck();
		});
	}

	populationSchedulerTeam() {

		if (!this.validateToSaveTeam()) {
			this.throwError("Erro para adicionar o time")
			return;
		}

		this.scheduler_list.teams_list.push({ 
			tipo: this.type_function, 
			name_resource: this.name_professional + ' - ' + this.getDoctorName(this.name_professional), 
			medicoID: this.name_professional,
		});

		this.scheduler_list_persist.teams_list.push({
			agendamentoID: null, 
			medicoID: +this.name_professional,
			funcao: this.type_function
		});
		
		this.setDefaultsValue();
		// Mark for check
		this._changeDetectorRef.markForCheck();
	}
	
	removeSchedulerDoctor(index) {
		this.scheduler_list.teams_list.splice(index, 1);
		this.scheduler_list_persist.teams_list.splice(index, 1);

		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	
	// -----------------------------------------------------------------------------------------------------
    // @ Scheduler methods
    // -----------------------------------------------------------------------------------------------------
	createScheduler() {

		// Disable the form
		this.planBillingForm.disable();

		this.handleChangeTime();

		this.planBillingForm.get('color').setValue(this.getStatusColor(this.planBillingForm.get('status').value));
		this.planBillingForm.get('actions').setValue(JSON.stringify(this.scheduler_list_persist));

		this.planBillingForm.get('nascimento').setValue(this.datePipe.transform(new Date(this.planBillingForm.get('nascimento').value), 'yyyy-MM-dd'));

		this._schedulerApi.addScheduler(this.planBillingForm.value)
			.pipe( finalize(() => {
					// Re-enable the form
					this.planBillingForm.enable();
				})).subscribe((response) => {

				this.throwSuccess('Agendamento criado com sucesso');
				this.planBillingForm.reset();

				setTimeout(() => {
					this.dialog.getDialogById('scheduler_new').close();
				}, 200);
			});
	}

	editScheduler() {

		// Disable the form
		this.planBillingForm.disable();

		this.handleChangeTime();

		this.planBillingForm.get('color').setValue(this.getStatusColor(this.planBillingForm.get('status').value));
		this.planBillingForm.get('actions').setValue(JSON.stringify(this.scheduler_list_persist));

		this._schedulerApi.editScheduler(this.planBillingForm.value)
			.pipe( finalize(() => {
					// Re-enable the form
					this.planBillingForm.enable();
				})).subscribe((response) => {

				this.throwSuccess('Agendamento atualizado com sucesso!');
				
				setTimeout(() => {
					this.dialog.getDialogById('scheduler_new').close();
				}, 200);
			});
	}


	insederChange() {
		if (this.planBillingForm.get('medicoID').value != null) {
			this.getDoctorScale();
		}
	}


	// -----------------------------------------------------------------------------------------------------
    // @ Public methods
    // -----------------------------------------------------------------------------------------------------

	handleValidateSaveScheduler(type) {
		let type_screen = this.planBillingForm.get('tipoAgendamento').value;

		// Return if the form is invalid
		if (this.planBillingForm.invalid) {
			return;
		}

		if (type_screen == 'exame' && this.scheduler_list.resources_list.length == 0) {
			this.throwError("Precisa adicionar pelo menos um recurso para o exame");
			return;
		}
		

		if (type_screen == 'cirurgia' && this.scheduler_list.teams_list.length == 0 && this.scheduler_list.resources_list.length == 0) {
			this.throwError("Precisa adicionar pelo menos um recurso ou time para a Cirurgia");
			return;
		}

		let date = this.datePipe.transform(new Date(this.planBillingForm.get('horaInicio').value), 'yyyy-MM-dd');
		let hora = DateTime.fromISO(new Date(this.planBillingForm.get('horaInicio').value).toISOString()).toFormat("HH':'mm");
		let sql =  `EXEC VERIFICAHORARIODISPONIVEL ${this.planBillingForm.get('centroCustoID').value}, 
												${this.planBillingForm.get('medicoID').value},
												'${date}',
												'${hora}', 
												${this.selected_time}
												, ${type == 'new' ? 0 : this.planBillingForm.get('id').value}`;

		if (this.planBillingForm.get('encaixeHorario').value == true) {
			if (type == 'edit') {
				return this.editScheduler();
			}
			if (type == 'new') {
				return this.createScheduler();
			}
		}

		if (this.planBillingForm.get('encaixeHorario').value == false) {
			this._schedulerApi.handleValidateSaveScheduler(sql).subscribe((response) => {

				if (response[0].values[0] != 0) {
					if (type == 'edit') {
						return this.editScheduler();
					}
					if (type == 'new') {
						return this.createScheduler();
					}
				}
				
				return this.throwError("Erro para salvar o agendamento, o horário já está ocupado!");

			}, (err) => {
				return this.throwError(err.error.message);
			});
		}
	}

    /**
     * Track by function for ngFor loops
     *
     * @param index
     * @param item
     */
    trackByFn(index: number, item: any): any {
        return item.id || index;
    }

	/**
	 * @description Rezetando os valores padroes
	 * 
	 */
	setDefaultsValue() {
		this.name_resources = null;
		this.name_professional = null;
		this.quantity_resources = 1;
	}

	/**
	 * 
	 * Validando para adicionar um recurso ao agendamentos
	 * 
	 * @returns boolean
	 */
	validateToSaveResources() {
		return this.type_resources != null && this.type_resources != '' &&
				this.name_resources != null && this.name_resources != '' &&
				this.quantity_resources != null && this.quantity_resources > 0;
	}

	/**
	 * 
	 * Validando para adicionar um time ao agendamentos
	 * 
	 * @returns boolean
	 */
	validateToSaveTeam() {
		return this.type_function != null && this.type_resources != '' &&
				this.name_professional != null && this.name_professional != '';
	}

	/**
	 * 
	 * @description Validando os caracteres para fazer a pesquisa de recursos
	 * 
	 */
	onKeySearch(value, tipo) {
		if (value.length > 2) {
			
			if (tipo == 'team') {
				this.handleTeamValue(value)
			} else {
				this.isLoadingRecurso = true;
				this.handleRecursoValue(value)
			}
		}
	}

	/**
	 * 
	 * @description Função para limpar a lista de recursos ao alterar o tipo
	 * 
	 */
	onKeyChange() {
		this.resourcesInputList = [];
		this.professionalsInputList = [];
	}

	/**
	 * Funcition to Successo
	 * 
	 * @param msg 
	 */
	throwSuccess(msg) {
		this._toastr.success(msg, 'Successo', {
			timeOut: 3000,
			closeButton: true,
			progressBar: true,
		});
	}

	/**
	 * Funcition to throwError
	 * 
	 * @param err 
	 */
	throwError(err) {
		this._toastr.error(err, 'Error', {
			timeOut: 3000,
			closeButton: true,
			progressBar: true,
		  });
	}

	toObject(arr) {
		// target object
		let rv = {};
		// Traverse array using loop
		for (let i = 0; i < arr.length; ++i) {
			let name;
			if ((arr[i].columnValue.match(/_/g) || []).length > 1) {
				name = arr[i].columnValue.replace('_', ''); // valor tela anterior coluna [coluns_value]
			} else {
				name = arr[i].columnValue; // valor anterior coluna [coluns_value]
			}

			// Assign each element to object
			rv[name.substring(0, name.indexOf('_'))] = name.substring(name.indexOf('_') + 1);
		}
			
		return rv;
	}
}
