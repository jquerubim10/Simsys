import { DIALOG_DATA } from '@angular/cdk/dialog';
import { NgIf, CommonModule, TitleCasePipe, DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOptionModule, MatRippleModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogActions, MatDialogContent, MatDialogClose, MatDialogTitle, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatTooltipModule } from '@angular/material/tooltip';
import { fuseAnimations } from '@fuse/animations';
import { TranslocoModule } from '@ngneat/transloco';
import { ToastrService } from 'ngx-toastr';
import { SchedulerApi } from '../../api/api';
import { DateTime } from 'luxon';
import { PreviewPatternComponent } from 'app/core/utils/preview-pattern/preview-pattern.component';
import { uniq } from 'lodash';
import { ApiResponse, MapItem } from 'app/core/utils/preview-pattern/util/preview';

@Component({
  selector: 'modal-utils',
  templateUrl: './modal-utils.component.html',
  styles: [
      /* language=SCSS */
      `
          @screen sm {
            .modal_utils {
              max-width: 45vw !important;
              width: 45vw !important,
            }
          }
    
          @screen md {
            .modal_utils {
              max-width: 40vw !important;
              width: 40vw !important,
            }
          }
    
          @screen lg {
            .modal_utils {
              max-width: 30vw !important;
              width: 30vw !important,
            }
          }

          .status-cicle {
            width: 10px;
            height: 10px;
            border-radius: 100%;
          }
        `,
  ],
  encapsulation: ViewEncapsulation.None,
  animations: fuseAnimations,
  standalone: true,
  imports: [
    NgIf,
    CommonModule,
    MatButtonModule,
    MatDialogActions,
    MatDialogContent,
    MatDialogClose,
    MatDialogTitle,
    TitleCasePipe,
    MatDatepickerModule,
    MatTooltipModule,
    DatePipe,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    MatSortModule,
    MatSlideToggleModule,
    MatSelectModule,
    MatOptionModule,
    MatCheckboxModule,
    MatRippleModule,
    TranslocoModule
  ],
})
export class ModalUtilsComponent implements OnInit {

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

  sql_execute       : string = '';

  doctorInputList     : any = [];
  setorInputList      : any = [];
  listResultsMaps     : any = [];
  organizedResults    : any = [];
  convenioInputList   : any = [];

  analitico  : boolean = false;

  /* Get the current instant */
  now								: DateTime = DateTime.now();
  datePipe					: DatePipe = new DatePipe('pt-BR');
  mapsForm					: UntypedFormGroup;

  constructor(
      @Inject(DIALOG_DATA) public data: { modal_type: any },
      @Inject(ChangeDetectorRef) private _changeDetectorRef: ChangeDetectorRef,
      @Inject(UntypedFormBuilder) 
      private _formBuilder			      : UntypedFormBuilder, 
      public dialog                   : MatDialog, 
      private _schedulerApi           : SchedulerApi,
      private _toastr                 : ToastrService,
    ) { }
  
    ngOnInit(): void {

      // Create the form for maps
      this.mapsForm = this._formBuilder.group({
        medicoID: new FormControl(null),
        centroCustoID: new FormControl(null),
        convenioID: new FormControl(null),
        paciente: new FormControl(null, [Validators.minLength(4)]),
        typeResource: new FormControl(null),
        status: new FormControl(null),
        consulta: [false],
        exame: [false],
        cirurgia: [false],
        encaixeHorario: [false],
        horaInicio: [],
        horaTermino: []
      });

      this._schedulerApi.getAllSector().subscribe((data) => {
        this.setorInputList = data.content;
        this._changeDetectorRef.markForCheck();
      });

      this._schedulerApi.getAllDoctors().subscribe((data) => {
        this.doctorInputList = data.content;
        this._changeDetectorRef.markForCheck();
      });

      this._schedulerApi.getAllConvenio().subscribe((data) => {
        this.convenioInputList = data.content;
        this._changeDetectorRef.markForCheck();
      });

      this._changeDetectorRef.markForCheck();
    }

