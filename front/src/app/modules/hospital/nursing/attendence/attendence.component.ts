import { DatePipe, NgFor, NgIf, TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule, UntypedFormControl } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOptionModule, MatRippleModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router, RouterModule } from '@angular/router';
import { fuseAnimations } from '@fuse/animations';
import { TranslocoModule } from '@ngneat/transloco';
import { UtilApi } from 'app/app-api/default/api-util';
import { DynamicModalComponent } from 'app/core/utils/dynamic-modal/dynamic-modal.component';
import { DateTime } from 'luxon';
import { ToastrService } from 'ngx-toastr';
import { TherapeuticPlanningComponent } from '../../pep/therapeutic-planning/therapeutic-planning.component';
import { DifeAgePipe } from './utils/pipe/diffAge.pipe';
import { CenterCostApi } from './utils/service/center-cost.service';

@Component({
    selector: 'app-attendence',
    templateUrl: './attendence.component.html',
    styles: [
        /* language=SCSS */
        `	
		.cdk-overlay-pane {
			min-width: 13rem;
		}

		.mat-mdc-menu-panel {
			width: 100% !important;
		}
	
		.min-w-12 {
			min-width: 12rem !important;
		}

		.w-30px {
			width: 30px !important;
		}

		.internal-spinner {
			display: inline-block;
			position: absolute;
			right: 25px;
		}

		.min-w-80 {
			min-width: 20rem !important;
		}

		.inventory-grid:nth-last-child(even) {
			background-color: lightblue;

			button {
				border-color: gray !important;
			}
		}

		.visible {
			display: none !important;
		}

		.align-text {
			text-align: left;
		}

		.inventory-grid {
			grid-template-columns:  auto 120px 48px 80px 20rem 100px 15rem 10rem 150px 5rem;

			div:nth-child(10) {
				display: flex !important;
				flex-wrap: wrap;
			} 

			.toolt {
				width: 1rem;
				height: 1rem;
				padding: 0.5rem;
				border-radius: 1rem;
			}

			.classf {
				width: 50%;
				color: transparent;
			}

			@screen sm {
				grid-template-columns:  auto 120px 48px 80px 20rem 100px 15rem 10rem 150px 150px 5rem;
			}

			@screen md {
				grid-template-columns: auto 120px 48px 80px 20rem 100px 15rem 10rem 150px 150px 5rem;
			}

			@screen lg {
				grid-template-columns:  5rem 120px 48px 80px 20rem 100px 15rem 10rem 150px 150px 5rem;
			}
		}
		`,
    ],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: fuseAnimations,
	standalone: true,
    imports: [
        NgIf,
        TitleCasePipe,
        MatProgressSpinnerModule,
        MatDatepickerModule,
        MatTooltipModule,
        MatMenuModule,
        DifeAgePipe,
        DatePipe,
        RouterModule,
        MatProgressBarModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        FormsModule,
        ReactiveFormsModule,
        MatButtonModule,
        MatSortModule,
        NgFor,
        MatPaginatorModule,
        MatSlideToggleModule,
        MatSelectModule,
        MatOptionModule,
        MatCheckboxModule,
        MatRippleModule,
        TranslocoModule,
    ]
})
export class AttendenceComponent implements OnInit {

  	isLoading = false;
	isLoadingSelect = false;
	isSubLoading = false;

	subList: any[] = [];
	listComponent: any[] = [];
	listGroup: any[] = [];
	zoneList: any[] = [];
	responseBody: any[] = [];
  	heardsSearch: any[] = [];

	style = 'grid-template-columns: ';

	startDate;
	dayInputControl = DateTime.now();
	datePipe: DatePipe = new DatePipe('pt-BR');

	searchInputControl: UntypedFormControl = new UntypedFormControl();
	attenInputControl: UntypedFormControl = new UntypedFormControl('I');
	zoneInputControl: UntypedFormControl = new UntypedFormControl(0);

