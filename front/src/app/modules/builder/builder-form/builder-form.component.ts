import {
	JsonPipe,
	NgClass,
	NgFor,
	NgIf,
	Location,
	TitleCasePipe,
} from '@angular/common';
import {
	ChangeDetectionStrategy,
	ChangeDetectorRef,
	Component,
	Input,
	OnDestroy,
	OnInit,
	ViewChild,
	ViewEncapsulation
} from '@angular/core';
import {
	FormControl,
	FormsModule,
	NgForm,
	ReactiveFormsModule,
	UntypedFormBuilder,
	UntypedFormGroup,
	ValidationErrors,
	Validators,
} from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOptionModule, MatRippleModule } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatError, MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { fuseAnimations } from '@fuse/animations';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { UtilApi } from 'app/app-api/default/api-util';
import { DynamicSaveClick } from 'app/core/utils/dynamic-modal/utils/service/dynamic-save-click.servive';
import { PreSaveComponent } from 'app/core/utils/pre-save/pre-save.component';
import { PreviewComponent } from 'app/core/utils/preview/preview.component';
import { Field } from 'app/modules/admin/screen/model/field.model';
import { ToastrService } from 'ngx-toastr';
import { Subscription, delay, combineLatest, tap } from 'rxjs';
import { RequestBody } from '../builder-screen/util/model/request-body.model';
import { UtilsMethod } from './util/default/util';
import { FieldComponent } from './util/field/field.component';
import { BuildingScreen } from 'app/modules/admin/screen/model/building.model';


@Component({
    selector: 'app-builder-form',
    templateUrl: './builder-form.component.html',
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
    styles: [
        /* language=SCSS */
        `
			.w-30px {
				width: 30px !important;
			}

			::ng-deep .erro-snack {
				background-color: red;
				opacity: .5;
				color:white;
			}

			.loading {
				opacity: .5;
			}
		`,
    ],
	standalone: true,
    animations: fuseAnimations,
    imports: [
        NgIf,
        FieldComponent,
        RouterModule,
        TitleCasePipe,
        JsonPipe,
        MatError,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        FormsModule,
        MatAutocompleteModule,
        MatProgressSpinnerModule,
        ReactiveFormsModule,
        MatButtonModule,
        MatSortModule,
        NgFor,
        MatPaginatorModule,
        NgClass,
        MatSlideToggleModule,
        MatSelectModule,
        MatOptionModule,
        MatCheckboxModule,
        MatRippleModule,
        TranslocoModule,
    ]
})
export class BuilderFormComponent implements OnInit, OnDestroy {

	@Input() idBuildScreen: any = null;
	@Input() typeBuildScreen: any = null;
	@Input() valuePreviewScreen: any = null;
	@Input() componentScreen: any = null;

	@ViewChild('comingSoonNgForm') comingSoonNgForm: NgForm;

	isModal = false;
	isValid = false;
	isLoadingSelect = false;
	selectedProductForm: UntypedFormGroup;

	previewScreenObj;

	item$: BuildingScreen = null;
	screenName = '';
	names = [];
	id;
	
	requestBody: RequestBody;
	routerType = 'add';
	whereClause = '';
	idLastSaved: any = null;

	clickEventSubscription: Subscription;

	/**
	 * Constructor
	 */
	constructor(
		private _formBuilder			: UntypedFormBuilder,
		private activatedroute			: ActivatedRoute,
		private _changeDetectorRef		: ChangeDetectorRef,
		private _utilsMethod			: UtilsMethod,
		private _toastr					: ToastrService,
		public dialog					: MatDialog,
		private _location				: Location,
		private transSrv				: TranslocoService,
		private _utilApi				: UtilApi,
		private _click					: DynamicSaveClick) {
		this.clickEventSubscription = this._click.getClickEvent().subscribe(() => {
			this.handlePreSaveFunction();
		})
	}

	/**
	 * On init
	 */
	ngOnInit(): void {
		this.firstFunction();
	}


	/**
	 * Manipula o pré-salvamento baseado no tipo de tela (modal ou padrão)
	 */
	handlePreSaveFunction(): void {
		this.validateAndSave(
			this.isModal ? this.componentScreen.screenLogin : this.item$.screenLogin
		);
	}

	/**
	 * Valida o formulário e executa o fluxo de salvamento apropriado
	 * @param hasLogin Indica se a tela requer autenticação
	 */
	private validateAndSave(hasLogin: boolean): void {
		if (this.selectedProductForm.invalid) {
			this.throwFormErrors();
			return;
		}

		hasLogin ? this.modalPreSaveHaveLogin() : this.modalPersistSave();
	}


	firstFunction() {
		this.selectedProductForm = this._formBuilder.group({});

		if((this.idBuildScreen && this.typeBuildScreen) == null) {

			this.isEditFunction();
		} else {
			this.isModalFunction();
		}
	}


	/**
	 * Verifica se a rota atual é de edição e obtém parâmetros necessários
	 */
	isEditFunction(): void {
		combineLatest([ this.activatedroute.params, this.activatedroute.url ]).subscribe(([params, urlSegments]) => {
			// Verifica se é rota de edição
			if (urlSegments[0]?.path.includes('edit')) {
			this.routerType = 'edit';
			this.whereClause = urlSegments[0].parameters?.state;
			}

			// Obtém parâmetros básicos
			this.id = params.idScreen;
			this.screenName = params.name?.replaceAll("_", " ") || '';
			
			this.getBuilderFieldByDefault();
		});
	}


	isModalFunction() {
		this.isModal = true;
		this.previewScreenObj = this.valuePreviewScreen;
		this.id = this.idBuildScreen;
		this.routerType = this.typeBuildScreen;

		this.getBuilderFieldByDefault();
	}

	getBuilderFieldByDefault() {
		let i = 0;

		this._utilApi
			.getBuildingScreen(`builder/screen/${this.id}/complete`)
				.subscribe((screen: any) => {
					this.item$ = new BuildingScreen(screen);
					//TODO: remove
					//console.log("endpoint novo > ", this.item$);
					this.addFormControll();

					// Mark for check
					this._changeDetectorRef.markForCheck();
				});
	}

	/**
	 *  Method to populate default value
	 * 
	 */
	validateDefaultValue() {
		this.item$.divs.forEach((div: any) => {
			if (div.fields) {
				div.fields.forEach((field: any) => {

					if(!field.value.includes('@') && field.value != '""' && this.selectedProductForm.get(field.formControlName)) {
						this.selectedProductForm.get(field.formControlName).setValue(field.value);
					}
				});
			}
		});
	}