    async handleMaps(): Promise<void> {
      try {
        // 1. Resetar lista de resultados
        this.listResultsMaps = [];

        // 2. Construir a query SQL de forma segura
        this.buildSqlQuery();

        // 3. Chamar a API
        const response = await this._schedulerApi.getMaps(this.sql_execute).toPromise();

        if (!response?.length) {
          return;
        }

        // 4. Processar os itens da resposta
        this.processApiResponse(response);

        // 5. Organizar os resultados por centro de custo
        // this.organizeResultsByCenter();

        // 6. Chamar a função de preview
        this.handlePatternPreview();
      } catch (error) {
        console.error('Error in handleMaps:', error);
        // Tratar erro conforme necessário
      }
    }

    private buildSqlQuery(): void {
      const formValues = this.mapsForm.value;

      // Format dates
      const date_init = this.datePipe.transform(new Date(formValues.horaInicio), 'yyyy-MM-dd');
      const date_finish = this.datePipe.transform(new Date(formValues.horaTermino), 'yyyy-MM-dd');

      // Base query
      this.sql_execute = `AND CAST(A.HoraInicio AS DATE) >='${date_init}' AND CAST(A.HoraTermino AS DATE) <='${date_finish}'`;

      // Adicionar filtros condicionais
      const filters = {
        'medicoID': `\nAND A.MedicoID = ${formValues.medicoID}`,
        'centroCustoID': `\nAND A.CentroCustoID = ${formValues.centroCustoID}`,
        'convenioID': `\nAND A.ConvenioID = ${formValues.convenioID}`,
        'paciente': `\nAND A.PACIENTE LIKE '%${formValues.paciente}%'`,
        'status': `\nAND A.STATUS = '${formValues.status}'`,
        'encaixeHorario': `\nAND A.ENCAIXEHORARIO = 1`
      };

      Object.entries(filters).forEach(([key, value]) => {
        if (formValues[key]) {
          this.sql_execute += value;
        }
      });
    }

    private processApiResponse(response: ApiResponse[]): void {
      response.forEach(item => {
        const transformedItem: MapItem = {};
        
        item.columns.forEach((column, index) => {
          const key = column.toLowerCase();
          transformedItem[key] = item.values[index];
        });

        this.listResultsMaps.push(transformedItem);
      });
    }

    private organizeResultsByCenter(): void {
      const centersMap = new Map<string, MapItem>();

      // Agrupar por centro de custo
      this.listResultsMaps.forEach(item => {
        const center = item.centrocusto;
        
        if (!centersMap.has(center)) {
          centersMap.set(center, {
            ...item,
            persons: []
          });
        }

        centersMap.get(center).persons.push(item);
      });

      // Converter Map para array
      this.organizedResults = Array.from(centersMap.values());
    }

    handlePatternPreview() {
      const opened_pattern_modal = this.dialog.open(PreviewPatternComponent, {
        height: '90vh',
        width: '90vw',
        id: 'preview_pattern',
        panelClass: 'my-preview-pattern',
        data: { mapps_list: JSON.stringify(this.listResultsMaps), isAnalizing: this.analitico },	
      });

      opened_pattern_modal.afterClosed().subscribe((result) => {
        // Notify the change detector
        this._changeDetectorRef.detectChanges();
      });
    }


    // -----------------------------------------------------------------------------------------------------
    // @ Public methods
    // -----------------------------------------------------------------------------------------------------

    getStatusDescription(status) {
      return this.status_list.find((item) => item.value == status)?.label || '';
    }
  
    getStatusColor(status) {
      return this.status_list.find((item) => item.value == status)?.color || '#FFF';
    }

    cleanFormMaps() {
      this.mapsForm.reset();
      this.mapsForm.get('horaInicio').setValue(this.now.startOf('day').plus({ days: -7 }).toISODate());
      this.mapsForm.get('horaTermino').setValue(this.now.startOf('day').toISODate());
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