  	CopydisplayedColumns: string[] = [
		'ANAMNESE',
		'ATEDIMENTO',
		'CID',
		'CLASSIFICACAO',
		'CONFERIDO',
		'CONVENIO',
		'DATA_ALTA',
		'DATA_ATENDIMENTO',
		'ESPECMEDICA',
		'EVOLUCAO',
		'HORA_PRESCRICAO',
		'HORA_TRIAGEM',
		'INSUMO',
		'LEITO',
		'MEDICO',
		'MEDICO_BLOQUEIO',
		'NASCIMENTO',
		'NOMEMEDICO',
		'NOME_CONVENIO',
		'NOME_PACIENTE',
		'PRONTUARIO',
		'SETOR',
		'STATUS_LAUDO',
		'TEMPO_ATENDIMENTO',
		'TEMPO_CONSULTA',
		'TEMPO_SETOR',
		'TEMPO_TRIAGEM',
		'TIPOCONVENIO',
		'TIPOSAIDA',
		'TIPO_ATENDIMENTO',
		'TIPO_SETOR',
		'URGENCIA',
		'edit',
	];

	treatmentList = [
		{id: 'I', label: 'inter', value: 'INTERNO'},
		{id: 'A', label: 'ambu', value: 'AMBULATORIAL'},
		{id: 'U', label: 'emerg', value: 'URGÊNCIA'},
		{id: 'E', label: 'exter', value: 'EXTERNO'},
	]

	displayedColumns = [
		{ label: 'data_atendimento', compare: 'dataatendimento' , columnName: 'DATA_ATENDIMENTO' },
		{ label: '', compare: 'classificacao' , columnName: 'CLASSIFICACAO' },
		{ label: 'atendimento', compare: 'atendimento' , columnName: 'ATEDIMENTO' },
		{ label: 'nome_pasciente', compare: 'nomepaciente' ,columnName: 'NOME_PACIENTE' },
		{ label: 'idade', compare: 'nascimento' ,columnName: 'NASCIMENTO' },
		{ label: 'nome_medico', compare: 'nomemedico' ,columnName: 'NOMEMEDICO' },
		{ label: 'nome_convenio', compare: 'nomeconvenio' ,columnName: 'NOME_CONVENIO' },
		{ label: 'setor', compare: 'descricaocc' ,columnName: 'DESCRICAOCC' },
		{ label: 'leito', compare: 'leito' ,columnName: 'LEITO' },
		{ label: 'status', compare: 'status' ,columnName: 'status' },
	];

  /**
	 * Constructor
	 */
	constructor(
		private _utilApi: UtilApi, private _centerCostApi: CenterCostApi,
		private _changeDetectorRef: ChangeDetectorRef,
		private _toastr: ToastrService,
		public dialog: MatDialog, public router: Router
	) {}

  	ngOnInit(): void {
		this.startDate = this.datePipe.transform(new Date(), 'dd-MM-yy');
		this.heardsSearch = this.displayedColumns;
		this.cssCreated();

		// Subscribe to search input field value changes
		this.attenInputControl.valueChanges.subscribe((query) => {
			this.getDataInitial()
		});

		// Subscribe to search input field value changes
		this.zoneInputControl.valueChanges.subscribe((query) => {
			this.getDataInitial();
		});

		this.getDataInitial();
		this.getComponent();
		this.getGroup();
	}