	/**
	 *  Adicionando o field ao formControll
	 * 
	 */
	// STEP 1 
	addFormControll(): void {
		try {
			this.item$.divs.forEach((div: any, index: number) => {
			if (!div.fields) return;

			this.processFields(div.fields);

			if (index === this.item$.divs.length - 1) {
				this.finalizeFormSetup();
			}
			});
		} catch (error) {
			console.error('Error in addFormControll:', error);
			this.isValid = false;
			this._changeDetectorRef.markForCheck();
		}
	}

	// STEP 2
	private processFields(fields: Field[]): void {
		fields.forEach((field: Field) => {
			if (field.previewOnly) return;

			this.addFieldControl(field);
			this.addFieldValidators(field);
		});
	}

	// STEP 3
	private addFieldControl(field: Field): void {
		if (field.type === 'search') {
			this.addSearchControl(field);
			return;
		}

		if (field.type === 'checkbox') {
			this.addCheckboxControl(field);
			return;
		}

		if (this.shouldGetFunctionValue(field)) {
			this.getFunctionsValue(field);
			return;
		}

		this.addStandardControl(field);
	}

	private addSearchControl(field: Field): void {
		this.selectedProductForm.addControl(
			field.formControlName,
			new FormControl()
		);
	}

	private addCheckboxControl(field: Field): void {
		this.selectedProductForm.addControl(
			field.formControlName,
			new FormControl(field.value == 1)
		);
	}

	private addStandardControl(field: Field): void {
		const value = this.getFieldValue(field);
	
		if (field.type === 'select') {
			this.selectedProductForm.addControl(
			field.formControlName,
			new FormControl(value ?? '')
			);
			
			if (!field.defaultsValue) {
			this.createSelectListField(field);
			}
			return;
		}

		if (value !== undefined) {
			this.selectedProductForm.addControl(
			field.formControlName,
			new FormControl(value)
			);
		}
	}

	// STEP 4
	private getFieldValue(field: Field): any {
		if (field.value.includes('@') || field.value.includes('$')) {
			return undefined;
		}

		if (this.previewScreenObj) {
			const cleanValue = field.value.replaceAll("@", "").toLowerCase();
			return this.previewScreenObj[cleanValue] ?? field.value;
		}

		return field.value;
	}

	// STEP 5
	private addFieldValidators(field: Field): void {
		if (field.validatorRequired && !field.columnName.includes("$")) {
			const control = this.selectedProductForm.get(field.formControlName);
			control?.setValidators([Validators.required]);
		}
	}

	private shouldGetFunctionValue(field: Field): boolean {
		return this.routerType === 'add' && !!field.functionMethod;
	}

	/**
	 * Finalizes the form setup after all fields have been added
	 * 
	 */
	private finalizeFormSetup(): void {
		if (this.routerType === 'edit') {
			this.getPopulateData();
		} else {
			this.isValid = true;
			this._changeDetectorRef.markForCheck();
			
			if (this.valuePreviewScreen != null) {
			this.populatePreviewScreen();
			}
		}
	}

	/**
	 * Method to populate function in control on init
	 * 
	 * @param field 
	 * 
	 */
	getFunctionsValue(field) {

		switch(field.functionMethod) {
			case 'atualDateAndHourFormated': {
				this.selectedProductForm.addControl(
					field.formControlName,
					new FormControl(this._utilsMethod.getDateFormated())
				);
				// Mark for check
				this._changeDetectorRef.markForCheck();
				break;
			}
			case 'atualDateAndHour': {
				this.selectedProductForm.addControl(
					field.formControlName,
					new FormControl(this._utilsMethod.getDateAndHour())
				);
				// Mark for check
				this._changeDetectorRef.markForCheck();
			}
			case "getAgeFormated": {
				// TODO: implementar
				"IMPLEMENTAR";
			}
			case 'atualDate': {
				this.selectedProductForm.addControl(
					field.formControlName,
					new FormControl(this._utilsMethod.getDate())
				);
				// Mark for check
				this._changeDetectorRef.markForCheck();
			}
			case 'atualHour': {
				this.selectedProductForm.addControl(
					field.formControlName,
					new FormControl(this._utilsMethod.getHours())
				);
				// Mark for check
				this._changeDetectorRef.markForCheck();
			}
		}
	}


	/**
	 * Method to populate value after add
	 * 
	 */
	addFunctionsAfterClear() {
		this._utilApi.getOne('builder/field/list/', this.item$.id).subscribe((fields) => {
			fields.content.forEach((f: any) => {
				if (f.functionMethod) {

					switch(f.functionMethod) {
						case 'atualDateAndHourFormated': {
							this.selectedProductForm.get(f.formControlName).setValue(this._utilsMethod.getDateFormated())
							// Mark for check
							this._changeDetectorRef.markForCheck();
							break;
						}
						case 'atualDateAndHour': {
							this.selectedProductForm.get(f.formControlName).setValue(this._utilsMethod.getDateAndHour())
							// Mark for check
							this._changeDetectorRef.markForCheck();
							break;
						}
						case 'atualDate': {
							this.selectedProductForm.get(f.formControlName).setValue(this._utilsMethod.getDate())
							// Mark for check
							this._changeDetectorRef.markForCheck();
							break;
						}
						case 'atualHour': {
							this.selectedProductForm.get(f.formControlName).setValue(this._utilsMethod.getHours())
							// Mark for check
							this._changeDetectorRef.markForCheck();
							break;
						}
					}
				}
			})
		})
	}


	/**
	 * Obtém e popula dados para edição baseado na tabela e cláusula WHERE
	 */
	getPopulateData(): void {
		this.requestBody = new RequestBody({
			operationType: 'SELECT',
			tableName: this.item$.tableName,
			whereValue: this.whereClause
		});

		this._utilApi.getOneQuery('query/one', this.requestBody).subscribe({
			next: (data) => this.handlePopulationSuccess(data),
			error: (err) => this.handlePopulationError(err)
		});
	}

	/**
	 * Formata os dados para população
	 */
	private formatPopulationData(data: { columns: string[], values: any[] }): string[] {
		return data.columns.map((column, index) => 
			`colun:${column}value_${data.values[index]}`
		);
	}

	/**
	 * Trata a resposta bem-sucedida da API
	 */
	private handlePopulationSuccess(data: any[]): void {
		if (!data?.length || !data[0].columns || !data[0].values) {
			console.warn('Dados de população inválidos');
			return;
		}

		const formattedData = this.formatPopulationData(data[0]);
		this.ifEditPopulate(formattedData);
	}

