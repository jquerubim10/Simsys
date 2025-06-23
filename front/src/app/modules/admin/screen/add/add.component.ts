import {
	ChangeDetectionStrategy,
	ChangeDetectorRef,
	Component,
	OnInit,
	ViewEncapsulation,
} from '@angular/core';
import {
	TitleCasePipe,
	NgClass,
	NgFor,
	NgIf,
	Location
} from '@angular/common';
import {
	FormArray,
	FormsModule,
	ReactiveFormsModule,
	UntypedFormBuilder,
	UntypedFormGroup,
	Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOptionModule, MatRippleModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { fuseAnimations } from '@fuse/animations';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FuseConfirmationService } from '@fuse/services/confirmation';
import { DefaultApi } from 'app/app-api/default/api';
import { material } from 'app/mock-api/ui/icons/data';
import { finalize } from 'rxjs';
import { UtilApi } from 'app/app-api/default/api-util';

@Component({
    selector: 'app-add-screen',
    templateUrl: './add.component.html',
    styles: [
        /* language=SCSS */
        `	

		.disabled-f {
			pointer-events: none;
			opacity: 0.5;
		}

		.w-f {
			width: 40rem !important;
		}
		
		.inventory-grid {
			grid-template-columns: auto 40px 50px 40px;

			@screen sm {
				grid-template-columns: auto 40px 50px 40px;
			}

			@screen md {
				grid-template-columns: auto 40px 50px 40px;
			}

			@screen lg {
				grid-template-columns: auto 40px 50px 40px;
			}
		}

		.inventory-grid-field {
			grid-template-columns: auto 250px 60px 100px 70px 40px 40px;

			@screen sm {
				grid-template-columns: auto 250px 80px 100px 70px 40px 40px;
			}

			@screen md {
				grid-template-columns: auto 200px 80px 100px 70px 40px 40px;
			}

			@screen lg {
				grid-template-columns: auto 250px 80px 100px 70px 40px 40px;
			}
		}
		`,
    ],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: fuseAnimations,
	standalone: true,
    imports: [
        NgIf,
        RouterModule,
        TitleCasePipe,
        MatProgressBarModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        FormsModule,
        ReactiveFormsModule,
        MatProgressSpinnerModule,
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
export class AddScreenComponent implements OnInit {

	isLoading: boolean = false;
	iconList: any = material;
	dataEdit?: any = null;
	items: any[] = [];

	isPreviewVisible = false;

	previewObject: any = {
		"title" : "",
		"type": "default",
		"type_body": "unique",
		"type_template": "self",
		"template": null,
		"have_function": false,
		"functions": null,
		"functions_system": null,
		"pattern": null,
		"field_together": null,
		"header_item": [],
		"sub_header_item": [],
		"body": [],
	};

	isVisible = false;

	divList: any[] = [];
	fieldList: any[] = [];
	sidebarList: any[] = [];
	screensList: any[] = [];
	screen_with_preview: any[] = [];

	typeTabList: any[] = [
		{id: 'list', label: 'LISTAGEM'},
		{id: 'listQuery', label: 'LISTAGEM_QUERY'},
		{id: 'ev_hist', label: 'EVOLUTION_HISTORY'},
		{id: 'ev_anamnese', label: 'EVOLUTION_ANAMNESE'},
		{id: 'chart_monitor', label: 'CHART_MONITORAMENTO'},
		{id: 'chart_vitais', label: 'CHART_SINAIS_VITAIS'},
	]

	type_template_list: any[] = [
		{id: 'self', label: 'Sem Template'},
		{id: '_blank', label: 'Outra Tela'},
	]

	listFunctions: any[] = [
		{id: '', name: ''},
		{id: 'atualDateAndHour', name: 'atualDateAndHour'},
		{id: 'atualDateAndHourFormated', name: 'atualDateAndHourFormated'},
		{id: 'getAgeFormated', name: 'getAgeFormated'},
		{id: 'atualDate', name: 'atualDate'},
		{id: 'atualHour', name: 'atualHour'},
	]

	type_preview_body: any[] = [
		{id: 'unique', label: 'Campo Unico'},
		{id: 'multi', label: 'Varios Campos'},
	]

	type_functions: any[] = [
		{id: '', label: ''},
		{id: 'together', label: '2 Campos'},
		{id: 'system_function', label: 'Função Pronta'},
		{id: 'pattern', label: 'Pattern'}
	]

	operatorTypeList: any[] = [
		{id: 'empty'},
		{id: 'not_empty'},
		{id: 'equals'},
		{id: 'not_equals'},
	]

	selectedProductForm: UntypedFormGroup;

	/**
	 * Constructor
	 */
	constructor(
		private _formBuilder: UntypedFormBuilder,
		private _location: Location,
		private activatedroute: ActivatedRoute,
		private _changeDetectorRef: ChangeDetectorRef,
		private _defaultApi: DefaultApi,
		private _utilApi: UtilApi,
		private transSrv: TranslocoService,
		private _fuseConfirmationService: FuseConfirmationService
	) {}

	// -----------------------------------------------------------------------------------------------------
	// @ Lifecycle hooks
	// -----------------------------------------------------------------------------------------------------

	/**
	 * On init
	 */
	ngOnInit(): void {
		this.isLoading = true;

		// Create the selected product form
		this.selectedProductForm = this._formBuilder.group({
			id: [],
			title: ['', [Validators.required]],
			icon: ['', [Validators.required]],
			tableName: ['', [Validators.required]],
			dateCreated: [''],
			idScreenC: [''],


			signTable: [''],
			signColumn: [''],

			active: [false],
			screenSign: [false],
			screenLogin: [false],
			screenPreview: [false],
			componentS: [false],
			historyC: [false],
			
			listC: [false],
			executeOption: [false],

			typeTab: [null],

			whereClause: [''],

			previewObjectText: [''],

			preSaveListTxt: [''],
			preSaveFunctionListTxt: [''],
			proSaveListTxt: [''],

			preSaveList : this._formBuilder.array([]),
			preSaveFunctionList : this._formBuilder.array([]),
			proSaveList : this._formBuilder.array([]),
		});

		this.activatedroute.params.subscribe((e) => {
			if (e.id) {
				this._defaultApi
					.getOneItem('builder/screen/', e.id)
					.subscribe((item) => {
						this.dataEdit = item;

						this.previewObject.title = this.dataEdit.title;

						// Create the selected product form
						this.selectedProductForm
							.get('id')
							.setValue(this.dataEdit.id);
						this.selectedProductForm
							.get('title')
							.setValue(this.dataEdit.title);
						this.selectedProductForm
							.get('icon')
							.setValue(this.dataEdit.icon);
						this.selectedProductForm
							.get('tableName')
							.setValue(this.dataEdit.tableName);
						this.selectedProductForm
							.get('dateCreated')
							.setValue(this.dataEdit.dateCreated);
						this.selectedProductForm.get('idScreenC').setValue(this.dataEdit.idScreenC);

						this.selectedProductForm.get('signTable').setValue(this.dataEdit.signTable || '');
						this.selectedProductForm.get('signColumn').setValue(this.dataEdit.signColumn || '');
						
						this.selectedProductForm.get('componentS').setValue(this.dataEdit.componentS || false);
						this.selectedProductForm.get('historyC').setValue(this.dataEdit.historyC || false);
						this.selectedProductForm.get('listC').setValue(this.dataEdit.listC || false);
						this.selectedProductForm.get('active').setValue(this.dataEdit.active || false);

						this.selectedProductForm.get('whereClause').setValue(this.dataEdit.whereClause || '');

						this.selectedProductForm.get('typeTab').setValue(this.dataEdit.typeTab || '');

						this.selectedProductForm.get('executeOption').setValue(this.dataEdit.executeOption || true);

						this.selectedProductForm.get('screenSign').setValue(this.dataEdit.screenSign || false);
						this.selectedProductForm.get('screenPreview').setValue(this.dataEdit.screenPreview || false);

						this.selectedProductForm.get('screenLogin').setValue(this.dataEdit.screenLogin || false);

						this.selectedProductForm.get('previewObjectText').setValue(this.dataEdit.previewObjectText || JSON.stringify(this.previewObject));
						if (this.selectedProductForm.get('previewObjectText').value != '') {
							this.previewObject = JSON.parse(this.selectedProductForm.get('previewObjectText').value);
						}

						// Events pre e post save
						this.selectedProductForm.get('preSaveListTxt').setValue(this.dataEdit.preSaveListTxt || null);
						if (this.selectedProductForm.get('preSaveListTxt').value != null) {
							this.populatePreList();
						}

						this.selectedProductForm.get('preSaveFunctionListTxt').setValue(this.dataEdit.preSaveFunctionListTxt || null);
						if (this.selectedProductForm.get('preSaveFunctionListTxt').value != null) {
							this.populatePreFunctionList();
						}

						this.selectedProductForm.get('proSaveListTxt').setValue(this.dataEdit.proSaveListTxt || null);
						if (this.selectedProductForm.get('proSaveListTxt').value != null) {
							this.populatePosList();
						}

						this.getDivList();
						this.handleGetScreenWithPreview();
					});
			}  else {
				this.isLoading = false;
				// Mark for check
				this._changeDetectorRef.markForCheck();
			}
		});
		
		this.getScreenList();
		this.getSidebarList();
	}

	populatePosList() {
		let preSqlList = JSON.parse(this.selectedProductForm.get('proSaveListTxt').value);
		preSqlList.forEach((item) => {
			this.proSaveList.push(this._formBuilder.group({
				sql: [item.sql, Validators.required]
			}));
		})
	}

	populatePreFunctionList() {
		let preFunctionList = JSON.parse(this.selectedProductForm.get('preSaveFunctionListTxt').value)
		preFunctionList.forEach((item) => {

			this.preSaveFunctionList.push(this._formBuilder.group({
				field_trigger: [item.field_trigger, Validators.required],
				field_operator: [item.field_operator, Validators.required],
				field_affected: [item.field_affected, Validators.required],
				field_function: [item.field_function, Validators.required],
			}));

		})
	}

	populatePreList() {
		let pre = JSON.parse(this.selectedProductForm.get('preSaveListTxt').value);
		pre.forEach((p) => {
			let formArray = new FormArray ([]);
			p.preFieldList.forEach((f, index) => {

				formArray.push(this._formBuilder.group({
					key: [f.key, Validators.required],
					field: [f.field, Validators.required],
				}));

				if (index == (p.preFieldList.length -1)) {
					this.preSaveList.push(this._formBuilder.group({
						sql: [p.sql, Validators.required],
						preFieldList : formArray,
					}));
				}
			})

		})
	}

	getDivList() {
		this._utilApi.getAll('builder/div/list/' + this.dataEdit.id).subscribe((e) => {
			this.divList = e.content;
			
			this.divList.length > 0 ? this.getFieldList() : this.isLoading = false;
			// Mark for check
			this._changeDetectorRef.markForCheck();
		});
	}

	getFieldList() {
		this._utilApi.getAll('builder/field/list/' + this.dataEdit.id).subscribe((e) => {
			this.fieldList = e.content;
			this.getNameDiv();
		})
	}

	getSidebarList() {
		this._utilApi.getAll('sidebar/').subscribe((e) => {
			this.sidebarList = e.content;

			// Mark for check
			this._changeDetectorRef.markForCheck();
		})
	}

	getScreenList() {
		this._utilApi.getAll('builder/screen/').subscribe((e) => {
			this.screensList = e.content;

			// Mark for check
			this._changeDetectorRef.markForCheck();
		})
	}

	handleGetScreenWithPreview() {
		this._utilApi.buildingScreenWithPreview('builder/screen/previews').subscribe((content) => {
			this.screen_with_preview = content.filter(item => item.id != this.dataEdit.id);

			// Mark for check
			this._changeDetectorRef.markForCheck();
		})
	}

	private _filter(value: string): string[] {
		const filterValue = value.toLowerCase();
	
		return this.iconList.filter(option => option.toLowerCase().includes(filterValue));
	  }

	/**
	 * Send object to create
	 */
	additem(): void {
		this.isLoading = true;

		// Return if the form is invalid
		if (this.selectedProductForm.invalid) {
			return;
		}

		// Disable the form
		this.selectedProductForm.disable();

		this.selectedProductForm.get('previewObjectText').setValue(JSON.stringify(this.previewObject));

		// Events pre e post save
		this.selectedProductForm.get('preSaveListTxt').setValue(JSON.stringify(this.selectedProductForm.get('preSaveList').value));
		this.selectedProductForm.get('preSaveFunctionListTxt').setValue(JSON.stringify(this.selectedProductForm.get('preSaveFunctionList').value));
		this.selectedProductForm.get('proSaveListTxt').setValue(JSON.stringify(this.selectedProductForm.get('proSaveList').value));

		this.selectedProductForm.removeControl('preSaveList');
		this.selectedProductForm.removeControl('preSaveFunctionList');
		this.selectedProductForm.removeControl('proSaveList');

		this._utilApi
			.post('builder/screen/', this.selectedProductForm.value)
			.pipe(
				finalize(() => {
					// Re-enable the form
					this.selectedProductForm.enable();
					this.selectedProductForm.addControl('preSaveList', this._formBuilder.array([]));
					this.selectedProductForm.addControl('preSaveFunctionList', this._formBuilder.array([]));
					this.selectedProductForm.addControl('proSaveList', this._formBuilder.array([]));
					this.isLoading = false;
				})
			)
			.subscribe((response) => {
				this._location.back();
			});
	}

	deleteSelectedProduct() {
		// Open the confirmation dialog
		const confirmation = this._fuseConfirmationService.open({
			title: this.transSrv.translate(
				'title-delete',
				{},
				this.transSrv.getActiveLang()
			),
			message: this.transSrv.translate(
				'util.message-delete',
				{},
				this.transSrv.getActiveLang()
			),
			actions: {
				confirm: {
					label: this.transSrv.translate(
						'delete',
						{},
						this.transSrv.getActiveLang()
					),
				},
				cancel: {
					label: this.transSrv.translate(
						'cancel',
						{},
						this.transSrv.getActiveLang()
					),
				},
			},
		});

		// Subscribe to the confirmation dialog closed action
		confirmation.afterClosed().subscribe((result) => {
			// If the confirm button pressed...
			if (result === 'confirmed') {
				this.isLoading = true;
				// Delete the contact
				this._defaultApi
					.deleteItem('builder/screen/', this.dataEdit.id)
					.subscribe((isDeleted) => {
						// Return if the contact wasn't deleted...
						if (!isDeleted) {
							return;
						}
					});
				this._defaultApi.updateNav(this.dataEdit.id);
				this.return();
			}
		});
	}

	deleteDiv(id) {
		// Open the confirmation dialog
		const confirmation = this._fuseConfirmationService.open({
			title: this.transSrv.translate(
				'title-delete',
				{},
				this.transSrv.getActiveLang()
			),
			message: this.transSrv.translate(
				'message-delete',
				{},
				this.transSrv.getActiveLang()
			),
			actions: {
				confirm: {
					label: this.transSrv.translate(
						'delete',
						{},
						this.transSrv.getActiveLang()
					),
				},
				cancel: {
					label: this.transSrv.translate(
						'cancel',
						{},
						this.transSrv.getActiveLang()
					),
				},
			},
		});

		// Subscribe to the confirmation dialog closed action
		confirmation.afterClosed().subscribe((result) => {
			// If the confirm button pressed...
			if (result === 'confirmed') {
				this._utilApi.delete('builder/div/', id).subscribe((e) => {
					this.isLoading = true;
					this.getDivList();
		
					// Mark for check
					this._changeDetectorRef.markForCheck();
				})
			}
		})
	}

	deleteField(id) {
		// Open the confirmation dialog
		const confirmation = this._fuseConfirmationService.open({
			title: this.transSrv.translate('util.delete-title', {}, this.transSrv.getActiveLang()),
			message: this.transSrv.translate('util.delete-message', {}, this.transSrv.getActiveLang()),
			actions: {
				confirm: {
					label: this.transSrv.translate('util.delete', {}, this.transSrv.getActiveLang()),
				},
				cancel: {
					label: this.transSrv.translate('util.cancel', {}, this.transSrv.getActiveLang())
				}
			},
		});

		// Subscribe to the confirmation dialog closed action
		confirmation.afterClosed().subscribe((result) => {
			// If the confirm button pressed...
			if (result === 'confirmed') {
				// Delete the contact
				this._utilApi
					.delete('builder/field/', id)
					.subscribe(() => {
						this.isLoading = true;
						this.getFieldList();
	
						// Mark for check
						this._changeDetectorRef.markForCheck();
					});
			}
		});
	}
	
	updateItem() {
		this.isLoading = true;
		// Return if the form is invalid
		if (this.selectedProductForm.invalid) {
			return;
		}

		// Disable the form
		this.selectedProductForm.disable();

		this.selectedProductForm.get('previewObjectText').setValue(JSON.stringify(this.previewObject));

		// Events pre e post save
		this.selectedProductForm.get('preSaveListTxt').setValue(JSON.stringify(this.selectedProductForm.get('preSaveList').value));
		this.selectedProductForm.get('preSaveFunctionListTxt').setValue(JSON.stringify(this.selectedProductForm.get('preSaveFunctionList').value));
		this.selectedProductForm.get('proSaveListTxt').setValue(JSON.stringify(this.selectedProductForm.get('proSaveList').value));

		this.selectedProductForm.removeControl('preSaveList');
		this.selectedProductForm.removeControl('preSaveFunctionList');
		this.selectedProductForm.removeControl('proSaveList');

		this._utilApi
			.put('builder/screen/', this.selectedProductForm.value)
			.pipe(
				finalize(() => {
					// Re-enable the form
					this.selectedProductForm.enable();
					this.selectedProductForm.addControl('preSaveList', this._formBuilder.array([]));
					this.selectedProductForm.addControl('preSaveFunctionList', this._formBuilder.array([]));
					this.selectedProductForm.addControl('proSaveList', this._formBuilder.array([]));
					this.isLoading = false;
				})
			)
			.subscribe((e) => {
				this.return();
			});
	}

	visible() {
		this.isVisible = !this.isVisible;
		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	getNameDiv() {
		this.divList.forEach((d, index) => {
			this.fieldList.forEach((field) => {
				if(d.id === field.divId) {
					field = field['div'] = d.title;
				}
			})

			if (index == (this.divList.length - 1)) {
				this.isLoading = false;
				// Mark for check
				this._changeDetectorRef.markForCheck();
			}
		})
	}

	return() {
		this._location.back();
	}


	addHeaderItem() {
		this.previewObject.header_item.push(
			{
				"label": "",
				"field": "",
				"have_function": false,
				"functions": null,
				"functions_system": null,
				"pattern": null,
				"field_together": null,
			}
		)

		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	addSubHeaderItem() {
		this.previewObject.sub_header_item.push(
			{
				"label": "",
				"field": "",
				"have_function": false,
				"functions": null,
				"functions_system": null,
				"pattern": null,
				"field_together": null,
			}
		)

		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	addBodyItem() {
		this.previewObject.body.push(
			{
				"label": "",
				"field": ""
			}
		)

		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	removeHeaderItem(index) {
		this.previewObject.header_item.splice(index, 1);
		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	removeSubHeaderItem(index) {
		this.previewObject.sub_header_item.splice(index, 1);
		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	removeBodyItem(index) {
		this.previewObject.body.splice(index, 1);
		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Function to add obj to pre
	 * 
	 */
	addObjToPre() {
		this.preSaveList.push(this._formBuilder.group({
			sql: ['', Validators.required],
			preFieldList : this._formBuilder.array([]),
		}));
		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * Function to add obj to pre
	 * 
	 */
	addObjFunctionToPre() {
		this.preSaveFunctionList.push(this._formBuilder.group({
			field_trigger: ['', Validators.required],
			field_operator: ['', Validators.required],
			field_affected: ['', Validators.required],
			field_function: ['', Validators.required],
		}));
		// Mark for check
		this._changeDetectorRef.markForCheck();
	}
	/**
	 * Function to add obj to pos
	 * 
	 */
	addObjToPos() {
		this.proSaveList.push(this._formBuilder.group({
			sql: ['', Validators.required],
		}));
		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	addSubObjToPre(i) {
		let pre =  this.preSaveList.controls[i].get('preFieldList') as FormArray;
		pre.push(this._formBuilder.group({
			key: ['', Validators.required],
			field: ['', Validators.required],
		}));
		// Mark for check
		this._changeDetectorRef.markForCheck();
	}

	/**
	 * removendo da lista de pre sql
	 * 
	 * @param index 
	 * 
	 */
	removePreList(index: number) {
		this.preSaveList.removeAt(index);
	}

	/**
	 * removendo da lista de pre function
	 * 
	 * @param index 
	 * 
	 */
	removePreSaveFunctionList(index: number) {
		this.preSaveFunctionList.removeAt(index);
	}

	/**
	 * removendo da lista de pos
	 * 
	 * @param index 
	 * 
	 */
	removePostList(index: number) {
		this.proSaveList.removeAt(index);
	}

	visiblePreview() {
		this.isPreviewVisible = !this.isPreviewVisible;
		if (this.previewObject.body.length <= 0) { this.addBodyItem(); }
	}

	getPreList(form) {
		return form.controls.preFieldList.controls;
	}

	get preSaveList() {
		return this.selectedProductForm.controls["preSaveList"] as FormArray;
	}

	get preSaveFunctionList() {
		return this.selectedProductForm.controls["preSaveFunctionList"] as FormArray;
	}
	
	get proSaveList() {
		return this.selectedProductForm.controls["proSaveList"] as FormArray;
	}
}
