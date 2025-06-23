import { ModalPreviewSchedulerResourcesComponent } from './modal-preview-scheduler-resources/modal-preview-scheduler-resources/modal-preview-scheduler-resources.component';
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
import { DateTime } from 'luxon';
import { BuilderFormComponent } from 'app/modules/builder/builder-form/builder-form.component';
import { BuilderComponent } from 'app/modules/builder/builder-screen/builder.component';
import { ChartSignalVitalComponent } from 'app/modules/charts/chart-signal-vital/chart-signal-vital.component';
import { ChatMonitoramentoComponent } from 'app/modules/charts/chat-monitoramento/chat-monitoramento.component';
import { DifeAgePipe } from 'app/modules/hospital/nursing/attendence/utils/pipe/diffAge.pipe';
import { EvolutionComponent } from 'app/modules/hospital/pep/evolution/evolution.component';
import { SchedulerApi } from '../api/api';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { ModalSchedulerAtendenceComponent } from '../modal-scheduler-atendence/modal-scheduler-atendence.component';
import { ModalRefreshService } from './utils/modal-refresh.service';

@Component({
  selector: 'app-modal-preview-scheduler',
  templateUrl: './modal-preview-scheduler.component.html',
  styles: [
    /* language=SCSS */
    `
        @screen sm {
          .modal-preview-scheduler {
            max-width: 45vw !important;
            width: 45vw !important,
          }

          .mat-mdc-dialog-actions {
            flex-wrap: nowrap !important;
          }
        }
  
        @screen md {
          .modal-preview-scheduler {
            max-width: 40vw !important;
            width: 40vw !important,
          }

          .mat-mdc-dialog-actions {
            flex-wrap: nowrap !important;
          }
        }
  
        @screen lg {
          .modal-preview-scheduler {
            max-width: 40vw !important;
            width: 40vw !important,
          }

          .mat-mdc-dialog-actions {
            flex-wrap: nowrap !important;
          }
        }

        @screen xl {
          .modal-preview-scheduler {
            max-width: 30vw !important;
            width: 30vw !important,
          }
        }

        .bg-green {
          background-color: #46b946 !important;
          color: #fff !important;
        }

        .bg-purple {
          background-color: purple !important;
          color: #fff !important;
        }

        .status-cicle {
          width: 10px;
          height: 10px;
          border-radius: 100%;
			  }

        .modal-preview-scheduler {
          .mat-mdc-dialog-container .mdc-dialog__surface {
            padding: 0 !important;
          }

          .modal-preview-scheduler-title {
            background-color: #5bb4ee;
            padding: 1.5rem !important;
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
export class ModalPreviewSchedulerComponent implements OnInit {

  preview_object: any;
  selected_status: any;
  doctorInputList: any = [];

  isLoading = true;
  scheduler_list: any[] = [];

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

  constructor(
    @Inject(DIALOG_DATA) public data: { preview_data: any },
    @Inject(ChangeDetectorRef) private _changeDetectorRef: ChangeDetectorRef,
    public dialog: MatDialog, private _schedulerApi: SchedulerApi,
    private _toastr                 : ToastrService,
    private _modalRefresh           : ModalRefreshService,
  ) { }

  ngOnInit(): void {
    this.isLoading = true;

    this.preview_object = JSON.parse(this.data.preview_data.object);

    this._schedulerApi.getAllDoctors().subscribe((data) => {
      this.doctorInputList = data.content;
      this.isLoading = false;

      // Mark for check
      this._changeDetectorRef.markForCheck();
    });

    //console.log('data ', this.preview_object);
  }

  getStatusColor(status) {
    return this.status_list.find((item) => item.value == status)?.color || '#FFF';
  }

  beautifyDate(date: string) {
    return DateTime.fromISO(date).toFormat("HH':'mm");
  }

  beautifyDoctorName(id: string) {
    return this.doctorInputList.find((e) => e.medico === id).nome;
  }

  handleChangeStatus() {
    this.preview_object.color = this.getStatusColor( this.preview_object.status);

    this._schedulerApi.handleUpdateStatus({ valueLong: this.preview_object.id, whereValue: this.preview_object.status, secondWhereValue: this.preview_object.color })
      .pipe( finalize(() => {
        this.throwSuccess("Status Alterado");
        })).subscribe((response) => { }, (erro) => { console.error(erro) });
  }

  handleEditScheduler() {

    this.dialog.getDialogById('scheduler_preview').close();

    const opened_edit_modal = this.dialog.open(ModalSchedulerAtendenceComponent, {
      height: '90vh',
      width: '90vw',
      id: 'scheduler_new',
      panelClass: 'modal-scheduler-atendence',
      data: { scheduler_item: JSON.stringify(this.preview_object), scheduler_type: 'scheduler_ready' }	
    });

    opened_edit_modal.afterClosed().subscribe((result) => { 

      this._modalRefresh.sendClickEvent("edit_scheduler");

      // Notify the change detector
      this._changeDetectorRef.markForCheck();
    });
  }

  handleDuplicatedScheduler() {

    this.dialog.getDialogById('scheduler_preview').close();

    const opened_duplicated_modal = this.dialog.open(ModalSchedulerAtendenceComponent, {
      height: '90vh',
      width: '90vw',
      id: 'scheduler_new',
      panelClass: 'modal-scheduler-atendence',
      data: { scheduler_item: JSON.stringify(this.preview_object), scheduler_type: 'scheduler_duplicated' }	
    });

    opened_duplicated_modal.afterClosed().subscribe((result) => { 

      this._modalRefresh.sendClickEvent("duplicated_scheduler");

      // Notify the change detector
      this._changeDetectorRef.markForCheck();
    });
  }

  handleResourcesScheduler() {
    const opened_resources_modal = this.dialog.open(ModalPreviewSchedulerResourcesComponent, {
      height: '90vh',
      width: '50vw',
      panelClass: 'modal-scheduler-resources-atendence',
      data: { scheduler_id: this.preview_object.id }	
    });

    opened_resources_modal.afterClosed().subscribe((result) => { 

      // Notify the change detector
      this._changeDetectorRef.markForCheck();
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
