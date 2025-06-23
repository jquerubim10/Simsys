import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { DatePipe, NgFor, NgIf, TitleCasePipe } from '@angular/common';
import { Evolution } from './model/evolution.types';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
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
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { UtilApi } from 'app/app-api/default/api-util';
import { DateTime } from 'luxon';
import { catchError, finalize, map, of, Subscription } from 'rxjs';
import { LocalizedDatePipe } from './util/fomated.pipe';
import { HistoryClickUpdate } from 'app/core/utils/dynamic-modal/utils/service/history-clcik-update.service';
import { SignService } from 'app/core/utils/sign-form/service/sign.service';
import { EvolutionModel } from './model/evolution.model';
import { MatDialog } from '@angular/material/dialog';
import { PreviewComponent } from 'app/core/utils/preview/preview.component';
import { PreSaveComponent } from 'app/core/utils/pre-save/pre-save.component';
import { ScreenBuilder } from 'app/modules/admin/screen/model/screen.model';
import { ToastrService } from 'ngx-toastr';
import { EvolutionRecord, TutorApiResponse } from './util/evolution';

@Component({
    selector: 'app-evolution',
    templateUrl: './evolution.component.html',
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
			.min-w-12 {
				min-width: 10rem !important;
			}

			.min-w-80 {
				min-width: 13rem !important;
			}
		`,
    ]
})
export class EvolutionComponent implements OnInit {
  
	@Input() item: any;
	@Input() builderScreenObject: any;
	
	activities: any[];
	tutorsList: any[] = [];
	

	reg = 0;
	tutor = 1;
	date = '';
	age;
	zoneInputControl = 0;

	table_name_screen;

	screenTitle = '';
	itemReplce = '';

	isLoading = true;
	isLoadingDownload = false;
	isLoadingTutor = false;


	loggedUserObject: any = {};

	dayStartInputControl = DateTime.now();
	dayEndInputControl = DateTime.now();
	
	datePipe: DatePipe = new DatePipe('pt-BR');

	clickEventSubscription: Subscription;

  	/**
	 * Constructor
	 */
	constructor(private _changeDetectorRef: ChangeDetectorRef,
		private transSrv: TranslocoService, public _signService: SignService,
		public _dialog: MatDialog, private _toastr: ToastrService,
		private _utilApi: UtilApi, private _updateHistory: HistoryClickUpdate) {

    this.clickEventSubscription = this._updateHistory.getClickEvent().subscribe(() => {
      this.getTutors();
    })
  }

  // -----------------------------------------------------------------------------------------------------
	// @ Lifecycle hooks
	// -----------------------------------------------------------------------------------------------------

	/**
	 * On init
	 */
	ngOnInit(): void {
		this.isLoading = true;
		this.isLoadingTutor = true;
		// Mark for check
		this._changeDetectorRef.markForCheck();

		if (this.item['atedimento']) {
			this.reg = this.item['atedimento'];
		}

		this.activities = [];

		let user = JSON.parse(localStorage.getItem("userScreen")) || JSON.parse(localStorage.getItem("loggedUser"));
		this.loggedUserObject = JSON.parse(localStorage.getItem("loggedUser"));

		this.zoneInputControl = user?.tutorPrescricao || 1;

		this.getTutors();
	}

  	getTutors(): void {
		this.isLoadingTutor = true;
		
		this._utilApi.getTutor().pipe(
			map((tutorList: TutorApiResponse[]) => {
				// Transforma os dados antes de usar
				return tutorList.map(item => ({
					id: +item.values[0],
					value: item.values[1]
				}));
			}),
			catchError(error => {
				console.error('Erro ao carregar tutores:', error);
				return of([]); // Retorna array vazio em caso de erro
			}),
				finalize(() => {
					this.isLoadingTutor = false;
					this._changeDetectorRef.markForCheck();
				})
			).subscribe({
				next: (formattedTutors) => {
				this.tutorsList = [{ id: 0, value: "Todos" }, ...formattedTutors];
				this.validateHistortyScreen();
			}
		});
	}

	validateHistortyScreen() {
		let item$: ScreenBuilder;
		if (!this.builderScreenObject) {
			item$ = new ScreenBuilder(JSON.parse(localStorage.getItem("opened_screen")))
		} else {
			item$ = new ScreenBuilder(this.builderScreenObject);
		}

		if (item$.typeTab == 'ev_hist') {
			this.getEvolutionStatus("EV");
		} 

		if (item$.typeTab == 'ev_anamnese') {
			this.getEvolutionStatus("ANAMNESE");
			this.screenTitle = "Anamnese";
			this.itemReplce = "Anamnese:";
		}
	}

	getEvolutionStatus(type: string, inScreen?: boolean): void {
		// Estado de loading
		if (inScreen) {
			this.isLoading = true;
		}

		this.activities = [];
		this._changeDetectorRef.markForCheck();

		// Formatação de datas de forma segura
		const start = this.datePipe.transform(this.dayStartInputControl.toISODate(), 'yyyy-MM-dd');
		const end = this.datePipe.transform(this.dayEndInputControl.toISODate(), 'yyyy-MM-dd');

		// Construção segura da cláusula WHERE
		const whereParams = {
			registro: this.reg,
			tipo: parseInt(this.item['idtipoatendimento']) || 1, // Default to 1 if not provided
			dataInicio: start,
			dataFim: end
		};

		let whereClause = `A.REGISTRO = ${whereParams.registro} AND A.TIPO = ${whereParams.tipo} AND cast(a.data as date) >= '${whereParams.dataInicio}' AND cast(a.data as date) <= '${whereParams.dataFim}'`;

		if (this.zoneInputControl != 0) {
			whereClause += ` AND A.TUTOR = ${this.zoneInputControl}`;
		}

		this._utilApi.dynamicQuery('treatment/evolution', { selectOne: type, whereValue: whereClause })
		.pipe(
			finalize(() => {
				this.isLoading = false;
				this._changeDetectorRef.markForCheck();
			})
		).subscribe({
			next: (response) => {
				if (!response?.length) {
					this.activities = [];
					return;
				}

				this.activities = this.transformEvolutionData(response);
				this.populateArrayObjectEvolution(this.activities, type);
			},
			error: (err) => {
				console.error('Error fetching evolution status', err);
				this.activities = [];
				this.isLoading = false;
				this._changeDetectorRef.markForCheck();
			}
		});
	}

	private transformEvolutionData(data: any[]): EvolutionRecord[] {
		return data.map(item => {
			const record: Record<string, any> = {};
			
			item.columns.forEach((column: string, index: number) => {
				// Converte o nome da coluna para snake_case (opcional)
				const key = column.toLowerCase() as keyof EvolutionRecord;
				record[key] = item.values[index];
			});
			
			return record as EvolutionRecord;
		});
	}
	
	populateArrayObjectEvolution(arrays: any[], type): void {
		const now = DateTime.now();
		const titleCasePipe = new TitleCasePipe();

		this.activities = arrays.map(item => ({
			id: Math.random().toString(),
			icon: 'heroicons_solid:document-text',
			description: `	${type == "EV" ? `${this.transSrv.translate('evolutionH', {}, this.transSrv.getActiveLang())} : ${item.evolucao}` 
										   : `Anamnese: ${item.evolucao}`} <br>

							${item?.tutor ? `${titleCasePipe.transform(item.tutor)} : ${item.nome_profissional}<br>` : ''}
							${this.transSrv.translate('concelho', {}, this.transSrv.getActiveLang())} : ${item.conselho}<br>
							${item?.nome_autorizador ? `${this.transSrv.translate('authorization', {}, this.transSrv.getActiveLang())}<br>
							${this.transSrv.translate('medic', {}, this.transSrv.getActiveLang())} : ${item.nome_autorizador}<br>
							${this.transSrv.translate('concelho', {}, this.transSrv.getActiveLang())} : ${item.conselho_autorizador}<br>` : ''}<br>`,
			date: now.minus({ milliseconds: now.diff(DateTime.fromISO(item.data)).milliseconds }).toISO(),
			datef: item.data,
			hours: item.hora,
			extraContent: `<div>${item.evolucao_txt}</div>`,
			itemSigned: item?.signed_docs || null,
			user_authority: parseInt(item?.usuario_autorizador) || null,
			evolution_id: parseInt(item.evolucao),
		})).filter((v, i, a) => a.findIndex(t => t.evolution_id === v.evolution_id) === i);

		this.isLoading = false;
		this._changeDetectorRef.markForCheck();
	}

	// -----------------------------------------------------------------------------------------------------
	// @ Public methods
	// -----------------------------------------------------------------------------------------------------

	/**
	 * Returns whether the given dates are different days
	 *
	 * @param current
	 * @param compare
	 */
	isSameDay(current: string, compare: string): boolean {
		return DateTime.fromISO(current).hasSame(
			DateTime.fromISO(compare),
			'day'
		);
	}

	/**
	 * Get the relative format of the given date
	 *
	 * @param date
	 */
	getRelativeFormat(date: string): string {
		return DateTime.fromISO(date).toRelativeCalendar();
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

	getPdfPreviewDownload(id) {
		this.isLoadingDownload = true;

		this._signService.downloadPDF(parseInt(id)).subscribe((res: any) => {
			let blob = new Blob([res.body], {type: "application/pdf"})
			let downloadLink = window.URL.createObjectURL(blob);

			this.isLoadingDownload = false;

			// Mark for check
			this._changeDetectorRef.markForCheck();

			window.open(downloadLink);
		}, (error) => {
			console.error("Error downloading PDF:", error);
		});
	}

	getBuilderFieldByDefault(id_evolution, user_logged_system, builderScreenObject) {
		let item$: any = {};
		let variableArray = [];

		if (!builderScreenObject) {
			item$ = new ScreenBuilder(JSON.parse(localStorage.getItem("opened_screen")))
		} else {
			item$ = new ScreenBuilder(builderScreenObject);
		}
		
		this._utilApi
			.getAll( `builder/field/${item$.id}/identity`)
			.subscribe((field_list) => {
				if (field_list.content.length > 0) {
					field_list.content.forEach((field: any, index) => {
						variableArray.push(field)

						if (index == (field_list.content.length - 1)) {
							this.table_name_screen = item$.tableName;
							this.openPreviewSignature(id_evolution, user_logged_system, JSON.stringify(variableArray));
						}
					});
				} else {
					this.throwError("Sem campos identity")
				}
			}, (error) => {
				this.throwError(error.error)
			});
	}

	openPreviewSignature(id_evolution, user_logged_system, screen_builder_indetity) {
		const dialogPreview = this._dialog.open(PreviewComponent, {
			height: '90vh',
			width: '80vw',
			panelClass: 'my-dialog-preview',
			data: {
				response: id_evolution,
				loggedScreen: user_logged_system,
				screenBuilderIdentity: screen_builder_indetity,
				screenOrigin: "history",
				table_name: this.table_name_screen
			}
		});
	
		dialogPreview.afterClosed().subscribe(result => {
			if (result) {
				localStorage.removeItem("userScreen")
				this.validateHistortyScreen();
			}
		});
	}

	openPreSaveDialog(id_evolution) {
		const preSaveDialog = this._dialog.open(PreSaveComponent, {
			height: '60vh',
			width: '40vw',
			panelClass: 'my-dialog-preview',
			data: {}
		});
	
		preSaveDialog.afterClosed().subscribe(result => {
			if (result) {
				let user_logged_system = JSON.parse(localStorage.getItem("userScreen"));
				this.getBuilderFieldByDefault(parseInt(id_evolution), user_logged_system, this.builderScreenObject)
			}
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
	
}