  	getDataInitial() {
		this.isLoading = true;
		this.isLoadingSelect = true;
		let where = "";

		if (this.attenInputControl.value == 'I') {
			where +=  "AND ATEND.TIPO = " + "'"  + this.attenInputControl.value +"'" 
		} else {
			where +=  "AND (DATEADD(dd, DATEDIFF(dd, 0, ATEND.DATA_ATENDIMENTO), 0)=" + "'"+this.datePipe.transform(this.dayInputControl.toISODate(), 'yyyy/MM/dd') + "'" + " AND ATEND.TIPO = " + "'"  + this.attenInputControl.value +"')" 
		}

		if (this.zoneInputControl.value != 0) {
			where +=  ` AND ATEND.CENTROCUSTO_ATUAL = ${this.zoneInputControl.value} ` 
		}
		
		this.responseBody = [];

		let logged = JSON.parse(localStorage.getItem("loggedUser"));

		this._utilApi.postResponseBody('treatment/list', {whereValue: where, loggedUser: logged.usuario})
			.subscribe((response) => {
				if(response.length > 0) {
					response.forEach((item, i) => {
						let arrayHeader = [];
						item.columns.forEach((column, index) => {
							arrayHeader.push(
								{
									columnValue: column.toLowerCase() + '_' + item.values[index],
								}	
							);
	
							if (index == (item.columns.length - 1)) {
								this.responseBody.push(this.toObject(arrayHeader));
							}
						});
	
						if (i == (response.length - 1)) {
							this.isLoading = false;
							this.getZoneInitial();
	
							// Mark for check
							this._changeDetectorRef.markForCheck();
						}
					});
				} else {
					this.isLoading = false;
					this.isLoadingSelect = false;
					// Mark for check
					this._changeDetectorRef.markForCheck();
				}
				
			}, (err) => {
				this.isLoading = false;
				this.isLoadingSelect = false;
				
				this.throwError(err);

				// Mark for check
				this._changeDetectorRef.markForCheck();
			});
	}

	getGroup() {
		this._utilApi.getAll('menu/group/')
			.subscribe((res) => {
				this.listGroup = res.content;
			}, (erro) => {
				this.throwError(erro);
			})
	}

	openMatMenuProgrammatically(data) {
		this.isSubLoading = true;
		this._utilApi.getAll('menu/group/' + data.id + '/screens').subscribe((e) => {
			this.subList = e.content;
			this.isSubLoading = false;

			// Mark for check
			this._changeDetectorRef.markForCheck();
		}, (err) => {
			this.isSubLoading = false;
			
			this.throwError(err);

			// Mark for check
			this._changeDetectorRef.markForCheck();
		})
	}

	getComponent() {
		this._utilApi.getAll('builder/screen/component').subscribe((e) => {
			this.listComponent = e.content;

			// Mark for check
			this._changeDetectorRef.markForCheck();
		}, (erro) => {
			this.throwError(erro);

			// Mark for check
			this._changeDetectorRef.markForCheck();
		})
	}

	openDynamicDialog(comp, item) {
		localStorage.setItem("opened_screen", JSON.stringify(comp))

		const dynamicDialogScreen = this.dialog.open(DynamicModalComponent, {
			height: '90vh',
			width: '98vw',
			panelClass: 'my-dialog',
			data: {comp, item}
		});

		dynamicDialogScreen.afterClosed().subscribe((result) => {
			console.log(`Dialog close result: ${result}`);
			console.log(item);	
			localStorage.removeItem("userScreen");
			localStorage.removeItem("dynamicScreen");
			localStorage.removeItem("opened_screen");
		});
		
	}

	openCheckScreen(item) {
		const checkDialogScreen = this.dialog.open(TherapeuticPlanningComponent, {
			height: '90vh',
			width: '98vw',
			panelClass: 'my-dialog',
			data: {item}
		});

		checkDialogScreen.afterClosed().subscribe(result => {
			console.log(`Dialog check close result: ${result}`);
			console.log(item);	
			localStorage.removeItem("userScreen");
			localStorage.removeItem("dynamicScreen");
			localStorage.removeItem("opened_screen");
		});
		
	}

	getZoneInitial() {
		let array = [];

		this.responseBody.forEach((item, index) => {
			if (item["setor"]) {
				array.push(+item["setor"])
			}

			if (index == (this.responseBody.length - 1)) {
				let newArray = array.filter((item, index) => array.indexOf(item) === index);
				let a = "( "

				newArray.forEach((item, index) => {
					if (index < (newArray.length -1)) {
						a += item + ","
					} else {
						a += item + " )"
						this.executeQuery(a);
					}
				})
			}
		})
	}

