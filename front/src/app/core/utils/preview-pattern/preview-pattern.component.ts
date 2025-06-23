import { NgIf, CommonModule, TitleCasePipe } from '@angular/common';
import { ChangeDetectorRef, Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOptionModule, MatRippleModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule, MatDialogActions, MatDialogContent, MatDialogClose, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
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
import { DomSanitizer } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { fuseAnimations } from '@fuse/animations';
import { TranslocoModule } from '@ngneat/transloco';
import { UtilApi } from 'app/app-api/default/api-util';
import { UtilsMethod } from 'app/modules/builder/builder-form/util/default/util';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
import { ToastrService } from 'ngx-toastr';
import { PreviewComponent } from '../preview/preview.component';
import { PreviewService } from '../preview/service/preview.service';
import { SignService } from '../sign-form/service/sign.service';
import { JsonPipe } from '@angular/common';

import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { Agendamento, AgendamentoAgrupado } from './util/preview';

@Component({
  selector: 'app-preview-pattern',
  templateUrl: './preview-pattern.component.html',
  encapsulation: ViewEncapsulation.None,
  animations: fuseAnimations,
  standalone: true,
  imports: [
      NgIf,
      CommonModule,
      NgxExtendedPdfViewerModule,
      MatProgressSpinnerModule,
      MatDialogModule,
      PdfViewerModule,
      MatButtonModule,
      MatDialogActions,
      MatDialogContent,
      MatDialogClose,
      TitleCasePipe,
      MatDatepickerModule,
      MatTooltipModule,
      MatMenuModule,
      RouterModule,
      MatFormFieldModule,
      MatIconModule,
      MatInputModule,
      FormsModule,
      ReactiveFormsModule,
      JsonPipe,
      MatSortModule,
      MatPaginatorModule,
      MatSlideToggleModule,
      MatSelectModule,
      MatOptionModule,
      MatCheckboxModule,
      MatRippleModule,
      TranslocoModule,
      MatTabsModule
  ],
  styles: [
    /* language=SCSS */
    `

    .w-40R {
      width: 40rem !important;
    }

    .inventory-grid {
				grid-template-columns: 50px 300px 100px 150px 150px auto;

				@screen sm {
					grid-template-columns: 50px 300px 100px 150px 150px auto;
				}

				@screen md {
					grid-template-columns: 50px 300px 100px 150px 150px auto;
				}

				@screen lg {
					grid-template-columns: 50px 300px 100px 150px 150px auto;
				}
			}

    @screen sm {
      .my-preview-pattern {
        max-width: 80vw !important;
        max-height: 90vh !important;

        .mat-mdc-dialog-content {
          max-height: 100% !important;
        }
      }

      .backgroud-b-color-w {
        background-color: white;
        color: black;

        .icon-size-20 svg {
          width: 5rem !important;
          height: 5rem !important;
          color: black;
        }
      }
    }

    @screen md {
      .my-preview-pattern {
        max-width: 80vw !important;
        max-height: 90vh !important;

        .mat-mdc-dialog-content {
          max-height: 100% !important;
        }
      }

      .backgroud-b-color-w {
        background-color: white;
        color: black;

        .icon-size-20 svg {
          width: 8rem !important;
          height: 8rem !important;
          color: black;
        }
      }
    }

    @screen lg {
      .my-preview-pattern {
        max-width: 80vw !important;
        max-height: 90vh !important;

        .mat-mdc-dialog-content {
          max-height: 100% !important;
        }

        svg {
          font-size: 10rem;
        }
      }

      .backgroud-b-color-w {
        background-color: white;
        color: black;

        .icon-size-20 svg {
          width: 10rem !important;
          height: 10rem !important;
          color: black;
        }
      }
    }

    .icon-full-size {
      min-width: 100% !important;
      min-height: 100% !important;
    }

    .backgroud-g-color-w {
      .backgroud-b-color-w {
        background-color: #FEFEFE;
        color: black;
      }
    }

    .preview-img-align {
      display: flex;
      flex-direction: column;
      justify-content: center;
    }

    .row-print {
      padding: 1rem 0;
    }
  
    `,
  ]
})
export class PreviewPatternComponent implements OnInit {

  headerObject            : any       = {};
  loggedObject            : any       = {};
  transformedObject       : any       = {};
  agendamentosAgrupados   : any       = [];
  logoImageURL            : any       = null;
  
  dateNow;
  pdfSrc;
  fileURL;

  isSigned                = false;
  isLoadingDownload       = false;
  isLoading               = true;
  isLoadingSign           = false;
  isFileReadyForDownload  = false;
  isGenerateScreenPrint   = false;
  isAnalizing             = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) 
    public dialogData               : { mapps_list: any, isAnalizing?: boolean },
    public dialogRef                : MatDialogRef<PreviewComponent>,
    private _toastr                 : ToastrService,
    public _signService             : SignService,
    public _utils                   : UtilsMethod,
    private _utilApi                : UtilApi,
    private sanitizer               : DomSanitizer,
    public _previewService          : PreviewService,
    private _changeDetectorRef      : ChangeDetectorRef
  ) { }
  
  ngOnInit(): void {
    this.dateNow = this._utils.getDateFormatedReverse(); 

    this.transformedObject = JSON.parse(this.dialogData.mapps_list);

    // Agrupa os agendamentos
    this.agendamentosAgrupados = this.agruparAgendamentos(this.transformedObject);

    // Debugging: Log the grouped appointments
    console.log('Agendamentos Agrupados:', this.agendamentosAgrupados);

    this.isAnalizing = this.dialogData.isAnalizing ? this.dialogData.isAnalizing : false;

    this.loggedObject = JSON.parse(localStorage.getItem("loggedUser"));
    this._changeDetectorRef.markForCheck();

    this.handlePatternHeaderPage();
  }


  handlePatternHeaderPage() {
    this._previewService.getHeader()
      .subscribe((data) => {
        this.headerObject     = this.toObject(data);

        // get image client
        let objectURL       = 'data:image/jpg;base64,' + this.headerObject.logo;
        this.logoImageURL   = this.sanitizer.bypassSecurityTrustResourceUrl(objectURL);
        
        this.isLoading = false;

        this._changeDetectorRef.markForCheck();
      }, (error) => {
        this.throwError(error.error.message);
        this.isLoading = false;
        this._changeDetectorRef.markForCheck();
      })
  }

  async printScreen(print?: string) {
    this.isGenerateScreenPrint = print ? true : this.isGenerateScreenPrint; // Mantenha a lógica existente para esta flag
    this.isLoadingSign = !print ? true : this.isLoadingSign; // Mantenha a lógica existente
    this._changeDetectorRef.markForCheck();

    const element = document.getElementById("preview-full");
    if (!element) {
        this.throwError("Elemento de pré-visualização não encontrado.");
        this.isLoadingSign = false;
        this.isGenerateScreenPrint = false;
        this._changeDetectorRef.markForCheck();
        return;
    }
  
    // Clone o elemento que contém o conteúdo visível
    // Esta é a div com *ngIf="!isSigned && !isFileReadyForDownload"
    const contentToPrint = element.cloneNode(true) as HTMLElement;
    // Garanta que o elemento clonado seja parte do DOM se o jsPDF.html() exigir,
    // ou se os estilos não forem totalmente computados de outra forma. Frequentemente é melhor operar em um
    // elemento que é temporariamente anexado ao body, ou garantir que os estilos sejam autocontidos.
    // Para simplificar, usaremos o clone diretamente. Garanta que seja o correto.
    // Se `preview-full` for o pai do conteúdo que você deseja imprimir (e não o do iframe),
    // você pode precisar selecionar um filho dele se o próprio `element` tiver o toggle *ngIf.
    // O código atual parece ter duas divs com id="preview-full". Seja específico.
    // Vamos assumir que a segunda é o alvo quando !isSigned
    let actualElementToPrint = element;
    if (this.isSigned || this.isFileReadyForDownload) {
      // Se já estiver assinado ou pronto, o PDF provavelmente já é um iframe.
      // Este caminho lógico pode não ser para gerar um novo PDF a partir do HTML.
      // No entanto, a estrutura original do código implica que printScreen é chamado para conteúdo HTML.
      // Precisamos do elemento de conteúdo HTML.
      const htmlContentElement = Array.from(document.querySelectorAll('#preview-full')).find(el => {
        const htmlEl = el as HTMLElement;
        // Verifica se o elemento está visível e não contém um iframe
        return window.getComputedStyle(htmlEl).display !== 'none' && !htmlEl.querySelector('iframe');
      });
      if (!htmlContentElement) {
        this.throwError("Conteúdo HTML para geração de PDF não encontrado.");
          this.isLoadingSign = false;
          this.isGenerateScreenPrint = false;
          this._changeDetectorRef.markForCheck();
        return;
      }
      actualElementToPrint = htmlContentElement as HTMLElement;
    }
  
    const pdf = new jsPDF('l', 'mm', 'a4');
    const userLoggedScreen = JSON.parse(localStorage.getItem("userScreen")) || JSON.parse(localStorage.getItem("loggedUser"));

    pdf.setProperties({
      title: `Mapa de Agendamento`,
      subject: `r`,
      keywords: `r`,
      creator: userLoggedScreen.logon,
      author: userLoggedScreen.logon
    });
    
  const screenToPdfRatio = 1100 / element.scrollWidth;
  const effectiveWindowWidth = 1100; // Valor fixo de referência

    try {
      // Use o método html do jsPDF
      await pdf.html(actualElementToPrint, {
        callback: (doc) => {
          const file = doc.output('blob');
          this.onlyPrintFunction(file);
          
          // Redefina as flags após o processamento do PDF ser concluído pelo callback
          this.isGenerateScreenPrint = false;
          this.isLoadingSign = false;
          
          this._changeDetectorRef.markForCheck();
        },
        x: 15, // Margem X
        y: 15, // Margem Y
        width: 267, // Largura para A4 landscape (297mm - 20mm de margem)
        windowWidth: effectiveWindowWidth, // Largura da janela para html2canvas
        autoPaging: 'text', // Paginação automática baseada no texto
        html2canvas: {
          scale: 0.25, // Reduzindo a escala para diminuir o zoom
          letterRendering: true, // Melhora a renderização de texto
          useCORS: true,
          allowTaint: true,
          scrollX: 0,
          scrollY: 0,
          windowWidth: effectiveWindowWidth ,
          onclone: (clonedDoc) => {
              // Garantir que estilos sejam preservados
              const style = clonedDoc.createElement('style');
              clonedDoc.head.appendChild(style);
          }
        },
      });
    } catch (error) {
      console.error("Erro ao gerar PDF com jsPDF.html():", error);
      this.throwError("Falha ao gerar PDF baseado em texto. O conteúdo pode ser muito complexo ou ocorreu um erro.");
      if (print === "imprimir") this.isGenerateScreenPrint = false;
      else this.isLoadingSign = false;
      this._changeDetectorRef.markForCheck();
    }
  }

  onlyPrintFunction(file) {
    this.pdfSrc = window.URL.createObjectURL(file);
    this.fileURL = this.sanitizer.bypassSecurityTrustResourceUrl(
      URL.createObjectURL(file)
    );

    this.isGenerateScreenPrint = false; // Redefina aqui, pois é a etapa final para este caminho
    this.isFileReadyForDownload = true;
    this._changeDetectorRef.markForCheck();
  }


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
			for (let x = 0; x < arr[i].columns.length; ++x) {
				// Assign each element to object
				rv[arr[i].columns[x].toLowerCase()] = arr[i].values[x];
			}
		}
			
		return rv;
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

  getValuesPerson(values) {
    return JSON.parse(values);
  }

  agruparPorData(agendamentos: Agendamento[]): Record<string, Agendamento[]> {
    const agrupados: Record<string, Agendamento[]> = {};

    agendamentos.forEach(agendamento => {
      // Extrai a data completa do campo data do JSON
      const dataCompleta = new Date(agendamento.data);
      
      // Formata como YYYY-MM-DD para agrupamento
      const dataKey = dataCompleta.toISOString().split('T')[0];
      
      if (!agrupados[dataKey]) {
        agrupados[dataKey] = [];
      }
      
      agrupados[dataKey].push(agendamento);
    });

    return agrupados;
  }

  agruparPorAgendamentoId(agendamentos: Agendamento[]): Record<string, Agendamento[]> {
    const agrupados: Record<string, Agendamento[]> = {};

    agendamentos.forEach(agendamento => {
      const id = agendamento.agendamentoid;
      
      if (!agrupados[id]) {
        agrupados[id] = [];
      }
      
      agrupados[id].push(agendamento);
    });

    return agrupados;
  }

  /**
   * Agrupa agendamentos por data e centro de custo
   * @param dados Lista de agendamentos
   * @returns Lista de agendamentos agrupados
   
  agruparAgendamentos(dados: any[]): AgendamentoAgrupado[] {
    // Primeiro normaliza os dados
    const agendamentosNormalizados: Agendamento[] = [];
    const idsProcessados = new Set<string>();
    
    dados.forEach(item => {
      if (!idsProcessados.has(item.agendamentoid)) {
        const grupo = dados.filter(d => d.agendamentoid === item.agendamentoid);
        const normalizado = this.normalizarAgendamentos(grupo);
        if (normalizado) {
          agendamentosNormalizados.push(normalizado);
          idsProcessados.add(item.agendamentoid);
        }
      }
    });

    // Agora agrupa por data e centrocusto
    const grupos = new Map<string, Map<string, Agendamento[]>>();
    
    agendamentosNormalizados.forEach(agendamento => {
      const dataKey = agendamento.data.split('T')[0]; // Apenas a data, sem hora
      const centroKey = agendamento.centrocusto;
      
      if (!grupos.has(dataKey)) {
        grupos.set(dataKey, new Map<string, Agendamento[]>());
      }
      
      const gruposData = grupos.get(dataKey)!;
      
      if (!gruposData.has(centroKey)) {
        gruposData.set(centroKey, []);
      }
      
      gruposData.get(centroKey)!.push(agendamento);
    });

    // Converte para o formato de saída
    const resultado: AgendamentoAgrupado[] = [];
    
    grupos.forEach((gruposCentro, data) => {
      gruposCentro.forEach((agendamentos, centrocusto) => {

        resultado.push({
          data,
          centrocusto,
          agendamentos: Array.from(agendamentos.values())
        });
        
      });
    });

    // Ordena por data (mais recente primeiro)
    return resultado.sort((a, b) => new Date(b.data).getTime() - new Date(a.data).getTime());;
  }
  

  // Função auxiliar para normalizar (baseada na solução anterior)
  normalizarAgendamentos(dados: any[]): Agendamento | null {
    if (!dados.length) return null;
    
    const base = dados[0];
    
    const extrairNomeId = (str: string) => {
      const parts = str.split(' - ');
      return {
        nome: parts.slice(0, -1).join(' - '),
        id: parts[parts.length - 1]
      };
    };

    return {
      agendamentoid: base.agendamentoid,
      tipoagendamento: base.tipoagendamento,
      centrocusto: base.centrocusto,
      convenio: base.convenio,
      profissional: base.profissional,
      ficha: base.ficha,
      paciente: {
        nome: base.paciente,
        nascimento: base.nascimento,
        contato1: base.contato1,
        contato2: base.contato2,
        whatsapp: base.whatsapp
      },
      data: base.data,
      tempo: base.tempo,
      usuario: base.usuario,
      observacoes: base.observacoes,
      status: base.status,
      encaixe: base.encaixe,
      recursos: [{
        codigo: base.cod_recurso,
        nome: base.nome_recurso != null ? base.nome_recurso.trim() : null,
        quantidade: base.quant_recurso
      }],
      equipe: dados.map(item => {
        const { nome, id } = item.profissional_equipe != null ? extrairNomeId(item.profissional_equipe) : { nome: '', id: '' };
        return {
          funcao: item.funcao,
          nome,
          id
        };
      })
    };
  }
  */

  agruparAgendamentos(agendamentos: any[]) {
    const agrupados: {[key: string]: any} = {};

    agendamentos.forEach(agendamento => {
      // Extrair apenas a data (sem hora) para agrupamento
      const data = new Date(agendamento.data).toISOString().split('T')[0];
      
      // Criar chave única para o agrupamento
      const chave = `${data}_${agendamento.centrocusto}_${agendamento.profissional}_${agendamento.agendamentoid}`;
      
      if (!agrupados[chave]) {
        agrupados[chave] = {
          data: agendamento.data,
          dataFormatada: this.formatarData(agendamento.data),
          centrocusto: agendamento.centrocusto,
          profissional: this.agregarProfissional(agendamento.profissional),
          agendamentoid: agendamento.agendamentoid,
          pacientes: [],
          recursos: [],
          equipe: [] // Este array não terá duplicatas
        };
      }

      // Adicionar paciente (evitando duplicatas)
      if (!agrupados[chave].pacientes.some(p => p.paciente === agendamento.paciente)) {
        agrupados[chave].pacientes.push({
          data: agendamento.data,
          paciente: agendamento.paciente,
          nascimento: agendamento.nascimento,
          contato1: agendamento.contato1,
          contato2: agendamento.contato2,
          whatsapp: agendamento.whatsapp,
          tipoagendamento: agendamento.tipoagendamento,
          observacoes: agendamento.observacoes,
          status: agendamento.status,
          convenio: agendamento.convenio,
          encaixe: agendamento.encaixe
        });
      }

      // Adicionar paciente (evitando duplicatas)
      const pacienteExistente = agrupados[chave].pacientes.some(
        (p: any) => p.paciente === agendamento.paciente
      );
      
      if (!pacienteExistente) {
        agrupados[chave].pacientes.push({
          data: agendamento.data,
          paciente: agendamento.paciente,
          nascimento: agendamento.nascimento,
          contato1: agendamento.contato1,
          contato2: agendamento.contato2,
          whatsapp: agendamento.whatsapp,
          observacoes: agendamento.observacoes,
          tipoagendamento: agendamento.tipoagendamento,
          status: agendamento.status,
          convenio: agendamento.convenio,
          encaixe: agendamento.encaixe
        });
      }

      // Adicionar recursos (evitando duplicatas)
      if (agendamento.cod_recurso &&  !agrupados[chave].recursos.some(r => r.cod_recurso === agendamento.cod_recurso)) {
        agrupados[chave].recursos.push({
          cod_recurso: agendamento.cod_recurso,
          nome_recurso: agendamento.nome_recurso?.trim(),
          tipo_recurso: agendamento.tipo_recurso?.trim(),
          quant_recurso: agendamento.quant_recurso
        });
      }

      // Adicionar membros da equipe (evitando duplicatas)
      if (agendamento.funcao && agendamento.profissional_equipe) {
        const membroExistente = agrupados[chave].equipe.some(
          m => m.funcao === agendamento.funcao && 
            m.profissional_equipe === agendamento.profissional_equipe
        );
        
        if (!membroExistente) {
          agrupados[chave].equipe.push({
            funcao: agendamento.funcao,
            profissional_equipe: agendamento.profissional_equipe
          });
        }
      }
    });

    // Converter objeto em array e ordenar por data
    return Object.values(agrupados).sort((a, b) => 
      new Date(a.data).getTime() - new Date(b.data).getTime()
    );
  }

  // Funções auxiliares
  formatarData(dataString: string): string {
    const data = new Date(dataString);
    return data.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  agregarProfissional(profissional: string): string {
    // Remove o código do hospital se existir (ex: " - 6500")
    return profissional.split(' - ')[0];
  }

  objectKeys(obj: Record<string, any>): string[] {
    return Object.keys(obj);
  }
  
  agendamentosArray(agendamentosMap) {
    return Array.from(agendamentosMap?.values() || []);
  }

}
