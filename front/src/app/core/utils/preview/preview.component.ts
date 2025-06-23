import { CommonModule, DatePipe, NgIf, TitleCasePipe } from '@angular/common';
import { ChangeDetectorRef, Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule, UntypedFormControl } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOptionModule, MatRippleModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogContent, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
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
import { UtilsMethod } from 'app/modules/builder/builder-form/util/default/util';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
import { ToastrService } from 'ngx-toastr';
import { SignService } from '../sign-form/service/sign.service';
import { PreviewService } from './service/preview.service';
import { UtilApi } from 'app/app-api/default/api-util';


@Component({
    selector: 'app-preview',
    templateUrl: './preview.component.html',
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
			@screen sm {
				.my-dialog-preview {
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
				.my-dialog-preview {
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
				.my-dialog-preview {
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
			
		`,
    ]
})
export class PreviewComponent implements OnInit {

  headerObject;
  isPreviewDownload: number;
  previewObject: any = {};
  openedScreenObject: any = {};
  openedScreenPreviewObject: any = {};
  loggedObject: any = {};
  modalInfoObject = {};
  
  dateNow;

  logoSrcImage = "";
  generic_query = "";
  logoImageURL;

  pdfSrc;
  fileURL;

  list_identity_fields: any[] = [];

  idLastSaved: number;

  isSigned                = false;
  isLoadingDownload       = false;
  isLoading               = true;
  isLoadingSign           = false;
  isFileReadyForDownload  = false;
  isGenerateScreenPrint   = false;
  
  pinInputControl: UntypedFormControl = new UntypedFormControl();

  constructor(
    @Inject(MAT_DIALOG_DATA) 
    public dialogData               : any,
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
    this.handleHeaderPage();
  }

  handleHeaderPage() {
    this._previewService.getHeader()
      .subscribe((data) => {
        this.headerObject     = this.toObject(data);
        this.modalInfoObject  = JSON.parse(localStorage.getItem("dynamicScreen"));

        // get image client
        let objectURL       = 'data:image/jpg;base64,' + this.headerObject.logo;
        this.logoImageURL   = this.sanitizer.bypassSecurityTrustResourceUrl(objectURL);

        this.handleValidatePreview();
        
      }, (error) => {
        this.throwError(error.error.message);
        this.isLoading = false;
      })
  }

  handleValidatePreview() {

    this.idLastSaved = null;

    let validation  = this.dialogData?.response?.returnID?.[0]?.values?.[0]  ? true  : false;

    if (parseInt(this.dialogData?.response)) {
      this.idLastSaved = parseInt(this.dialogData?.response);
    }

    if (validation) {
      this.idLastSaved = parseInt(this.dialogData?.response?.returnID[0].values[0]);
    }

    this.loggedObject               = this.dialogData?.loggedScreen;
    this.list_identity_fields       = JSON.parse(this.dialogData?.screenBuilderIdentity);
    this.openedScreenObject         = JSON.parse(localStorage.getItem("opened_screen"));

    if (this.openedScreenObject?.previewObjectText == "" || !JSON.parse(this.openedScreenObject?.previewObjectText)) {
      return;
    } else {
      this.openedScreenPreviewObject  = JSON.parse(this.openedScreenObject?.previewObjectText);

      if (this.openedScreenPreviewObject?.type_template == "self") {
        this.constructorGenericPreviewOrEvolution(this.openedScreenObject.tableName);
      }

      if (this.openedScreenPreviewObject?.type_template != "self" && this.openedScreenPreviewObject?.template) {

        this._utilApi.getBuildingScreen(`builder/screen/${this.openedScreenPreviewObject.template}`)
          .subscribe((Opscreen: any) => {
            
            this._utilApi.getIndetityField(Opscreen.id).subscribe((fields_identity: any) => {
              this.openedScreenPreviewObject      = JSON.parse(Opscreen.previewObjectText);
              this.list_identity_fields           = fields_identity.content;

              this.constructorGenericPreviewOrEvolution(Opscreen.tableName);
            })
            
          });
      }
    }
    
  }

  constructorGenericPreviewOrEvolution(table_name) {
    if(this.openedScreenObject.tableName != "EVOLUCAO" && this.openedScreenObject && this.list_identity_fields) {
      this.generic_query += `SELECT * FROM ${table_name.toUpperCase()} WHERE ${this.list_identity_fields[0]?.columnName} = ${this.idLastSaved}`
    }
    

    if (this.idLastSaved) {
      this._previewService.handleGenericPreview(this.idLastSaved, this.generic_query)
      .subscribe((preview) => {
        this.previewObject  = this.toObject(preview);
        
        //TODO: remover
        //console.log("Screen Opened >", this.openedScreenObject);
        //console.log("Logged User Opened >", this.dialogData.loggedScreen);
        console.log("Return Select >", this.previewObject);
        console.log("Preview Object >", this.openedScreenPreviewObject);
        //console.log("Screen Opened Identity >", this.list_identity_fields);
        
        this.isLoading = false;
       
      }, (error) => {
        this.throwError(error.error.message);
        this.isLoading = false;
      })

    } else {
      this.throwError("Não foi possível abrir a tela de pré-visualização, pois não foi possível encontrar o ID do último registro salvo.");
    }
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

  async preparePdfToDownload() {
    this.pinInputControl.disable();
    this.isLoadingSign = true;
    this._changeDetectorRef.markForCheck();

    try {
      // Chame o método printScreen atualizado e assíncrono
      await this.printScreen();
      // isLoadingSign será tratado dentro de printScreen ou seus callbacks
    } catch (error) {
      console.error("Erro em preparePdfToDownload:", error);
      this.isLoadingSign = false; // Garanta que o estado de carregamento seja resetado em caso de erro
      this.pinInputControl.enable();
      this._changeDetectorRef.markForCheck();
      this.throwError("Falha ao preparar PDF para download.");
    }
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
        this.pinInputControl.enable();
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
          this.pinInputControl.enable();
          this._changeDetectorRef.markForCheck();
        return;
      }
      actualElementToPrint = htmlContentElement as HTMLElement;
    }

    const pdf = new jsPDF('p', 'mm', 'a4');
    const userLoggedScreen = JSON.parse(localStorage.getItem("userScreen")) || JSON.parse(localStorage.getItem("loggedUser"));

    pdf.setProperties({
        title: `${this.openedScreenObject?.title?.toLowerCase() || "print"}-${this.previewObject?.nome || 'document'}`,
        subject: `${this.openedScreenObject?.title?.toLowerCase() || "print"}-update`,
        keywords: `${this.openedScreenObject?.title?.toLowerCase() || "print"}`,
        creator: userLoggedScreen.logon,
        author: userLoggedScreen.logon
    });

    try {
        // Use o método html do jsPDF
        await pdf.html(actualElementToPrint, {
            callback: (doc) => {
                const file = doc.output('blob');
                if (print === "imprimir") { // Verifique o argumento passado
                    this.onlyPrintFunction(file);
                } else {
                    this.validateWhereColumn(file, userLoggedScreen, this.idLastSaved, this.dialogData.table_name);
                }
                // Redefina as flags após o processamento do PDF ser concluído pelo callback
                if (print === "imprimir") this.isGenerateScreenPrint = false;
                else this.isLoadingSign = false;
                
                this.pinInputControl.enable(); // Habilite a entrada do pin após o processamento
                this._changeDetectorRef.markForCheck();
            },
            x: 10, // Margem X
            y: 10, // Margem Y
            width: 190, // Largura do conteúdo (largura A4 210mm - 2*10mm de margem)
            windowWidth: actualElementToPrint.scrollWidth, // ou actualElementToPrint.offsetWidth
            // autoPaging: 'text', // 'slice' ou 'text'. 'text' pode ser melhor para documentos de texto.
                                  // Teste para ver qual oferece melhores resultados para o seu layout.
        });
    } catch (error) {
        console.error("Erro ao gerar PDF com jsPDF.html():", error);
        this.throwError("Falha ao gerar PDF baseado em texto. O conteúdo pode ser muito complexo ou ocorreu um erro.");
        if (print === "imprimir") this.isGenerateScreenPrint = false;
        else this.isLoadingSign = false;
        this.pinInputControl.enable();
        this._changeDetectorRef.markForCheck();
    }
  }


  oldSign(file, userLoggedScreen) {
    this._signService.newSign(file, userLoggedScreen, this.pinInputControl.value).subscribe((e) => {
      this.isLoadingSign = false;
      this.isSigned = true;

      // Mark for check
      this._changeDetectorRef.markForCheck();

      this.throwSuccess("Assinado com Sucesso!!");
    }, (error) => {
      this.throwError(error.error.message);

      // Mark for check
      this._changeDetectorRef.markForCheck();

      this.isLoadingSign = false;
    })
  }

  validateWhereColumn(file: any, userLoggedScreen: any, idLastSaved: any, previewTableName: string, signColumnName?: string) {
    try {
      // 1. Obter list_identity_fields de forma segura
      this.list_identity_fields = this.dialogData.screenBuilderIdentity 
          ? JSON.parse(this.dialogData.screenBuilderIdentity)
          : JSON.parse(localStorage.getItem("opened_screen") || '[]');

      if (!this.list_identity_fields?.length) {
          throw new Error("Nenhum campo de identidade configurado para assinatura.");
      }

      // 2. Construir whereColumn de forma eficiente
      const whereColumns = this.list_identity_fields
          .map(field => field.columnName)
          .filter(column => column) // Filtra colunas vazias/nulas
          .join(', '); // Junta com vírgulas automaticamente

      if (!whereColumns) {
          throw new Error("Nenhuma coluna válida encontrada.");
      }
      

      // 3. Chamar makeSigningPdf uma única vez
      this.makeSigningPdf(file, userLoggedScreen, idLastSaved, previewTableName, whereColumns, signColumnName);

    } catch (error) {
        console.error("Erro em validateWhereColumn:", error);
        this.throwError(error.message || "Erro ao processar campos de identidade");
    }
  }
  
  makeSigningPdf(file, userLoggedScreen, idLastSaved, previewTableName, whereClauseColumn, signColumnName?: string) {
    
    this._signService.signingWithDownload(file, this.pinInputControl.value, previewTableName, idLastSaved, userLoggedScreen, whereClauseColumn, signColumnName)
      .subscribe((res) => {
        if(res.body) {

          this.throwSuccess("Assinado com Sucesso!!");
          this.isPreviewDownload = res.body.id;
          this.getPdfPreview(this.isPreviewDownload);
        }

      }, (error) => {
        this.throwError(error.error.message);

        this.isLoadingSign = false;
        this.pinInputControl.enable();

        // Mark for check
        this._changeDetectorRef.markForCheck();
      })
  }

  getPdfPreview(id) {
    this._signService.downloadPDF(id).subscribe(
			(res: any) => {
        let blob = new Blob([res.body], {type: "application/pdf"})
        this.pdfSrc = window.URL.createObjectURL(blob);


        this.fileURL = this.sanitizer.bypassSecurityTrustResourceUrl(
          URL.createObjectURL(blob)
        );

        this.isLoadingSign = false;
        this.pinInputControl.enable();
        this.isSigned = true;

        // Mark for check
        this._changeDetectorRef.markForCheck();
			}
		);
  }

  getPdfPreviewDownload(id) {
    this.isLoadingDownload = true;

    this._signService.downloadPDF(id).subscribe(
			(res: any) => {
				let blob = new Blob([res.body], {type: "application/pdf"})
				let downloadLink = window.URL.createObjectURL(blob);

        this.isLoadingDownload = false;

        // Mark for check
        this._changeDetectorRef.markForCheck();

				window.open(downloadLink);
			}
		);
  }

  async getPrintScreen() { // Torne assíncrono
    this.isGenerateScreenPrint = true;
    this._changeDetectorRef.markForCheck();

    try {
      // Chame o método printScreen atualizado e assíncrono
      await this.printScreen("imprimir");
        // isGenerateScreenPrint será redefinido no callback de printScreen
    } catch (error) {
      console.error("Erro em getPrintScreen:", error);
      this.isGenerateScreenPrint = false; // Garanta que o estado de carregamento seja resetado em caso de erro
      this._changeDetectorRef.markForCheck();
      this.throwError("Falha ao gerar PDF para impressão.");
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

  returnValue(item) {
    const datepipe: DatePipe = new DatePipe('pt-BR');
    let res = item.field.toLowerCase();

    if (item.have_function) {
      switch(item.functions) {
        case "together": {
          return `${this.previewObject[res]} ${this.previewObject[item.field_together.toLowerCase()]}`;
        }
        case "system_function": {
          switch(item.functions_system) {
            case "atualDateAndHour": {
              return this._utils.getDateAndHour();
            }
            case "atualDateAndHourFormated": {
              return this._utils.getDateFormated();
            }
            case "getAgeFormated": {
              return this._utils.getFormatedAge(this.previewObject[res]);
            }
            case "atualDate": {
              return this._utils.getDate();
            }
            case "atualHour": {
              return this._utils.getHours();
            }
          }
        }
        case "pattern": {
          let date;
          if (res.includes("data") || res.includes("age") || res.includes("date") ) {
            date = datepipe.transform(new Date(this.previewObject[res]), item.pattern);
          }
          return date;
        }
      }
    }

    return this.previewObject[res] || "";
  }

  enterKeyPress(event){
    if(event.key === 'Enter' && this.pinInputControl.value.length >= 4){
      this.preparePdfToDownload();
    }
  }
}
