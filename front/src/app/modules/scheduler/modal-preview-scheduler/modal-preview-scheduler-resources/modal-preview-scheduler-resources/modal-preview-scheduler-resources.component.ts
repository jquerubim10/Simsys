import { DIALOG_DATA } from '@angular/cdk/dialog';
import { NgIf, CommonModule, TitleCasePipe, DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOptionModule, MatRippleModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule, MatDialogActions, MatDialogContent, MatDialogClose, MatDialogTitle, MatDialog } from '@angular/material/dialog';
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
import { SchedulerApi } from 'app/modules/scheduler/api/api';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-modal-preview-scheduler-resources',
  templateUrl: './modal-preview-scheduler-resources.component.html',
  styles: [
      /* language=SCSS */
      `
          .inventory-grid {
				    grid-template-columns: 5vw 5vw auto;
			    }

          .ps {
            padding: 1rem 0px !important;
          }

          .bg-color-header {
				    background-color: #61b6f3;
    			  color: #fff;
			    }

          @screen sm {
            .modal-scheduler-resources-atendence {
              max-width: 50vw !important;
              width: 50vw !important,
            }
          }
    
          @screen md {
            .modal-scheduler-resources-atendence {
              max-width: 50vw !important;
              width: 50vw !important,
            }
          }
    
          @screen lg {
            .modal-scheduler-resources-atendence {
              max-width: 45vw !important;
              width: 45vw !important,
            }
          }

          @screen xl {
            .modal-scheduler-resources-atendence {
              max-width: 30vw !important;
              width: 30vw !important,
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
export class ModalPreviewSchedulerResourcesComponent implements OnInit{

  scheduler_list: any[] = [];

  constructor(
      @Inject(DIALOG_DATA) public data: { scheduler_id: any },
      @Inject(ChangeDetectorRef) private _changeDetectorRef: ChangeDetectorRef,
      public dialog: MatDialog, private _schedulerApi: SchedulerApi,
      private _toastr                 : ToastrService,
    ) { }


  ngOnInit(): void {
    this.getAllRecursoByAgendamento();
  }


  getAllRecursoByAgendamento() {
		this._schedulerApi.getAllResoucesAtendimento(this.data.scheduler_id)
			.pipe( finalize(() => {
				console.log("Sucesso!!");
			})).subscribe((response) => {
				
			if (response?.content.length > 0) {
				response.content.forEach((item) => {
					this.scheduler_list.push({ 
						tipo: item[0], 
						name_resource: item[2], 
						recursoID: item[3], 
						quantidade: item[4]
					});
				});
			}

			this._changeDetectorRef.markForCheck();
		});
	}
}
