import { DIALOG_DATA } from '@angular/cdk/dialog';
import { CommonModule, DatePipe, NgIf, TitleCasePipe } from '@angular/common';
import { Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import {
	FormsModule,
	ReactiveFormsModule,
	UntypedFormBuilder,
	UntypedFormGroup,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOptionModule, MatRippleModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
	MatDialog,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogModule,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { fuseAnimations } from '@fuse/animations';
import { TranslocoModule } from '@ngneat/transloco';
import { BuilderFormComponent } from 'app/modules/builder/builder-form/builder-form.component';
import { BuilderComponent } from 'app/modules/builder/builder-screen/builder.component';
import { ChartSignalVitalComponent } from 'app/modules/charts/chart-signal-vital/chart-signal-vital.component';
import { ChatMonitoramentoComponent } from 'app/modules/charts/chat-monitoramento/chat-monitoramento.component';
import { DifeAgePipe } from 'app/modules/hospital/nursing/attendence/utils/pipe/diffAge.pipe';
import { EvolutionComponent } from 'app/modules/hospital/pep/evolution/evolution.component';
import { SignService } from '../sign-form/service/sign.service';
import { DynamicModal } from './utils/model/dynamic-modal.model';
import { DynamicListClick } from './utils/service/dynamic-list-click.service';
import { DynamicSaveClick } from './utils/service/dynamic-save-click.servive';
import { HistoryClickUpdate } from './utils/service/history-clcik-update.service';


@Component({
	selector: 'app-dynamic-modal',
	templateUrl: './dynamic-modal.component.html',
	styles: [
		/* language=SCSS */
		`
			@screen sm {
				.my-dialog {
					max-width: 90vw !important;
				}
			}

			.alergias {
				text-transform: capitalize;
				color: white;
				border: 1px solid;
				width: 11rem;
				padding: 0.6rem;
				display: flex;
				justify-content: center;
				align-items: center;
				background-color: red;
				border-radius: 2rem;
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
		`,
	],
	encapsulation: ViewEncapsulation.None,
	animations: fuseAnimations,
	standalone: true,
	imports: [
		NgIf,
		BuilderComponent,
		BuilderFormComponent,
		ChartSignalVitalComponent,
		ChatMonitoramentoComponent,
		EvolutionComponent,
		CommonModule,
		MatProgressSpinnerModule,
		MatDialogModule,
		MatButtonModule,
		MatDialogActions,
		MatDialogContent,
		MatDialogClose,
		TitleCasePipe,
		MatDatepickerModule,
		MatTooltipModule,
		MatMenuModule,
		DifeAgePipe,
		DatePipe,
		RouterModule,
		MatFormFieldModule,
		MatIconModule,
		MatInputModule,
		FormsModule,
		ReactiveFormsModule,
		MatSortModule,
		MatDialogTitle,
		MatPaginatorModule,
		MatSlideToggleModule,
		MatSelectModule,
		MatOptionModule,
		MatCheckboxModule,
		MatRippleModule,
		TranslocoModule,
		MatTabsModule,
	],
})
export class DynamicModalComponent implements OnInit {
	headerValid: boolean;
	hiddenSave: boolean = false;

	headerInfo: DynamicModal;

	item: any;
	previewScreen: any;

	compScreen: any;
	selectedProductForm: UntypedFormGroup;

	constructor(
		@Inject(DIALOG_DATA) public data: { comp: any; item: any },
		private _formBuilder: UntypedFormBuilder,
		private _dynamicClick: DynamicSaveClick,
		private _historyUpdate: HistoryClickUpdate,
		private _dynamicListClick: DynamicListClick,
		private _sign: SignService,
		public dialog: MatDialog
	) {}

	ngOnInit(): void {
		this.previewScreen = null;
		this.compScreen = null;
		this.selectedProductForm = this._formBuilder.group({});

		this.headerInfo = new DynamicModal(this.data.item);
		console.log("HEADER ", localStorage.getItem('opened_screen'));

		this.previewScreen = this.data.item;
		this.compScreen = this.data.comp;

		this.headerValid = true;

		localStorage.setItem('dynamicScreen', JSON.stringify(this.headerInfo));
	}

	clickOut() {
		this._dynamicClick.sendClickEvent(this.compScreen);
		//this._form.validatePreSave();
	}

	tabHistory(a: { index: number }) {
		if (a.index == 1) {
			this.hiddenSave = true;
			if (this.compScreen.listC) {
				this._dynamicListClick.sendClickEvent();
			} else {
				this._historyUpdate.sendClickEvent();
			}
		} else {
			this.hiddenSave = false;
		}
	}
}
