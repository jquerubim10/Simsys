import { NgIf, TitleCasePipe, DatePipe, NgFor } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewEncapsulation } from '@angular/core';
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
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { CalendarEvent, CalendarEventAction, CalendarEventTimesChangedEvent, CalendarModule, CalendarUtils, CalendarView } from 'angular-calendar';
import { DateTime } from 'luxon';
import { ToastrService } from 'ngx-toastr';
import { EventColor } from 'calendar-utils';
import { subDays, startOfDay, addDays, endOfMonth, addHours, endOfDay, addMinutes, isSameDay, isSameMonth } from 'date-fns';
import { CalendarOptions } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid'
import { FullCalendarModule } from '@fullcalendar/angular';
import { Subject, Subscription } from 'rxjs';
import { ModalSchedulerAtendenceComponent } from '../modal-scheduler-atendence/modal-scheduler-atendence.component';
import { MatDialog } from '@angular/material/dialog';
import { SchedulerApi } from '../api/api';
import { ModalPreviewSchedulerComponent } from '../modal-preview-scheduler/modal-preview-scheduler.component';
import { ModalRefreshService } from '../modal-preview-scheduler/utils/modal-refresh.service';
import { ModalUtilsComponent } from '../utils/modal-utils/modal-utils.component';

@Component({
    selector: 'app-scheduler-atendence',
    templateUrl: './scheduler-atendence.component.html',
    styles: [
      /* language=SCSS */
      `

        .cal-month-view .cal-day-badge {
            background-color: #4f46e5;
            color: #fff;
            padding: 5px 9px;
            font-size: 14px;
            border-radius: 100%;
        }

        .button-color {
          background-color: #61b6f3 !important;
          color: #fff !important;
        }

        .mat-calendar-body-selected {
          background-color: #61b6f3 !important;
        }

        .cal-month-view .cal-cell-row .cal-cell:hover {
          background-color: #61b6f3 !important;
        }

        .cal-month-view .cal-open-day-events {
            background-color: #61b6f3;
            box-shadow: none !important;
        }

        .cal-current-time-marker {
          display: none;
        }

        .cal-month-view .cal-header .cal-cell {
          text-transform: capitalize;
        }

        .w-select-20-r {
          width: 20rem !important;
        }    

        .w-30-100 {
          width: 30vw;
        }

        @screen sm {
          grid-template-columns:  auto 120px 48px 80px 20rem 100px 15rem 10rem 150px 5rem;
        }

        @screen md {
          grid-template-columns: auto 120px 48px 80px 20rem 100px 15rem 10rem 150px 5rem;
        }

        @screen lg {
          grid-template-columns:  5rem 120px 48px 80px 20rem 100px 15rem 10rem 150px 5rem;
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
      CalendarModule,
      FullCalendarModule,
      MatTooltipModule,
      MatMenuModule,
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
export class SchedulerAtendenceComponent implements OnInit {
  
  startDate;
	dayInputControl = DateTime.now();
	datePipe: DatePipe = new DatePipe('pt-BR');

  zoneInputList: any = [];
  doctorInputList: any = [];

	doctorInputControl: UntypedFormControl = new UntypedFormControl();
	zoneInputControl: UntypedFormControl = new UntypedFormControl();

  clickEventSubscription: Subscription;

  isLoading = false;
  isLoadingZone = true;
  isLoadingDoctor = false;
  isLoadingSelect = true;

  view: CalendarView = CalendarView.Month;
  CalendarView = CalendarView;
  viewDate: Date = new Date();

  events: CalendarEvent[] = [];

  activeDayIsOpen: boolean = true

  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin],
    initialView: 'dayGridMonth',
    weekends: false,
    slotDuration: '00:15',
    businessHours: [ // specify an array instead
      {
        daysOfWeek: [ 1, 2, 3, 4, 5 ], // Monday, Tuesday, Wednesday
        startTime: '08:00', // 8am
        endTime: '18:00' // 6pm
      },
      {
        daysOfWeek: [ 6, 0 ], // Sunday, 
        startTime: '08:00', // 8am
        endTime: '13:00' // 6pm
      },
    ],
    events: []
  };

  /**
	 * Constructor
	 */
	constructor(
		private _changeDetectorRef: ChangeDetectorRef, public dialog: MatDialog,
    private _schedulerApi: SchedulerApi,
		private _toastr: ToastrService, 
    private translocoService: TranslocoService,
    private _modalClick: ModalRefreshService,
	) {
    this.clickEventSubscription = this._modalClick.getClickEvent().subscribe(() => {
			this.handleChangeScheduler(this.zoneInputControl.value, this.doctorInputControl.value);

      // Notify the change detector
      this._changeDetectorRef.detectChanges();
		})
  }
  
  ngOnInit(): void {
    this.startDate = this.datePipe.transform(new Date(), 'dd-MM-yy');
    
    // Subscribe to search input field value changes
		this.doctorInputControl.valueChanges.subscribe((query) => {
			this.handleChangeScheduler(this.zoneInputControl.value, query);
		});

		// Subscribe to search input field value changes
		this.zoneInputControl.valueChanges.subscribe((query) => {
      this.doctorInputControl.setValue("");
      this.isLoadingDoctor = true;

      // Notify the change detector
      this._changeDetectorRef.detectChanges();
			this.handleFindDoctorByZone(query);
		});

    this._schedulerApi.getAllSetor().subscribe((data) => {
      this.zoneInputList = data.content;

      this.isLoadingZone = false;
      // Notify the change detector
      this._changeDetectorRef.detectChanges();
    });
  }

  handleFindDoctorByZone(value) {
    this._schedulerApi.getAllDoctorsByCenter(value).subscribe((data) => { 
      this.doctorInputList = data.content;

      this.isLoadingDoctor = false;

      // Notify the change detector
      this._changeDetectorRef.detectChanges();
    }, error => {
      console.log(error);
      this.isLoadingDoctor = false;

      // Notify the change detector
      this._changeDetectorRef.detectChanges();
    });
  }

  handleChangeScheduler(value_setor, value_doctor) {
    this.isLoading = true;

    // Notify the change detector
    this._changeDetectorRef.detectChanges();
    this.events = [];
    this._schedulerApi.getAllSchedulerFiltered(value_setor, value_doctor).subscribe((data) => {

      if ( data.content?.length < 1) {
        this.isLoading = false;

        // Notify the change detector
        this._changeDetectorRef.detectChanges();
      } else {
        data.content.forEach((item, index) => {

          let data_consulta_inicio = `${DateTime.fromISO(item.horaInicio).toFormat("HH':'mm")}`;
          let data_consulta_fim = `${DateTime.fromISO(item.horaTermino).toFormat("HH':'mm")}`;

          this.events.push({
            title: `${data_consulta_inicio} - ${data_consulta_fim}  ${item.paciente != "" ? item.paciente : item.title}  <br>  <span> ${item.observacoes != "" ? " Obs: " + item.observacoes : "" } </span>  <br>`,
            start: new Date(item.horaInicio),
            end: new Date(item.horaTermino),
            color: {
              primary: item.color,
              secondary: '#4f46e5',
              secondaryText: "#fff"
            },
            draggable: false,
            allDay: false,
            object: JSON.stringify(item),
            resizable: {
              beforeStart: false,
              afterEnd: false
            },
          });

          if (index == (data.content.length - 1)) {

            this.isLoading = false;

            // Notify the change detector
            this._changeDetectorRef.detectChanges();
          }
        });
      }
    }, error => {
      console.log(error);
      // Notify the change detector
      this._changeDetectorRef.detectChanges();
    });
  }

  handleDayMonthYearView(view: CalendarView) {
    this.view = view;
  }

  handleDateChange(event) {
    let dateFormat = new Date(event);

    let e: CalendarEvent[] = this.events.filter(t => isSameDay(dateFormat, t.start));

    this.dayClicked({date: dateFormat,  events: e})
  }

  dayClicked({ date, events }: { date: Date; events: CalendarEvent[] }): void {
    if (events.length === 0) {
      this.activeDayIsOpen = false;
    } else {
      this.activeDayIsOpen = true;
    }

    this.viewDate = date;
  }

  handleOpenedNewScheduler() {
		const opened_check_modal = this.dialog.open(ModalSchedulerAtendenceComponent, {
			height: '90vh',
			width: '90vw',
      id: 'scheduler_new',
			panelClass: 'modal-scheduler-atendence',
			data: { scheduler_item: {}, scheduler_type: 'scheduler_new' }	
		});

		opened_check_modal.afterClosed().subscribe((result) => { 

      this.handleChangeScheduler(this.zoneInputControl.value, this.doctorInputControl.value);

      // Notify the change detector
      this._changeDetectorRef.detectChanges();
    });
	}

  handlePreviewScheduler(item) {
		const preview_check_modal = this.dialog.open(ModalPreviewSchedulerComponent, {
			height: '80vh',
			width: '25vw',
      id: 'scheduler_preview',
			panelClass: 'modal-preview-scheduler',
			data: { preview_data: item }	
		});

		preview_check_modal.afterClosed().subscribe((result) => { 

      this.handleChangeScheduler(this.zoneInputControl.value, this.doctorInputControl.value);

      // Notify the change detector
      this._changeDetectorRef.detectChanges();
    });
	}

  handleOpenModalUtils() {
    const modal_utils = this.dialog.open(ModalUtilsComponent, {
      height: '90vh',
      width: '90vw',
      id: 'modal_utils',
      panelClass: 'modal-scheduler-atendence',
      data: { modal_type: 'modal_maps' }	
    });

    modal_utils.afterClosed().subscribe((result) => { 
      this.handleChangeScheduler(this.zoneInputControl.value, this.doctorInputControl.value);
      this._changeDetectorRef.detectChanges();
    });
  }
}
