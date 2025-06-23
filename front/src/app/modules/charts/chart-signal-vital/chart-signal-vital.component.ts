import { NgIf, TitleCasePipe, DatePipe, NgStyle, JsonPipe, NgFor, NgTemplateOutlet, NgClass, AsyncPipe, CurrencyPipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
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
import { ChartsApi } from 'app/app-api/charts/charts-api';
import { EqualsPipe } from 'app/modules/builder/builder-screen/util/equals.pipe';
import { DifeAgePipe } from 'app/modules/hospital/nursing/attendence/utils/pipe/diffAge.pipe';
import { LocalizedDatePipe } from 'app/modules/hospital/pep/evolution/util/fomated.pipe';
import { DateTime } from 'luxon';
import { NgApexchartsModule } from 'ng-apexcharts';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-chart-signal-vital',
    templateUrl: './chart-signal-vital.component.html',
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: fuseAnimations,
    standalone: true,
    imports: [
        NgIf,
        NgApexchartsModule,
        MatProgressSpinnerModule,
        MatDatepickerModule,
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
        TranslocoModule
    ],
    styles: [
        /* language=SCSS */
        `	
    .min-w-12 {
      min-width: 10rem !important;
    }

    .min-w-80 {
      min-width: 15rem !important;
    }
  `,
    ]
})
export class ChartSignalVitalComponent implements OnInit {
  @Input() valuePreviewScreen: any;
  @Input() componentScreen: any;

  previewScreenObj;

  chartGithubIssues: any = {};

  afeicaoList = [
    { id: 'PADPAS', value: 'P.a. Sistólica e Diastólica [Mmhg]' },
    { id: 'FC', value: 'Frequência Cardíaca [Bpm]' },
    { id: 'FR', value: 'Frequência Respiratória[irpm]' },
    { id: 'TC', value: 'Tempreatura ºC' },
    { id: 'SAT_O2', value: 'Saturação O2 (%)'},
  ];

  zoneInputControl = 'PADPAS';

  dayStartInputControl = DateTime.now().plus({ days: -7 })
  dayEndInputControl = DateTime.now();
  datePipe: DatePipe = new DatePipe('pt-BR');

  clickEventSubscription: Subscription;

  isLoading = true;

  labelsTitle = [];
  chartData = [];
  chartDataSecond = []

  labelTitle = "Dados";
  labelTitleSecond = "Dados"

  /**
   * Constructor
   */
  constructor(private _changeDetectorRef: ChangeDetectorRef,
    private transSrv: TranslocoService,
    private _toastr: ToastrService,
    private _chartsApi: ChartsApi) {
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  /**
   * On init
   */
  ngOnInit(): void {
    this.isLoading = true;
    this.previewScreenObj = this.valuePreviewScreen;
    console.log("tela anterior ", this.valuePreviewScreen)
    console.log("tela anterior ", this.componentScreen)

    // Mark for check
    this._changeDetectorRef.markForCheck();
    this.getValuesChart();
  }

  getValuesChart() {
    const start = this.datePipe.transform(this.dayStartInputControl.toISODate(), 'yyyy-MM-dd');
    const end = this.datePipe.transform(this.dayEndInputControl.toISODate(), 'yyyy-MM-dd');

    let where = ` AND REGISTRO = ${this.previewScreenObj['atendimento']} AND TIPO = ${this.previewScreenObj['idtipoatendimento']}`

    let dtaRange = ` DATA >= '${start}' AND DATA <= '${end}'` + where;

    let col = null
    if (this.zoneInputControl == 'PADPAS') col = 'PAD, PAS'

    this._chartsApi.getAll('charts/signal/vital', { tableName: col != null ?  col : this.zoneInputControl, dateRange: dtaRange })
      .subscribe((res) => {
        if (res?.length > 0) {
          res.forEach((element, index) => {
            this.labelsTitle.push(this.datePipe.transform(element.values[0], 'yyyy-MM-dd HH:mm'));
            this.chartData.push(element.values[1]);

            if (this.zoneInputControl == 'PADPAS') {
              this.chartDataSecond.push(element.values[2])
            }

            if (index == (res.length - 1)) {
              // Mark for check
              this._changeDetectorRef.markForCheck();
              this._prepareChartData();
            }
          });
        } else {
          this.labelsTitle = [],
          this.chartData = [];
          this.isLoading = false;

          // Mark for check
          this._changeDetectorRef.markForCheck();
          this._prepareChartData();
        }
      }, (err) => {
        this.throwError(err);
        
        
        this.labelsTitle = [],
        this.chartData = [];
        this.isLoading = false;

        // Mark for check
        this._changeDetectorRef.markForCheck();
      })
  }

  /**
   * Prepare the chart data from the data
   *
   * @private
   */
  private _prepareChartData(): void {
    let data;
    if (this.zoneInputControl == 'PADPAS') {
      data = [
        {
          name: 'Pad',
          type: 'line',
          data: this.chartData,
        },
        {
          name: 'Pas',
          type: 'line',
          data: this.chartDataSecond
        }
      ]
    } else {
      data = [
        {
          name: this.zoneInputControl,
          type: 'line',
          data: this.chartData,
        }
      ]
    }

    // Github issues
    this.chartGithubIssues = {
      chart: {
        fontFamily: 'inherit',
        foreColor: 'inherit',
        height: '100%',
        type: 'line',
        toolbar: {
          show: false,
        },
        zoom: {
          enabled: false,
        },
      },
      colors: ['#64748B', '#94A3B8'],
      dataLabels: {
        enabled: true,
        enabledOnSeries: [0],
        background: {
          borderWidth: 0,
        },
      },
      grid: {
        borderColor: 'var(--fuse-border)',
      },
      labels: this.labelsTitle,
      legend: {
        show: false,
      },
      plotOptions: {
        bar: {
          columnWidth: '40%',
        },
      },
      series: {
        'chart': data
      },
      states: {
        hover: {
          filter: {
            type: 'darken',
            value: 0.75,
          },
        },
      },
      stroke: {
        width: [3, 0],
      },
      tooltip: {
        followCursor: true,
        theme: 'dark',
      },
      xaxis: {
        axisBorder: {
          show: false,
        },
        axisTicks: {
          color: 'var(--fuse-border)',
        },
        labels: {
          offsetX: 2,
          style: {
            colors: 'var(--fuse-text-secondary)',
          },
        },
        tooltip: {
          enabled: false,
        },
      },
      yaxis: {
        labels: {
          offsetY: -18,
          style: {
            colors: 'var(--fuse-text-secondary)',
          },
        },
      },
    };

    this.isLoading = false;
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
