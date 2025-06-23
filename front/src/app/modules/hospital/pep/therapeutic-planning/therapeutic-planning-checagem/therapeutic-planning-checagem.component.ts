import { TitleCasePipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogActions, MatDialogContent, MatDialogClose } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterModule } from '@angular/router';
import { fuseAnimations } from '@fuse/animations';
import { TranslocoModule } from '@ngneat/transloco';
import { DynamicModal } from 'app/core/utils/dynamic-modal/utils/model/dynamic-modal.model';
import { DateTime } from 'luxon';
import { DIALOG_DATA } from '@angular/cdk/dialog';
import { TherapeuticPlanningModel } from '../utils/model/therapeutic-planning.model';

@Component({
    selector: 'app-therapeutic-planning-checagem',
    templateUrl: './therapeutic-planning-checagem.component.html',
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: fuseAnimations,
	standalone: true,
    imports: [
        TitleCasePipe,
        MatDialogActions,
        MatDialogContent,
        MatDialogClose,
        MatDatepickerModule,
        MatIconModule,
        MatInputModule,
        FormsModule,
        MatButtonModule,
        MatFormFieldModule,
        MatProgressSpinnerModule,
        RouterModule,
        MatProgressBarModule,
        TranslocoModule,
    ],
    styles: [
        /* language=SCSS */
        `	
			@screen sm {
				.check-modal-dialog {
					max-width: 90vw !important;
				}
			}

			@screen md {
				.check-modal-dialog {
					max-width: 95vw !important;
				}
			}

			@screen lg {
				.check-modal-dialog {
					max-width: 98vw !important;
				}
			}

			.h-form-20 {
				height: 10rem !important;
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
export class TherapeuticPlanningChecagemComponent implements OnInit {

  headerInfo: DynamicModal;
	dateToday = new Date();
	hoursNow;

  therapeutic_data: TherapeuticPlanningModel = {};

  datePipe: DatePipe = new DatePipe('pt-BR');
  dayInputControl = DateTime.now();
  hourInputControl = DateTime.now().hour;

  obsInputControl: "";
  
  constructor(@Inject(DIALOG_DATA) public data: { item: any, therapeutic_data: any }) {}

  ngOnInit(): void {  
		this.hoursNow = +DateTime.now().hour.toString().substring(0,2);
		this.headerInfo = this.data.item;
		
		this.therapeutic_data = this.data.therapeutic_data;
		this.therapeutic_data.datas = [];
	}

}