	executeQuery(element) {
		let zone = [];
		let user = JSON.parse(localStorage.getItem("loggedUser")) || {tutorPrescricao: 1}

		this._centerCostApi.getAll({user: user.tutorPrescricao, array: element}).subscribe((data) => {
			if (data != null && data.length > 0) {
				data.forEach((item, i) => {
					let arrayHeader = [];
					item.columns.forEach((column, index) => {
						arrayHeader.push({
							columnValue: column.toLowerCase() + '_' + item.values[index],
						});

						if (index == (item.columns.length - 1)) {
							zone.push(arrayHeader);
						}
					});

					if (this.zoneList.length > 0) {
						this.isLoadingSelect = false;
								
						// Mark for check
						this._changeDetectorRef.markForCheck();
					} else {
						if (i == (data.length - 1)) {
							this.zoneList.push(
								{
									value: 0,
									label: "Todos"
								}
							)
							zone.forEach((item, z) => {
								this.zoneList.push(
									{
										value: item[0].columnValue.substring(item[0].columnValue.indexOf("_")  + 1),
										label: item[1].columnValue.substring(item[1].columnValue.indexOf("_")  + 1)
									}
								)

								if (z == (zone.length - 1)) {
									this.isLoadingSelect = false;
									
									// Mark for check
									this._changeDetectorRef.markForCheck();
								}
							})
						}
					}
				});
			} else {
				this.isLoadingSelect = false;
								
				// Mark for check
				this._changeDetectorRef.markForCheck();
			}
		})
	}
 
	deleteDataClick(item) {}

	// -----------------------------------------------------------------------------------------------------
    // @ All Util Methods
    // -----------------------------------------------------------------------------------------------------


	/**
	 * Function to transform list in object
	 * 
	 * @param arr 
	 * @returns 
	 */
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

	cssCreated() {
		this.style = 'grid-template-columns: ';

		this.heardsSearch.forEach((e, index) => {
			this.style += 'auto ';
			if (index === this.heardsSearch.length - 1) {
				this.style += '72px 40px';
			}
		});
	}

	setBackground(color) {
		switch(color.replaceAll(" ", "")) {
			case '99' : {
				return 'transparent';
			}
			case '0' : {
				return 'transparent';
			}
			case '1' : {
				return 'red';
			}
			case '2' : {
				return 'yellow';
			}
			case '3' : {
				return 'green';
			}
			case '4' : {
				return 'blue';
			}
			case '5' : {
				return 'orange';
			}
		}
	}

	getValueStatus(column) {
		if (column['anamnese']) {
			if (+column['anamnese'] > 0) {
				return 'blue';
			}
		}

		if (column['evolucao']) {
			if (+column['evolucao'] > 0) {
				return 'green';
			}
		}

		if (column['insumo']) {
			if (+column['insumo'] > 0) {
				return 'orange';
			}
		}

		if (column['urgencia']) {
			if (+column['urgencia'] > 0) {
				return 'red';
			}
		}

		if (column['radiologia']) {
			if (+column['radiologia'] > 0) {
				return 'gray';
			}
		}

		if (column['fisioterapia']) {
			if (+column['fisioterapia'] > 0) {
				return 'lightblue';
			}
		}

		if (column['endoscopiadigestiva']) {
			if (+column['endoscopiadigestiva'] > 0) {
				return 'lightgreen';
			}
		}

		if (column['conferido']) {
			if (+column['conferido'] > 0) {
				return 'lightorange';
			}
		}

		return false;
	}

	setBackgroundBlock(item, color) {
		if(item == color.toLowerCase()) {
			if (+item < 1) {
				return 'blue'
			}
		}
	}

	/**
     * Track by function for ngFor loops
     *
     * @param index
     * @param item
     */
    trackByFn(index: number, item: any): any
    {
        return item.id || index;
    }

	/**
	 * Funcition to throwError
	 * 
	 * @param err 
	 */
	throwError(err) {
		this._toastr.error(err.error, 'Error', {
			timeOut: 3000,
			closeButton: true,
			progressBar: true,
		  });
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
}