	/**
	 * Trata erros na obtenção dos dados
	 */
	private handlePopulationError(error: any): void {
		console.error('Erro ao obter dados para população:', error);
		this.throwError(error.error?.message || 'Erro desconhecido');
		
		this.isValid = true;
		this._changeDetectorRef.markForCheck();
	}

	/**
	 *  Methody populate formControll
	 * 
	 */
	ifEditPopulate(data) {
		this.item$.divs.forEach((div: any, index) => {
			if (div.fields.length > 0) {
				div.fields.forEach((field: any) => {
					data.forEach((element: any) => {
						let valid = element.substring(element.indexOf(":") + 1, element.indexOf("value_"));
						if (field.columnName == valid) {
							this.selectedProductForm.get(field.formControlName).setValue(element.substring(element.indexOf("_") + 1))
						}
					});
				})
			}

			if ((this.item$.divs.length - 1) == index) {
				this.isValid = true;
				// Mark for check
				this._changeDetectorRef.markForCheck();
			}
		})
	}

	/**
	 * Atualiza um item com base nos dados do formulário
	 */
	updateItem(): void {
		this.selectedProductForm.disable();

		if (this.item$.executeOption) {
			this._utilApi.getOne('builder/field/list/', this.item$.id).subscribe({
				next: (fields) => this.processEditableFields(fields.content),
				error: (err) => this.handleUpdateError(err)
			});
		} else {
			this.handleSimpleUpdate();
		}
	}

	/**
	 * Processa campos editáveis para construção da query SQL
	 */
	private processEditableFields(fields: any[]): void {
		const editableFields = fields.filter(f => f.editable && !f.previewOnly);
		const updateValues = this.buildUpdateValues(editableFields);
		
		if (updateValues) {
			this.handlePersistenceSQL(
				"update", 
				this.item$.tableName, 
				this.whereClause, 
				updateValues
			);
		}
	}

	/**
	 * Constrói a string de valores para atualização
	 */
	private buildUpdateValues(fields: any[]): string {
		return fields
			.map((field, index) => {
			const value = this.selectedProductForm.get(field.formControlName)?.value;
			return this.formatFieldValue(field, value);
			})
			.filter(Boolean)
			.join(',');
	}

	/**
	 * Formata o valor do campo para SQL conforme seu tipo e propriedades
	 * @param field - Objeto do campo com suas propriedades
	 * @param value - Valor a ser formatado
	 * @returns String formatada para uso em SQL ou null se valor for inválido
	 */
	private formatFieldValue(field: any, value: any): string | null {
		// Caso base: valor nulo
		if (value == null) {
			return field.nullValue ? 'null' : null;
		}

		// Tipos numéricos e identity
		if (field.type === 'identity' || field.type === 'number') {
			return `${this.formatNumberValue(value, field.nullValue)}`;
		}

		// Tipos de data
		if (field.type === 'date') {
			const formattedDate = this.formatDateValue(value, field.functionMethod);
			return `${formattedDate}`;
		}

		// Tipos padrão (texto, etc)
		const formattedValue = this.formatDefaultValue(value, field.nullValue);
		return formattedValue === 'null' 
			? `null`
			: `${formattedValue}`;
	}


	/**
	 * Trata atualização simples (sem executeOption)
	 */
	private handleSimpleUpdate(): void {
		this.throwSuccess(this.transSrv.translate('util.update-success', {}, this.transSrv.getActiveLang()));

		this.selectedProductForm.enable();
		this.selectedProductForm.reset();

		const proSaveList = JSON.parse(this.item$.proSaveListTxt || '[]');
		if (proSaveList.length > 0) {
			this.handlePosOrPreSql(proSaveList, "pos", null);
		}

		this.addFunctionsAfterClear();
		this.validateDefaultValue();
		this.populatePreviewScreen();
	}

	/**
	 * Popula valores modais baseados em campos que contêm '$' no valor
	 */
	populateModalValue(): void {
		this.item$.divs?.forEach((div: any) => {
			this.processDivFields(div);
		});
	}

	/**
	 * Processa campos de uma div individual
	 */
	private processDivFields(div: any): void {
		div.fields?.forEach((field: any) => {
			if (field?.value?.includes('$')) {
			this.dataUserModal(field);
			}
			this._changeDetectorRef.markForCheck();
		});
	}

	/**
	 * Preenche o valor do campo com dados do usuário armazenados localmente
	 * @param field - Campo do formulário a ser preenchido
	 */
	dataUserModal(field: any): void {
		try {
			const userData = this.getUserDataFromStorage();
			if (!userData || !field?.formControlName) return;

			const fieldKey = this.extractFieldKey(field.value);
			if (!fieldKey) return;

			this.setFormFieldValue(field.formControlName, userData[fieldKey]);
		} catch (error) {
			console.error('Error setting user modal data:', error);
		}
	}

	/**
	 * Obtém dados do usuário do localStorage
	 */
	private getUserDataFromStorage(): any {
		const userScreenData = localStorage.getItem('userScreen');
		const loggedUserData = localStorage.getItem('loggedUser');
		
		try {
			return JSON.parse(userScreenData) || JSON.parse(loggedUserData);
		} catch (e) {
			console.error('Error parsing user data:', e);
			return null;
		}
	}

	/**
	 * Extrai a chave do campo removendo o prefixo $
	 */
	private extractFieldKey(fieldValue: string): string | null {
		if (!fieldValue?.includes('$')) return null;
		return fieldValue.replace('$', '');
	}

	/**
	 * Executa a persistência dos dados do modal, com tratamento de pré-salvamento
	 */
	modalPersistSave(): void {
		this.prepareFormForSave();

		if (this.hasPreSaveFunctions()) {
			this.executePreSaveFunctions();
		} else {
			this.handleSaveWithoutPreFunctions();
		}
	}

