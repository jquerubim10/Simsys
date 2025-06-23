import {
	AsyncPipe,
	CurrencyPipe,
	JsonPipe,
	NgFor,
	NgIf,
	NgTemplateOutlet,
	Location,
	TitleCasePipe
} from '@angular/common';
import {
	ChangeDetectionStrategy,
	ChangeDetectorRef,
	Component,
	OnInit,
	ViewEncapsulation,
} from '@angular/core';
import {
	FormArray,
	FormGroup,
	FormsModule,
	ReactiveFormsModule,
	UntypedFormArray,
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
import { FuseConfirmationService } from '@fuse/services/confirmation';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { DefaultApi } from 'app/app-api/default/api';
import { heroicons } from 'app/mock-api/ui/icons/data';
import { finalize } from 'rxjs';
import { Field } from '../../model/field.model';
import { UtilApi } from 'app/app-api/default/api-util';

@Component({
    selector: 'app-add-field',
    templateUrl: './add-field.component.html',
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
    ]
})
export class AddFieldComponent implements OnInit {
	isLoading: boolean = false;
	
	dataEdit?: Field = null;
	idScreem: null;
	items: any[] = [];
	iconList: any[] = heroicons;
	divList: any[] = [];
	fieldList: any[] = [];
	
	divForm: UntypedFormGroup;
	selectedProductForm: UntypedFormGroup;

	classNames: any[] = [
		{ id: 'w-6', name: '6px' },
		{ id: 'w-10', name: '10px' },
		{ id: 'w-20', name: '20px' },
		{ id: 'w-60', name: '60px' },
		{ id: 'w-100', name: '100px' },
		{ id: 'w-1/4', name: '20%' },
		{ id: 'w-2/3', name: '50%' },
		{ id: 'w-3/4', name: '80%' },
		{ id: 'w-full', name: '100%' },
	];

	listFunctions: any[] = [
		{id: '', name: ''},
		{id: 'atualDateAndHour', name: 'atualDateAndHour'},
		{id: 'atualDateAndHourFormated', name: 'atualDateAndHourFormated'},
		{id: 'atualDate', name: 'atualDate'},
		{id: 'atualHour', name: 'atualHour'},
	]

	typeField = [
		{ id: "identity" }, 
		{ id: 'text' }, 
		{ id: 'text-area' }, 
		{ id: 'number' }, 
		{ id: 'checkbox' }, 
		{ id: 'select' },
		{ id: 'date' },
		{ id: 'search' }
	];

