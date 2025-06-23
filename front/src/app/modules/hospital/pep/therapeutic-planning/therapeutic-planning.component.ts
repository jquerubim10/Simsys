
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { DatePipe, NgFor, NgIf, TitleCasePipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule, UntypedFormControl } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOptionModule, MatRippleModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
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
import { RouterModule } from '@angular/router';
import { fuseAnimations } from '@fuse/animations';
import { TranslocoModule } from '@ngneat/transloco';
import { DifeAgePipe } from '../../nursing/attendence/utils/pipe/diffAge.pipe';
import { LocalizedDatePipe } from '../evolution/util/fomated.pipe';
import { DynamicModal } from 'app/core/utils/dynamic-modal/utils/model/dynamic-modal.model';
import { MatDialog, MatDialogActions, MatDialogClose, MatDialogContent } from '@angular/material/dialog';
import { DIALOG_DATA } from '@angular/cdk/dialog';
import { TherapeuticPlanningModel } from './utils/model/therapeutic-planning.model';
import { DateTime } from 'luxon';
import { TherapeuticPlanningApi } from './utils/api/therapeutic-planning.service';
import { FaqTherapeuticPlanningComponent } from './faq-therapeutic-planning/faq-therapeutic-planning.component';
import { TherapeuticPlanningChecagemComponent } from './therapeutic-planning-checagem/therapeutic-planning-checagem.component';

@Component({
    selector: 'app-therapeutic-planning',
    templateUrl: './therapeutic-planning.component.html',
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: fuseAnimations,
	standalone: true,
    imports: [
        NgIf,
        TitleCasePipe,
        MatDialogActions,
        MatDialogContent,
        MatDialogClose,
        MatProgressSpinnerModule,
        MatDatepickerModule,
        MatTooltipModule,
        MatMenuModule,
        DifeAgePipe,
        DatePipe,
        LocalizedDatePipe,
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
    ],
    styles: [
        /* language=SCSS */
        `	
			@screen sm {
				.my-dialog {
					max-width: 90vw !important;
				}
			}

			@screen md {
				.my-dialog {
					max-width: 95vw !important;
				}
			}

			@screen lg {
				.my-dialog {
					max-width: 98vw !important;
				}
			}
			
			.min-w-12 {
				min-width: 10rem !important;
			}

			.min-w-80 {
				min-width: 13rem !important;
			}

			.check-item {
				border: 1px solid rgba(226,232,240, 1);
			}

			.py-mei {
				padding-top: 1.1rem !important;
    			padding-bottom: .75rem !important;
			}
			
			.py-w {
				height:3.6rem !important;
			}

			.min-w-3 {
				min-width: 3rem !important;
			}

			.inventory-grid {
				grid-template-columns: auto 40px;
			}

			.check-opt {
				background-color: white; 
				padding-left: 1.5rem !important; 
				padding-right: 1rem !important; 
				max-width: 3rem !important; 
				border: 1px solid rgb(169 179 193);
			}

			.alergias {
				text-transform: capitalize;
				color: white;
				border: 1px solid;
				width: 11rem;
				padding: .6rem;
				display: flex;
				justify-content: center;
				align-items: center;
				background-color: red;
				border-radius: 2rem;
			}
		`,
    ]
})
export class TherapeuticPlanningComponent implements OnInit {
	
	responseBody: TherapeuticPlanningModel[] = [];
	headerInfo: DynamicModal;
	dateToday;
	hoursNow;

	isLoading = true;
	loadingMedicine = false;

	dayInputControl = DateTime.now();
	dayInputControlForm: UntypedFormControl = new UntypedFormControl(DateTime.now());
	datePipe: DatePipe = new DatePipe('pt-BR');

	constructor(@Inject(DIALOG_DATA) public data: { item: any },
		private _therapeuticPlanningApi: TherapeuticPlanningApi,
		private _changeDetectorRef: ChangeDetectorRef,
		public dialog: MatDialog) {
		
	}

	ngOnInit(): void {
		this.dateToday = new Date();
		this.hoursNow = +DateTime.now().hour.toString().substring(0,2);
		this.headerInfo = new DynamicModal(this.data.item)


		console.log(this.hoursNow);
		// Subscribe to search input field value changes
		this.dayInputControlForm.valueChanges.subscribe((query) => {
			this.sql()
		});

		this.sql();
	}