	/**
	 * Prepara o formulário para o processo de salvamento
	 */
	private prepareFormForSave(): void {
		this.selectedProductForm.disable();
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Verifica se existem funções de pré-salvamento
	 */
	private hasPreSaveFunctions(): boolean {
		try {
			const preSaveFunctions = JSON.parse(this.item$.preSaveFunctionListTxt || '[]');
			return preSaveFunctions?.length > 0;
		} catch (error) {
			console.error('Error parsing preSaveFunctionListTxt:', error);
			return false;
		}
	}

	/**
	 * Executa as funções de pré-salvamento
	 */
	private executePreSaveFunctions(): void {
		try {
			const preSaveFunctions = JSON.parse(this.item$.preSaveFunctionListTxt);
			this.executeFieldFunctions(preSaveFunctions);
		} catch (error) {
			console.error('Error executing pre-save functions:', error);
			this.selectedProductForm.enable();
		}
	}


	/**
	 * 
	 * @param list_of_execution 
	 * @param type_handle 
	 * @param response 
	 * 
	 * Handle to execute pre sql or pos sql
	 */
	handlePosOrPreSql(list_of_execution: any[], type_handle: "pre" | "pos", response?: any, iteration = 0) {
		const MAX_ITERATIONS = 30; // Evita recursão infinita
		const LAST_ID = response?.returnID[0]?.values?.[0] ?? null;
		const LOGGED_USER = JSON.parse(localStorage.getItem("loggedUser"));
		const LOGGED_USER_SCREEN = JSON.parse(localStorage.getItem("userScreen"));

		// Consolidar
		const ALL_FIELDS = this.item$.divs.flatMap(div => div.fields);

		for (let i = 0; i < list_of_execution.length; i++) {
			let sql = list_of_execution[i].sql;
			
			if (sql.includes("@")) {
				const match = sql.match(/@([^@]+)@/);
				if (match) {
					const [fullMatch, varName] = match;
					const varKey = varName.replace(/_/g, '').toLowerCase();

					// Encontrar campo por formControlName
					let FIELD_KEY = ALL_FIELDS.find(field => field.formControlName.replaceAll(/_/g, '') === varKey)?.value.toLowerCase();
					let replacement;
					if (fullMatch === "@LAST_ID@") {
						replacement = LAST_ID ?? "NULL";
					} else {
						replacement = this.selectedProductForm.get(varKey)?.value 
								?? this.previewScreenObj[varKey]
								?? this.previewScreenObj[FIELD_KEY.replaceAll(/[@$]/g, '')]
								?? LOGGED_USER_SCREEN[FIELD_KEY.replaceAll(/[@$]/g, '')]
								?? LOGGED_USER[FIELD_KEY.replaceAll(/[@$]/g, '')]
								?? "NULL";
					}
					sql = sql.replace(fullMatch, replacement);
					list_of_execution[i].sql = sql;
				}
			}

			// Lógica do último item
			if (i === list_of_execution.length - 1) {
				if (!sql.includes("@")) {
					type_handle === "pre"
						? this.queryPreSaveAndPopulate(list_of_execution)
						: this.queyPostSave(list_of_execution);
				} else if (iteration < MAX_ITERATIONS) {
					this.handlePosOrPreSql(list_of_execution, type_handle, response, iteration + 1);
				} else {
					console.error("builder-form.component.ts - 630 - Max iterations reached with unresolved variables");
				}
			}
		}
	}

	/**
	 * Executa queries de pré-salvamento e popula o formulário
	 * @param list Lista de queries de pré-salvamento
	 */
	async queryPreSaveAndPopulate(list: any[]): Promise<void> {
		if (!list?.length) return;

		try {
			// Executa todas as queries em paralelo
			await this.executePreSaveQueries(list);
			
			// Processa o resultado após todas as queries completarem
			this.handlePostPreSave();
		} catch (error) {
			this.handlePreSaveError(error);
		}
	}

	/**
	 * Executa as queries de pré-salvamento
	 */
	private async executePreSaveQueries(list: any[]): Promise<void[]> {
		return Promise.all(list.map(pre => 
			this._utilApi.getQueryPreOrUpdate({selectOne: pre.sql})
			.pipe(
				delay(1000),
				tap(e => this.processQueryResult(e, pre))
			)
			.toPromise()
		));
	}

	/**
	 * Processa o resultado de uma query individual
	 */
	private processQueryResult(response: any[], pre: any): void {
		if (!response?.length) return;

		response.forEach(ls => {
			ls.columns?.forEach((column, index) => {
			this.populateMatchingFields(pre.preFieldList, column, ls.values[index]);
			});
		});
	}

	/**
	 * Popula campos do formulário que correspondem à coluna
	 */
	private populateMatchingFields(fieldList: any[], column: string, value: any): void {
		const matchingField = fieldList.find(item => 
			item.key?.toUpperCase().trim() === column?.toUpperCase().trim()
		);

		if (matchingField?.field) {
			this.selectedProductForm.get(matchingField.field)?.setValue(value);
		}
	}

	/**
	 * Trata o pós pré-salvamento
	 */
	private handlePostPreSave(): void {
		if (this.selectedProductForm.invalid) {
			this.throwFormErrors();
			return;
		}

		this.item$.executeOption 
			? this.addItem() 
			: this.handleSuccessWithoutOptions();
	}

	/**
	 * Executa o fluxo padrão de pré-salvamento quando não há opções de execução
	 * @param res_insert Resultado da inserção (opcional)
	 */
	async defaultPreSaveWithoutOption(res_insert?: any): Promise<void> {
		try {
			const proSaveList = this.getProSaveList();
			const userData = this.getUserDataFromStorage();

			if (proSaveList.length > 0) {
			await this.handlePostSaveOperations(proSaveList, res_insert);
			} else if (this.item$.screenPreview === true) {
			await this.handlePreview(res_insert, userData);
			}

			this.executeFinalSteps();
		} catch (error) {
			this.handleSaveError(error);
		} finally {
			this.cleanUpForm();
		}
	}

	/**
	 * Manipula operações pós-salvamento
	 */
	private async handlePostSaveOperations(proSaveList: any[], res_insert?: any): Promise<void> {
		await this.handlePosOrPreSql(proSaveList, "pos", res_insert);
	}

	/**
	 * Manipula a visualização ou modal de assinatura
	 */
	private async handlePreview(res_insert: any, userData: any): Promise<void> {
		userData.previewTableName = this.item$.tableName;
		await this.handlePreviewOrSignatureModal(res_insert, userData);
	}

	/**
	 * Executa os passos finais após salvamento
	 */
	private executeFinalSteps(): void {
		this.addFunctionsAfterClear();
		this.validateDefaultValue();
		this.populatePreviewScreen();
	}

	/**
	 * Limpa e reativa o formulário
	 */
	private cleanUpForm(): void {
		this.selectedProductForm.reset();
		this.selectedProductForm.enable();
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Obtém a lista de operações pós-salvamento de forma segura
	 */
	private getProSaveList(): any[] {
		try {
			return JSON.parse(this.item$.proSaveListTxt || '[]');
		} catch (error) {
			console.error('Error parsing proSaveListTxt:', error);
			return [];
		}
	}


	/**
	 * Adiciona um novo item com base nos dados do formulário
	 */
	async addItem(): Promise<void> {
		try {
			if (this.item$.executeOption) {
				await this.executeInsertWithOptions();
			} else {
				await this.defaultPreSaveWithoutOption();
				this.showSuccessMessage();
			}
		} catch (error) {
			this.handleAddItemError(error);
		}
	}

	/**
	 * Executa inserção quando há opções de execução
	 */
	private async executeInsertWithOptions(): Promise<void> {
		this.populateModalValue();
		
		const fields = await this._utilApi.getOne('builder/field/list/', this.item$.id).toPromise();
		const { columnNames, values } = this.prepareInsertData(fields.content);
		
		this.handlePersistenceSQL(
			"insert", 
			this.validateTableName(this.item$.tableName),
			columnNames.replace(/,$/, ""), 
			values.replace(/,$/, "")
		);
	}

	/**
	 * Prepara os dados para inserção
	 */
	private prepareInsertData(fields: any[]): { columnNames: string, values: string } {
		const userData = this.getUserDataFromStorage();
		const insertFields = fields.filter(f => f.type !== 'identity');
		
		let columnNames = '';
		let values = '';

		insertFields.forEach((field, index) => {
			const { columnPart, valuePart } = this.processFieldForInsert(field, userData);
			
			if (columnPart && valuePart) {
				columnNames += this.formatColumnList(columnPart, index, insertFields.length);
				values += this.formatValueList(valuePart, index, insertFields.length);
			}
		});

		return { columnNames, values };
	}

	/**
	 * Processa um campo individual para inserção
	 */
	private processFieldForInsert(field: any, userData: any): { columnPart: string, valuePart: string } {

		const fieldValue = field.value.replaceAll('@', '').toLowerCase();

		if (field.value.includes('$')) {
			return {
				columnPart: field.columnName,
				valuePart: userData[field.value.replace('$', '')]
			};
		}

		if (field.value.includes('@')) {
			return {
				columnPart: field.columnName,
				valuePart: 	this.previewScreenObj[fieldValue] || userData[fieldValue] || null
			};
		}

		if (field.columnName.includes('$') || field.previewOnly) {
			return { columnPart: '', valuePart: '' };
		}

		const formValue = this.selectedProductForm.get(field.formControlName)?.value;
		return {
			columnPart: field.columnName,
			valuePart: this.formatFieldValue(field, formValue)
		};
	}

	/**
	 * Formata valores numéricos
	 */
	private formatNumberValue(value: any, allowNull: boolean): string {
		if (value === "" && allowNull) return 'null';
		return value ? value.toString() : 'null';
	}

	/**
	 * Formata valores de data
	 */
	private formatDateValue(value: string, functionMethod: string): string {
		if (!value) return 'null';
		
		const cleanedValue = value.replaceAll("/", "");
		if (functionMethod === 'atualDateAndHourFormated') {
			return `'${cleanedValue}'`;
		}
		return `'${this.dateToSqlFormate(cleanedValue)}'`;
	}

	/**
	 * Formata valores padrão
	 */
	private formatDefaultValue(value: any, allowNull: boolean): string {
		if (value === null && allowNull) return 'null';
		if (value === null) return "''";
		return `'${value}'`;
	}

	/**
	 * Formata a lista de colunas
	 */
	private formatColumnList(column: string, index: number, total: number): string {
		return index < total - 1 ? `${column},` : column;
	}

	/**
	 * Formata a lista de valores
	 */
	private formatValueList(value: string, index: number, total: number): string {
		return index < total - 1 ? `${value},` : value;
	}

	/**
	 * Valida e substitui placeholders no nome da tabela
	 */
	private validateTableName(tableName: string): string {
		return tableName.includes('@') 
			? this.validateAndReplaceTableName(tableName) || tableName
			: tableName;
	}


	/**
	 * Manipula operações de persistência SQL (INSERT/UPDATE)
	 * @param operationType Tipo de operação ('insert' ou 'update')
	 * @param tableName Nome da tabela alvo
	 * @param columnNameOrWhere Cláusula WHERE (update) ou lista de colunas (insert)
	 * @param values Valores para inserção/atualização
	 */
	async handlePersistenceSQL(operationType: 'insert' | 'update', tableName: string, columnNameOrWhere: string, values: string ): Promise<void> {
		try {
			this.prepareRequest(operationType, tableName, columnNameOrWhere, values);
			
			const result = await this.executeDatabaseOperation(operationType);
			this.handleOperationSuccess(operationType, result);
		} catch (error) {
			this.handleOperationError(error);
		}
	}

	/**
	 * Prepara o corpo da requisição conforme o tipo de operação
	 */
	private prepareRequest(operationType: string, tableName: string, columnNameOrWhere: string, values: string ): void {
		const operation = operationType.toUpperCase();
		
		if (operationType.toLowerCase() === 'update') {
			this.populateModalValue();
		}

		this.requestBody = new RequestBody({
			operationType: `${operation} `,
			tableName: tableName,
			...(operationType.toLowerCase() === 'insert'
			? { columnName: columnNameOrWhere, valuesInsert: values }
			: { whereValue: columnNameOrWhere, valuesUpdate: values })
		});
	}

	/**
	 * Executa a operação no banco de dados
	 */
	private executeDatabaseOperation(operationType: string): Promise<any> {
		return this._utilApi.dynamicQuery(
			`query/${operationType.toLowerCase()}`, 
			this.requestBody
		).pipe(delay(1000)).toPromise();
	}

	/**
	 * Executa queries de pós-salvamento e trata o resultado
	 * @param list Lista de queries SQL a serem executadas
	 */
	async queyPostSave(list: any[]): Promise<void> {
		if (!list?.length) return;

		try {
			const previewModalView = this.getPreviewModalView();
			await this.executePostSaveQueries(list);
			
			if (this.item$.screenPreview === true) {
				await this.showPreviewModal();
			} else {
				this.closeAllDialogs();
			}
		} catch (error) {
			this.handlePostSaveError(error);
		}
	}

	/**
	 * Obtém a configuração de preview do modal
	 */
	private getPreviewModalView(): any {
		try {
			return JSON.parse(this.item$.previewObjectText || '{}');
		} catch (error) {
			console.error('Error parsing previewObjectText:', error);
			return {};
		}
	}

	/**
	 * Executa as queries de pós-salvamento
	 */
	private async executePostSaveQueries(list: any[]): Promise<void[]> {
		return Promise.all(list.map((pre, index) => 
			this._utilApi.handlePostSqlExecute({selectOne: pre.sql})
			.pipe(
				tap((e) => this.notifyPostSaveExecution(index === list.length - 1, e))
			)
			.toPromise()
		));
	}

	/**
	 * Notifica execução do pós-salvamento
	 */
	private notifyPostSaveExecution(isLast: boolean, e): void {
		this.throwInfo("Pos Save executed!");
		if (!isLast) return;

		if (isLast) return this.idLastSaved = e.returnID[0]?.values?.[0] ?? null;
	}

	/**
	 * Mostra o modal de preview
	 */
	private async showPreviewModal(): Promise<void> {
		const userData = this.getUserDataFromStorage();
		userData.previewTableName = this.item$.tableName;
		await this.handlePreviewOrSignatureModal(this.idLastSaved, userData);
	}

	/**
	 * Populates the preview screen with data from form fields
	 * Handles both regular fields and custom select fields
	 */
	populatePreviewScreen(): void {
		if (!this.item$?.divs?.length) {
			this.markAsInvalid();
			return;
		}

		try {
			this.processAllDivs();
			this.markAsValid();
		} catch (error) {
			this.handlePopulationError(error);
		}
	}

	/**
	 * Processes all divs and their fields
	 */
	private processAllDivs(): void {
		this.item$.divs.forEach((div, index) => {
			this.processDivFieldsPreview(div);
			this.checkIfLastDiv(index);
		});
	}

	/**
	 * Processes all fields within a div
	 */
	private processDivFieldsPreview(div: any): void {
		div.fields?.forEach((field: Field) => {
			this.processField(field);
		});
	}

	/**
	 * Processes an individual field
	 */
	private processField(field: Field): void {
		this.verifyDataPreviewScreen(field);
		
		if (this.isCustomSelectField(field)) {
			this.createSelectListField(field);
		}
	}

	/**
	 * Checks if the field is a custom select field
	 */
	private isCustomSelectField(field: Field): boolean {
		return field.type === 'select' && !field.defaultsValue;
	}

	/**
	 * Checks if current div is the last one
	 */
	private checkIfLastDiv(index: number): void {
		if (index === this.item$.divs.length - 1) {
			this.markAsValid();
		}
	}
	
	modalPreSaveHaveLogin() {
		const preSaveLoginModal = this.dialog.open(PreSaveComponent, {
			height: '80vh',
			width: '40vw',
			panelClass: 'my-dialog-save'
		});

		preSaveLoginModal.afterClosed().subscribe(result => {
			if (result) {
				this.modalPersistSave();
			}
		});
	}

	/**
	 * Verifica e atualiza o valor do campo com dados do preview screen
	 * @param field - Objeto contendo as informações do campo
	 */
	verifyDataPreviewScreen(field: any): void {
		if (!field?.value?.includes('@')) return;

		const cleanValue = field.value.toLowerCase().replace(/@/g, '');
		const previewValue = this.previewScreenObj[cleanValue];

		if (previewValue == null || previewValue === "null" || previewValue === "") {
			this.setFormFieldValue(field.formControlName, "");
			return;
		}

		this.setFormFieldValue(field.formControlName, previewValue);
	}

	// Métodos auxiliares:
	/**
	 * Define o valor do campo do formulário de forma segura
	 */
	private setFormFieldValue(controlName: string, value: any): void {
		const control = this.selectedProductForm.get(controlName);
		control?.setValue(value);
	}

	/**
	 * Valida e processa uma string SQL contendo placeholders (@var@)
	 * @param sql_string - String SQL a ser validada
	 * @param field - Objeto de campo associado
	 */
	validateSql(sql_string: string, field: any): void {
		if (!sql_string.includes('@')) return;

		const sqlObj: any[] = [];
		const match = sql_string.match(/@([^@]+)@/);
		const placeholderMatch = match ? [match[0], match[1]] : null;
		
		if (!placeholderMatch) return;

		const [fullMatch, varName] = placeholderMatch;
		const formValue = this.selectedProductForm.get(varName.toLowerCase())?.value;

		const previewValue = this.previewScreenObj[varName.replace(/_/g, '').toLowerCase()];

		// Tenta substituir pelos valores disponíveis
		const substitutedSql = this.trySubstitute(sql_string, fullMatch, formValue, previewValue);

		if (substitutedSql !== sql_string) {
			this.executeQuery(substitutedSql, field, sqlObj);
		} else if (substitutedSql.includes('@')) {
			field.sqlText = substitutedSql;
			this.processPlaceholders(substitutedSql, field);
		}
	}

	// Métodos auxiliares:

	private trySubstitute(originalSql: string, placeholder: string, formValue: any, previewValue: any ): string {
		if (formValue != null) {
			return originalSql.replace(placeholder, formValue);
		}
		
		if (previewValue != null) {
			return originalSql.replace(placeholder, previewValue);
		}
		
		return originalSql;
	}


	/**
	 * Cria uma lista de opções para um campo select a partir de uma consulta SQL
	 * @param field - Campo que receberá as opções
	 */
	createSelectListField(field: Field): void {
		if (!field?.sqlText) {
			console.warn('Campo ou sqlText não definido');
			return;
		}

		this.setLoadingState(true);

		if (field.sqlText.includes('@')) {
			this.validateSql(field.sqlText, field);
		} else {
			this.executeQuery(field.sqlText, field, []);
		}
	}

	// Métodos auxiliares:

	private setLoadingState(isLoading: boolean): void {
		this.isLoadingSelect = isLoading;
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Executa uma consulta SQL e processa os resultados
	 * @param sql - Consulta SQL a ser executada
	 * @param field - Campo associado (opcional)
	 * @param sqlObj - Array para armazenar resultados (opcional)
	 */
	executeQuery(sql: string, field?: Field, sqlObj: any[] = []): void {
		if (!sql) return;

		this.isLoadingSelect = true;

		this._utilApi.getQuerySelectField({ selectOne: sql }).subscribe({
			next: (response) => this.handleQuerySuccess(response, field, sqlObj),
			error: (err) => this.handleQueryError(err)
		});
	}

	/**
	 * Trata a resposta bem-sucedida da consulta
	 */
	private handleQuerySuccess(response: any[], field?: Field, sqlObj?: any[]): void {
		if (!response?.length) return;

		const results = this.mapQueryResults(response);
		
		if (sqlObj) {
			sqlObj.push(...results);
		}

		if (field) {
			field.sqlList = results;
		}

		this.isLoadingSelect = false;
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Mapeia os resultados da consulta para o formato desejado
	 */
	private mapQueryResults(response: any[]): any[] {
		return response.map(item => ({
			id: item.values[0],
			value: item.values[1]
		}));
	}

	onKeySearch(value, field: Field) {
		if (value.length > 3) {
			field.isLoadingSelect = true;
			this.processPlaceholders(field.sqlText, field)
		}
	}

	/**
	 * Processa recursivamente uma string substituindo placeholders (@var@) por valores
	 * @param sql_execute - String a ser processada
	 * @param field - Campo associado (opcional)
	 */
	processPlaceholders(sql_execute: string, field?: any): void {
		if (!sql_execute.includes('@')) {
			this.getSelectField(field, sql_execute);
			return;
		}

		const match = sql_execute.match(/@([^@]+)@/);
		const placeholder = match ? [match[0], match[1]] : null;
		if (!placeholder) {
			this.getSelectField(field, sql_execute);
			return;
		}

		const [fullMatch, varName] = placeholder;
		const substitutedValue = this.getSubstitutionValue(fullMatch, varName);
		
		if (substitutedValue !== fullMatch) {
			sql_execute = sql_execute.replace(fullMatch, substitutedValue);
		}

		// Chamada recursiva se ainda existirem placeholders
		sql_execute.includes('@') ? this.processPlaceholders(sql_execute, field) : this.getSelectField(field, sql_execute);
	}

	private getSubstitutionValue(fullMatch: string, varName: string): string {
		const cleanVarName = varName.toLowerCase().replace(/_/g, '');
		
		// 1. Tenta obter do FormControl
		const formValue = this.selectedProductForm.get(cleanVarName)?.value;
		if (formValue != null) return formValue;

		// 2. Tenta obter do previewScreenObj
		const previewValue = this.previewScreenObj[cleanVarName];
		if (previewValue != null) return previewValue;

		// 3. Retorna o placeholder original se não encontrar substituição
		return fullMatch;
	}

	/**
	 * Busca e carrega opções para um campo select a partir de uma consulta SQL
	 * @param field - Campo que receberá as opções
	 * @param sql - Consulta SQL para buscar os dados
	 */
	getSelectField(field: Field, sql: string): void {
		if (!field || !sql) return;

		field.isLoadingSelect = true;
		this.selectedProductForm.disable();

		this._utilApi.getQuerySelectField({ selectOne: sql }).subscribe({
			next: (response) => this.handleSelectResponse(field, response),
			error: (err) => this.handleSelectError(field, err)
		});
	}

	/**
	 * Trata a resposta bem-sucedida da API
	 */
	private handleSelectResponse(field: Field, response: any[]): void {
		const options = this.mapApiResponseToOptions(response);
		
		field.names = options;
		field.isLoadingSelect = false;
		this.selectedProductForm.enable();
		
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Mapeia a resposta da API para o formato de opções do campo
	 */
	private mapApiResponseToOptions(response: any[]): any[] {
		if (!response?.length) return [];

		return response.map(item => ({
			id: item.values[0],
			value: item.values[1],
			valueFull: JSON.stringify(item)
		}));
	}

	onChangeOption(name, field: Field) {
		field.isLoadingSelect = true;

		let list = JSON.parse(field.sqlObjText);
		let columns = JSON.parse(name.valueFull);

		list.forEach((item, index) => {
			columns.columns.forEach((column, c) => {
				if (item.id.trim().toLowerCase() == column.trim().toLowerCase()) {
					this.selectedProductForm.get(item.value).setValue(columns.values[c])
				}
			})

			if(index == (list.length - 1)) {
				field.isLoadingSelect = false;
				// Mark for check
				this._changeDetectorRef.markForCheck();
			}

		})
	}

	executeFieldFunctions(listFunctions) {
		listFunctions.forEach((item, index) => {

			let replaceValue = item.field_function.substring(item.field_function.indexOf("@") + 1);
			let replaceClean = replaceValue.substring(0, replaceValue.indexOf(" "));

			switch(item.field_operator) {
				case 'empty': 
					if (this.selectedProductForm.get(item.field_trigger)?.value == '') {
						let txt = item.field_function.replace( `@${replaceClean}`, this.selectedProductForm.get(item.field_trigger)?.value)

						this.selectedProductForm.get(item.field_affected)
							.setValue( `${this.selectedProductForm.get(item.field_affected)?.value} ${txt}` )
					}
					break;
				case 'not_empty': 
					if (this.selectedProductForm.get(item.field_trigger)?.value != '') {
						let txt = item.field_function.replace( `@${replaceClean}`, this.selectedProductForm.get(item.field_trigger)?.value)

						this.selectedProductForm.get(item.field_affected)
							.setValue( `${this.selectedProductForm.get(item.field_affected)?.value} ${txt}` )

					}
					break;
				case 'equals': 
					break;
				case 'not_equals': 
					break;
			}


			if(index == (listFunctions.length - 1)) {
				// Mark for check
				this._changeDetectorRef.markForCheck();

				if(JSON.parse(this.item$.preSaveListTxt).length > 0) {
					console.warn('execute pre save');
					this.handlePosOrPreSql(JSON.parse(this.item$.preSaveListTxt), "pre")
				} else {
					if (this.item$.executeOption) {
						this.addItem();
					} else {
						console.warn('without pre save and without options');
						this.defaultPreSaveWithoutOption();
					}
					
				}
			}
		});
	}

	// -----------------------------------------------------------------------------------------------------
    // @ All Util Methods
    // -----------------------------------------------------------------------------------------------------

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
		this._toastr.error(err, 'Erro', {
			timeOut: 3000,
			closeButton: true,
			progressBar: true,
		  });
	}

	/**
	 * Funcition to throwError
	 * 
	 * @param err 
	 */
	throwSuccess(msg) {
		this._toastr.success(msg, 'Successo', {
			timeOut: 3000,
			closeButton: true,
			progressBar: true,
		});
	}


	/**
	 * Funcition to throw Info
	 * 
	 * @param err 
	 */
	throwInfo(msg) {
		this._toastr.info(msg, 'Informação', {
			timeOut: 3000,
			closeButton: true,
			progressBar: true,
		});
	}

	/**
	 * Funcition to throw Warn
	 * 
	 * @param err 
	 */
	throwWarn(msg) {
		this._toastr.warning(msg, 'Aviso', {
			timeOut: 3000,
			enableHtml: true,
			closeButton: true,
			progressBar: true,
		});
	}
	

	/**
	 * 
	 * Function to get a enter click
	 * 
	 * @param name ~
	 * @param field 
	 */
	onEnter(name, field: Field) {
		this.onChangeOption(name, field);
	}

	/**
	 * 
	 * Fuction to validate and a Replace a table name
	 * 
	 * @param name 
	 */
	validateAndReplaceTableName(name) {
		let val1 = name.substring(name.indexOf("@") + 1 );
		let val2 = "@" + val1.substring(0, val1.indexOf("@") + 1);

		if (this.previewScreenObj[val2.replaceAll('@', '').replaceAll('_','').toLowerCase()]) {
			let v = this.previewScreenObj[val2.replaceAll('@', '').replace('_','').toLowerCase()];
			return name.replace(val2, v);
		}
	}

	/**
	 * Method to format date to sql
	 * 
	 */
	dateToSqlFormate(date) {
		return date.substring(4) + "-" + date.substring(2,4) + "-" + date.substring(0,2)
	}


	handlePreviewOrSignatureModal(res, varScreen) {
		let variable
		this._utilApi.getIndetityField(this.item$.id)
			.subscribe((fields: any) => {
				variable = JSON.stringify(fields.content);
				this.handleOpenDialogPreviewOrSignature(res, varScreen, variable, this.item$.tableName)
			}, (erros) => {
				this.throwError(erros);
			})
	}

	handleOpenDialogPreviewOrSignature(res, logged_screen, screen_fields_indentity, table_name) {
		const dialogPreview = this.dialog.open(PreviewComponent, {
			height: '90vh',
			width: '80vw',
			panelClass: 'my-dialog-preview',
			data: {
				response: res,
				loggedScreen: logged_screen,
				screenBuilderIdentity: screen_fields_indentity,
				screenOrigin: "default",
				table_name: table_name
			}
		});

		dialogPreview.afterClosed().subscribe(result => {
			if (result) {
				this.dialog.closeAll();
			}
		});
	}

	return() {
		this._location.back();
	}

	shouldShowDefaultField(field: any): boolean {
		return (this.routerType === 'add' && 
				field.type !== 'identity' && 
				field.type !== 'select' && 
				field.type !== 'search') ||
				this.routerType === 'edit';
	}

	/**
 	* Trata erros da chamada à API
	*/
	private handleSelectError(field: Field, error: any): void {
		console.error('Error loading select options:', error);
		this.throwError(error.error?.message || 'Failed to load options');

		field.isLoadingSelect = false;
		field.names = [];
		this.selectedProductForm.enable();
		
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Trata erros da consulta
	 */
	private handleQueryError(error: any): void {
		console.error('Query execution error:', error);
		this.throwError(error.error?.message || 'Failed to execute query');
		
		this.isLoadingSelect = false;
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Trata o salvamento quando não há funções de pré-salvamento
	 */
	private handleSaveWithoutPreFunctions(): void {
		if (this.item$.executeOption) {
			this.addItem();
		} else {
			console.warn('No pre-save functions and no execute options');
			this.defaultPreSaveWithoutOption();
		}
	}

	/**
	 * Trata erros do formulário
	 */
	throwFormErrors() {
		this.selectedProductForm.markAllAsTouched();

		Object.keys(this.selectedProductForm.controls).forEach(key => {

			let controlErrors: ValidationErrors = null;
			controlErrors = this.selectedProductForm.get(key).errors;

			if (controlErrors != null) {
			  Object.keys(controlErrors).forEach(keyError => {
				this.throwWarn(`
						<div class="flex flex-col">
							<span> Campo com Erro: ${key} </span>
							<span> Tipo do Erro: ${keyError} </span>
						</div>
					`)
			  });
			}
		});

		// Mark for check
		this._changeDetectorRef.markForCheck();
		return;
	}

	/**
	 * Trata erros durante a atualização
	 */
	private handleUpdateError(error: any): void {
		console.error('Update failed:', error);
		this.selectedProductForm.enable();
		this.throwError(error.message || 'Failed to update item');
	}

	/**
	 * Trata sucesso quando não há executeOption
	 */
	private handleSuccessWithoutOptions(): void {
		console.warn('Success pre save without options');
		this.defaultPreSaveWithoutOption();
	}

	/**
	 * Trata erros no pré-salvamento
	 */
	private handlePreSaveError(error: any): void {
		console.error('Pre save error:', error);
		this.selectedProductForm.enable();
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Trata erros durante o salvamento
	 */
	private handleSaveError(error: any): void {
		console.error('Error in default pre-save:', error);
	// Adicione tratamento de erro adicional se necessário
	}

	/**
	 * Mostra mensagem de sucesso
	 */
	private showSuccessMessage(): void {
		this.throwSuccess(this.transSrv.translate(
			'util.create-success',
			{},
			this.transSrv.getActiveLang()
		));
	}

	/**
	 * Trata erros durante a adição
	 */
	private handleAddItemError(error: any): void {
		console.error('Error adding item:', error);
		this.selectedProductForm.enable();
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Trata o sucesso da operação
	 */
	private handleOperationSuccess(operationType: string, result: any): void {
		const translationKey = operationType.toLowerCase() === 'insert' 
			? 'util.create-success' 
			: 'util.update-success';
		
		this.throwSuccess(this.transSrv.translate(
			translationKey,
			{},
			this.transSrv.getActiveLang()
		));

		this.defaultPreSaveWithoutOption(result);
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Trata erros na operação
	 */
	private handleOperationError(error: any): void {
		this.selectedProductForm.enable();
		this.throwError(error.error?.message || error.error || 'Operation failed');
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Trata erros no pós-salvamento
	 */
	private handlePostSaveError(error: any): void {
		console.error('Post save error:', error);
		this.throwError("Pos Save error! - queyPostSave - builder-form.component.ts");
	}

	ngOnDestroy(): void {
		this._click.destroy()
	}

	/**
	 * Marks the form as valid and triggers change detection
	 */
	private markAsValid(): void {
		this.isValid = true;
		this.triggerChangeDetection();
	}

	/**
	 * Marks the form as invalid and triggers change detection
	 */
	private markAsInvalid(): void {
		this.isValid = false;
		this.triggerChangeDetection();
	}

	/**
	 * Triggers Angular change detection
	 */
	private triggerChangeDetection(): void {
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Fecha todos os diálogos
	 */
	private closeAllDialogs(): void {
		this.dialog.closeAll();
	}
}