	/**
	 * Constructor
	 */
	constructor(
		private _formBuilder: UntypedFormBuilder,
		private _location: Location,
		private activatedroute: ActivatedRoute,
		private _changeDetectorRef: ChangeDetectorRef,
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
			placeholder: ['', [Validators.required]],
			label: ['', [Validators.required]],
			formControlName: [''],
			type: ['text'],

			lines: [],
			value: [''],
			css: [''],
			columnName: ['', [Validators.required]],
			className: ['w-1/4'],
			mask: [''],

			infoSensitive: [false],
			searchable: [false],
			editable: [true],
			visible: [true],
			defaultsValue: [false],
			previewOnly: [false],
			nullValue: [false],
			orderRow: [],

			validatorRequired: [false],
			validatorMin: [10],
			validatorMax: [255],
			validatorMessage: [''],

			functionMethod: [''],
			defaultSelect: this._formBuilder.array([]),
			sqlObjSql: this._formBuilder.array([]),

			sqlText: [''],
			sqlObjText: [''],

			divId: [],
			screenId: [],
			orderScreen: [],

			active: [true],
		});

		this.activatedroute.params.subscribe((e) => {
			if (e.id) {
				this.idScreem = e.id;
			}
			
			this.getDivList();
			this.getFieldList();

			if (e.idField) {
				this._utilApi
					.getOne('builder/field/', e.idField)
					.subscribe((item) => {
						this.dataEdit = item;

						// Create the selected product form
						this.selectedProductForm
							.get('id')
							.setValue(this.dataEdit.id);
						this.selectedProductForm
							.get('placeholder')
							.setValue(this.dataEdit.placeholder);
						this.selectedProductForm
							.get('label')
							.setValue(this.dataEdit.label);
						this.selectedProductForm
							.get('formControlName')
							.setValue(this.dataEdit.formControlName);
						this.selectedProductForm
							.get('type')
							.setValue(this.dataEdit.type);

						this.selectedProductForm
							.get('lines')
							.setValue(this.dataEdit.lines);
						this.selectedProductForm
							.get('value')
							.setValue(this.dataEdit.value);
						this.selectedProductForm
							.get('css')
							.setValue(this.dataEdit.css);
						this.selectedProductForm
							.get('columnName')
							.setValue(this.dataEdit.columnName);
						this.selectedProductForm
							.get('className')
							.setValue(this.dataEdit.className);
						this.selectedProductForm
							.get('mask')
							.setValue(this.dataEdit.mask);

						this.selectedProductForm
							.get('orderScreen')
							.setValue(this.dataEdit.orderScreen);
						this.selectedProductForm
							.get('orderRow')
							.setValue(this.dataEdit.orderRow);

						this.selectedProductForm
							.get('infoSensitive')
							.setValue(this.dataEdit.infoSensitive);
						this.selectedProductForm
							.get('searchable')
							.setValue(this.dataEdit.searchable);
						this.selectedProductForm
							.get('editable')
							.setValue(this.dataEdit.editable);
						this.selectedProductForm
							.get('visible')
							.setValue(this.dataEdit.visible);
						this.selectedProductForm
							.get('nullValue')
							.setValue(this.dataEdit.nullValue);
						this.selectedProductForm
							.get('previewOnly')
							.setValue(this.dataEdit.previewOnly);
							

						this.selectedProductForm
							.get('validatorRequired')
							.setValue(this.dataEdit.validatorRequired);
						this.selectedProductForm
							.get('validatorMin')
							.setValue(this.dataEdit.validatorMin);
						this.selectedProductForm
							.get('validatorMax')
							.setValue(this.dataEdit.validatorMax);
						this.selectedProductForm
							.get('validatorMessage')
							.setValue(this.dataEdit.validatorMessage);

						this.selectedProductForm
							.get('divId')
							.setValue(this.dataEdit.divId);
						this.selectedProductForm
							.get('screenId')
							.setValue(this.dataEdit.screenId);

						this.selectedProductForm
							.get('functionMethod')
							.setValue(this.dataEdit.functionMethod)

						this.selectedProductForm
							.get('sqlText')
							.setValue(this.dataEdit.sqlText)

						this.selectedProductForm
							.get('sqlObjText')
							.setValue(this.dataEdit.sqlObjText)

						this.selectedProductForm
							.get('defaultsValue')
							.setValue(this.dataEdit.defaultsValue);

						if ( this.selectedProductForm.get('defaultsValue').value == true && this.selectedProductForm.get('type').value == 'select') {
							this.populateSelect(JSON.parse(this.dataEdit.sqlText));
						}

						if ( this.selectedProductForm.get('sqlObjText').value != null && this.selectedProductForm.get('type').value == 'search') {
							this.populateSqlObj(JSON.parse(this.dataEdit.sqlObjText));
						}
						
						this.selectedProductForm
							.get('active')
							.setValue(this.dataEdit.active);

						this.isLoading = false;
						// Mark for check
						this._changeDetectorRef.markForCheck();
					});
			} else {
				this.isLoading = false;
				// Mark for check
				this._changeDetectorRef.markForCheck();
			}
		});
	}

	populateSelect(list) {
		list.forEach((e) => {
			const defa = this._formBuilder.group({
				id: [e.id],
				value: [e.value]
			});
			this.defaultsSelect.push(defa);
		})
	}

	populateSqlObj(list) {
		list.forEach((e) => {
			const defa = this._formBuilder.group({
				id: [e.id],
				value: [e.value]
			});
			this.sqlObjSql.push(defa);
		})
	}


	getDivList() {
		this._utilApi
			.getAll('builder/div/list/' + this.idScreem)
			.subscribe((e) => {
				this.divList = e.content;
				// Mark for check
				this._changeDetectorRef.markForCheck();
			});
	}

	getFieldList() {
		this._utilApi.getAll('builder/field/list/' + this.idScreem).subscribe((e) => {
			this.fieldList = e.content;
			// Mark for check
			this._changeDetectorRef.markForCheck();
		})
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

		this.selectedProductForm.get('screenId').setValue(this.idScreem);
		this.selectedProductForm
			.get('formControlName')
			.setValue(
				this.selectedProductForm
					.get('columnName')
					.value.toLowerCase()
					.replace(/ /g, '_')
			);

		if (this.selectedProductForm.get('defaultsValue').value == true && this.selectedProductForm.get('type').value == 'select') {
			this.selectedProductForm.get('sqlText').setValue(JSON.stringify(this.defaultsSelect.value));
		}

		if (this.selectedProductForm.get('type').value == 'search') {
			this.selectedProductForm.get('sqlObjText').setValue(JSON.stringify(this.sqlObjSql.value));
		}

		this.selectedProductForm.removeControl('defaultSelect');
		this.selectedProductForm.removeControl('sqlObjSql');

		this._utilApi
			.post('builder/field', this.selectedProductForm.value)
			.pipe(
				finalize(() => {
					this.selectedProductForm.addControl('defaultSelect', this._formBuilder.array([]));
					this.selectedProductForm.addControl('sqlObjSql', this._formBuilder.array([]));
					// Re-enable the form
					this.selectedProductForm.enable();
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
				this.isLoading = true;
				// Delete the contact
				this._utilApi
					.delete('builder/field/', this.dataEdit.id)
					.subscribe((isDeleted) => {
						// Return if the contact wasn't deleted...
						if (!isDeleted) {
							return;
						}
					});
				this.return();
			}
		});
	}

	updateItem() {
		this.isLoading = true;
		if (this.selectedProductForm.get('defaultsValue').value == true && this.selectedProductForm.get('type').value == 'select') {
			if(this.defaultsSelect.length > 1) {
				this.selectedProductForm.get('sqlText').setValue(JSON.stringify(this.defaultsSelect.value));
			}
		}

		if (this.selectedProductForm.get('type').value == 'search') {
			this.selectedProductForm.get('sqlObjText').setValue(JSON.stringify(this.sqlObjSql.value));
		}

		this.selectedProductForm.removeControl('defaultSelect');
		this.selectedProductForm.removeControl('sqlObjSql');

		this.selectedProductForm
			.get('formControlName')
			.setValue(
				this.selectedProductForm
					.get('columnName')
					.value.toLowerCase()
					.replace(/ /g, '_'));

		this.selectedProductForm.get('screenId').setValue(Number(this.idScreem));
		this._utilApi
			.put('builder/field', this.selectedProductForm.value)
			.pipe(
				finalize(() => {
					this.selectedProductForm.addControl('defaultSelect', this._formBuilder.array([]));
					this.selectedProductForm.addControl('sqlObjSql', this._formBuilder.array([]));
					this.isLoading = false;
				})
			)
			.subscribe((e) => {
				this.return();
			});
	}


	/**
	 * Send object to create a row in field
	 */
	addRow() {
		this.selectedProductForm.get('orderRow').setValue((this.divList.length + 1));
		let title = 'linha ' + (this.divList.length + 1);

		this.divForm = this._formBuilder.group({
			id: [],
			title: [title, [Validators.required]],
			screenId: [this.idScreem],
			active: [true],
		});

		this._utilApi
			.post('builder/div', this.divForm.value)
			.subscribe((response) => {
				this.selectedProductForm.get('divId').setValue(response.id);
				this.getDivList();
			});
	}

	/**
	 * Function to add return in defaults value
	 * 
	 */
	addSelectdefaultSelect() {
		const defa = this._formBuilder.group({
			id: ['', Validators.required],
			value: ['', Validators.required]
		});

		this.defaultsSelect.push(defa);
	}

	/**
	 * Function to add return in sql value
	 * 
	 */
	addSqlObj() {
		this.sqlObjSql.push(this._formBuilder.group({
			id: ['', Validators.required],
			value: ['', Validators.required]
		}));
	}

	/**
	 * removendo da lista de defaults
	 * 
	 * @param index 
	 * 
	 */
	deleteDefaultSelect(index: number) {
		this.defaultsSelect.removeAt(index);
	}

	/**
	 * removendo da lista de sql
	 * 
	 * @param index 
	 * 
	 */
	deleteSqlObj(index: number) {
		this.sqlObjSql.removeAt(index);
	}

	get defaultsSelect() {
		return this.selectedProductForm.controls["defaultSelect"] as FormArray;
	}

	get sqlObjSql() {
		return this.selectedProductForm.controls["sqlObjSql"] as FormArray;
	}

	return() {
		this._location.back();
	}
}