	sql() {
		this.dateToday = this.dayInputControl.toISODate();

		// Mark for check
		this._changeDetectorRef.markForCheck();
		let wher = `WHERE A.REGISTRO = ${this.headerInfo.atendimento} AND A.DATA ='${this.datePipe.transform(this.dayInputControl.toISODate(), 'yyyy-MM-dd')}T00:00:00.000'`;
		let secondWhere = `WHERE C.REGISTRO = ${this.headerInfo.atendimento} AND C.DATA ='${this.datePipe.transform(this.dayInputControl.toISODate(), 'yyyy-MM-dd')}T00:00:00.000'`;
		this.responseBody = [];
		this._therapeuticPlanningApi.getAll({tpTreatment: this.headerInfo.tpatendimento, whereValue: wher, secondWhereValue: secondWhere})
			.subscribe(
				(response) => {
					if(response.length > 0) {
						response.forEach((item, i) => {
							let itemTranform = [];
							item.columns.forEach((column, index) => {
								itemTranform.push(
									{
										columnValue: column.toLowerCase() + '_' + item.values[index],
									}	
								);
		
								if (index == (item.columns.length - 1)) {
									this.responseBody.push( new TherapeuticPlanningModel(this.toObject(itemTranform)));
								}
							});
		
							if (i == (response.length - 1)) {
								this.isLoading = false;
								this.insertToGrid(this.responseBody);
								// Mark for check
								this._changeDetectorRef.markForCheck();
							}
						});
					} else {
						this.isLoading = false;
						// Mark for check
						this._changeDetectorRef.markForCheck();
					}
				}, (error) => {
					console.error(error);
				})
	}

	insertToGrid(list: TherapeuticPlanningModel[]) {
		let grid: any[] = [];

		grid = list.filter((value, index, self) =>
			index === self.findIndex((t) => (
			  t.prescricao === value.prescricao && t.procedimento === value.procedimento && t.sequencia === value.sequencia
			))
		)


		list.forEach((subData, sub) => {
			
			grid.forEach((item: TherapeuticPlanningModel, le) => {
				if(subData.prescricao == item.prescricao && subData.procedimento == item.procedimento && subData.sequencia === item.sequencia) {
					item.datas.push(subData)
				}
			})
			

			if (sub == (list.length - 1)) {
				this.responseBody = grid;
				console.log(this.responseBody)
			}
		})
	}

	checkDiv(number) {
		console.log(number);
	}

	validateMedication(itemObject: TherapeuticPlanningModel, number) {
		
		let varNumber = number < 10 ? `0${number}` : number;

		itemObject.datas.forEach((element: TherapeuticPlanningModel) => {
			let varProductHour = element.hora.substring(0, 2);

			this.loadingMedicine = true;

			// Mark for check
			this._changeDetectorRef.markForCheck();

			if (varProductHour == varNumber) {
				this.loadingMedicine = false;

				// Mark for check
				this._changeDetectorRef.markForCheck();

				`r ${element.quantidade}`;
			}
		});
		this.loadingMedicine = false;
		return "";
	}

	openCheckModal(item: TherapeuticPlanningModel) {
		const opened_check_modal = this.dialog.open(TherapeuticPlanningChecagemComponent, {
			height: '90vh',
			width: '90vw',
			panelClass: 'check-modal-dialog',
			data: {item: this.headerInfo, therapeutic_data: item }	
		});

		opened_check_modal.afterClosed().subscribe((result) => {
			this.sql();
		});
	}


	openFaqModal() {
		const opened_faq_modal = this.dialog.open(FaqTherapeuticPlanningComponent, {
			height: '30vh',
			width: '30vw',
			panelClass: 'faq-therapeutic-dialog',
			data: {}
		});

		opened_faq_modal.afterClosed().subscribe((result) => {
			console.log(`Faq dialog close result: ${result}`);
		});
	}

	// -----------------------------------------------------------------------------------------------------
    // @ All Util Methods
    // -----------------------------------------------------------------------------------------------------

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
			rv[name.substring(0, name.indexOf('_'))] = this.validateNullValue(name.substring(name.indexOf('_') + 1));
		}
			
		return rv;
	}

	validateNullValue(value) {
		if (value == "null" || value == "" || value == null) {
			return null
		}
		return value
	}
}
