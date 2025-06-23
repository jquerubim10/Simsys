import {
	ChangeDetectionStrategy,
	ChangeDetectorRef,
	Component,
	Input,
	OnInit,
	ViewEncapsulation,
} from '@angular/core';
import {
	NgFor,
	NgIf,
	TitleCasePipe,
} from '@angular/common';
import {
	FormsModule,
	ReactiveFormsModule,
	UntypedFormControl,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOptionModule, MatRippleModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { fuseAnimations } from '@fuse/animations';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { UtilApi } from 'app/app-api/default/api-util';
import { FuseConfirmationService } from '@fuse/services/confirmation/confirmation.service';
import { IMaskModule } from 'angular-imask';
import { Subscription } from 'rxjs';
import { FuseAlertComponent } from '@fuse/components/alert';
import { DynamicListClick } from 'app/core/utils/dynamic-modal/utils/service/dynamic-list-click.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BuilderDatePipe } from './util/builder-date.pipe';
import { ScreenBuilder } from 'app/modules/admin/screen/model/screen.model';
import { RequestBody } from './util/model/request-body.model';
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: 'app-builder',
    templateUrl: './builder.component.html',
    styles: [
        /* language=SCSS */
        `	
			.text-aligm {
				text-align: center;
			}

			.text-aligm-c {
				text-align: center;
			}

			.mat-mdc-tab-group .mat-mdc-tab-body-content {
				padding: 0 24px !important;
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
        IMaskModule,
        RouterModule,
        BuilderDatePipe,
        FuseAlertComponent,
        MatProgressSpinnerModule,
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
export class BuilderComponent implements OnInit {

	@Input() fatherScreen: any;
	@Input() valuePreviewScreen: any;
	previewScreenObj;

	isLoadingHeader = true;
	isLoading = true;
	isLoadingListModal = true;
	items: any[] = [];
	screenObejct: ScreenBuilder;

	isModal = false;

	requestBody: RequestBody;
	responseBody: any[] = [];
	tabelNameFull = '';

	typeInputControl: UntypedFormControl = new UntypedFormControl();
	searchInputControl: UntypedFormControl = new UntypedFormControl();

	style = 'grid-template-columns: ';

	heardsSearch: any[] = [];

	isError = false;
	erroMessage;

	clickEventSubscription: Subscription;

	/**
	 * Constructor
	 */
	constructor(
		public router: Router,
		private activatedroute: ActivatedRoute,
		private _changeDetectorRef: ChangeDetectorRef,
		private _utilApi: UtilApi,
		private transSrv: TranslocoService,
		private _toastr: ToastrService,
		private _fuseConfirmationService: FuseConfirmationService,
		private _dynamicListClick: DynamicListClick
	) {
		this.clickEventSubscription = this._dynamicListClick.getClickEvent().subscribe(() => {
			this.isLoadingListModal = true;
			this.responseBody = [];
			
			if (this.fatherScreen.typeTab == 'listQuery') {
				this.modalValidateWhereClause();
			} else {
				this.modalListCreateHeader(this.fatherScreen.id, this.fatherScreen.tableName);
			}
		})
	}

	/**
	 * Initial Method
	 */
	ngOnInit(): void {
		if (this.valuePreviewScreen) {
			this.isModal = true;
			this.previewScreenObj = this.valuePreviewScreen;
		} else {
			this.initialMethodList();
		}
		

		// Subscribe to search input field value changes
		this.searchInputControl.valueChanges.subscribe((query) => {
			console.log(query);
		});
	}

	// -----------------------------------------------------------------------------------------------------
    // @ All methods - List
    // -----------------------------------------------------------------------------------------------------

	initialMethodList() {
		
		this.clearValues();
		this.isLoading = true;
		this.isLoadingHeader = true;
		
		this.activatedroute.params.subscribe((router) => {
			this.initialGetScreenObject(router);
		});
	}

	initialGetScreenObject(router) {
		this._utilApi
			.getOne('builder/screen/', router.idScreen)
			.subscribe((screen: ScreenBuilder) => {
				this.screenObejct = new ScreenBuilder(screen);
				
				// Mark for check
				this._changeDetectorRef.markForCheck();

				this.initialGetFieldsSearch(this.screenObejct);
			}, (err) => {
				this.throwError(err);
			});
	}

	initialGetFieldsSearch(screen: ScreenBuilder) {
		this._utilApi
			.getAll('builder/field/search/' + screen.id)
			.subscribe((response) => {
				this.heardsSearch = response.content;

				this.isLoadingHeader = false;
				this.cssAdjustList();

				this.initialCreateSelectObject(screen.tableName,this.heardsSearch);
			}, (err) => {
				this.throwError(err);
			})
	}

	initialCreateSelectObject(table, columns) {
		this.requestBody = new RequestBody();
		this.requestBody.tabelNameFull = table;
		this.requestBody.columnName = '';
		this.requestBody.operationType = 'SELECT ';
		this.requestBody.tableName = `${table}   ${table.charAt(0)}`;

		columns.forEach((c, length) => {			

			if((columns.length - 1) == length) {
				if (columns.length == 1) {
					this.requestBody.columnName = table.charAt(0) + '.' + c.columnName;
				} else {
					this.requestBody.columnName += table.charAt(0) + '.' + c.columnName;
				}

				if (this.isModal) {
					this.modalValidateTableName();
				} else {
					this.initialPopulateResponseBody();
				}
				
			} else {
				this.requestBody.columnName += table.charAt(0) + '.' + c.columnName + ', ';
			}
		});
	}

	initialPopulateResponseBody() {
		this._utilApi.postResponseBody('query', this.requestBody)
			.subscribe((response) => {
				if (response.length < 1) {
					this.isLoading = false;
					this.responseBody = [];
					
					// Mark for check
					this._changeDetectorRef.markForCheck();
					return;
				}
				response.forEach((item) => {
					let arrayHeader = [];
						item.columns.forEach((column, index) => {
							
							let t = {columnValue: column.toLowerCase() + '_' + item.values[index]}
							arrayHeader.push(t);

							if (index == (item.columns.length - 1)) {
								this.isLoading = false;
								this.responseBody.push(this.toObject(arrayHeader));

								// Mark for check
								this._changeDetectorRef.markForCheck();
							}	
						});
				})
			}, (err) => {
				this.isLoading = false;
				this.responseBody = [];
					
				this.throwError(err);

				// Mark for check
				this._changeDetectorRef.markForCheck();
			})
	}
	
	// -----------------------------------------------------------------------------------------------------
    // @ All methods - Modal
    // -----------------------------------------------------------------------------------------------------

	/**
	 * Method to ger header fields
	 * 
	 * @param id 
	 * @param tableName 
	 */
	modalListCreateHeader(id, tableName) {

		if (tableName.includes("@")) {
			let val1 = tableName.substring(tableName.indexOf("@") + 1 );
			let val2 = "@" + val1.substring(0, val1.indexOf("@") + 1);

			if (this.previewScreenObj[val2.replaceAll('@', '').replaceAll('_','').toLowerCase()]) {
				let v = this.previewScreenObj[val2.replaceAll('@', '').replace('_','').toLowerCase()];
				tableName = tableName.replace(val2, v);
			}

		}


		this._utilApi
			.getAll('builder/field/search/' + id)
				.subscribe((items) => {
					if (items.content.length > 0) {
						this.heardsSearch = items.content;

						// Mark for check
						this._changeDetectorRef.markForCheck();

						this.cssAdjustModalList();

						this.initialCreateSelectObject(tableName, this.heardsSearch)
					} else {
						this.isLoadingListModal = false;
						this.isError = true;

						// Set the alert
						this.erroMessage = {
							type   : 'error',
							message: 'Sem campos pesquisaveis!! Ajuste no modulo Admin',
						};

						// Mark for check
						this._changeDetectorRef.markForCheck();
					}
					
				}, (err) => {
					this.isLoadingListModal = false;
					this.isError = true;

					// Set the alert
					this.erroMessage = {
						type   : 'error',
						message: err.error.message,
					};

					// Mark for check
					this._changeDetectorRef.markForCheck();
				})
	}

	modalValidateTableName() {
		this.requestBody.whereValue = "";
		if (this.fatherScreen.whereClause.match("@")) {
			let val1 = this.fatherScreen.whereClause.substring(this.fatherScreen.whereClause.indexOf("@") + 1 );
			let val2 = "@" + val1.substring(0, val1.indexOf("@") + 1);

			if (this.previewScreenObj[val2.replaceAll('@', '').replaceAll('_','').toLowerCase()]) {
				let v = this.previewScreenObj[val2.replaceAll('@', '').replace('_','').toLowerCase()];
				this.fatherScreen.whereClause = this.fatherScreen.whereClause.replace(val2, v);
			}

			if (this.fatherScreen.whereClause.match("@")) {
				this.modalValidateTableName();
			} else {
				this.requestBody.whereValue +=  'WHERE ' + this.fatherScreen.whereClause;
				this.modalPopulateResponseBody();
			}
		} else {
			this.requestBody.whereValue +=  'WHERE ' + this.fatherScreen.whereClause;
			this.modalPopulateResponseBody();
		}
	}

	modalValidateWhereClause() {
		if (this.fatherScreen.whereClause.match("@")) {
			let val1 = this.fatherScreen.whereClause.substring(this.fatherScreen.whereClause.indexOf("@") + 1 );
			let val2 = "@" + val1.substring(0, val1.indexOf("@") + 1);

			if (this.previewScreenObj[val2.replaceAll('@', '').replaceAll('_','').toLowerCase()]) {
				let v = this.previewScreenObj[val2.replaceAll('@', '').replace('_','').toLowerCase()];
				this.fatherScreen.whereClause = this.fatherScreen.whereClause.replace(val2, v);
			}

			if (this.fatherScreen.whereClause.match("@")) {
				this.modalValidateWhereClause();
			} else {
				this.modalPopulateResponseBodyByQuery();
			}
		} else {
			this.modalPopulateResponseBodyByQuery();
		}
	}

	modalPopulateResponseBody() {
		this.responseBody = [];

		this._utilApi
			.getDynamicListModal('query/list/modal', this.requestBody)
			.subscribe((e) => {
				if (e != null && e.length > 0) {
					e.forEach((item, i) => {
						let arrayHeader = [];
						item.columns.forEach((column, index) => {
							
							let t = {columnValue: column.toLowerCase() + '_' + item.values[index]}
							arrayHeader.push(t);

							if (index == (item.columns.length - 1)) {
								this.responseBody.push(this.toObject(arrayHeader));
							}	
						});

						if (i == (e.length - 1)) {
							this.isLoadingListModal = false;

							// Mark for check
							this._changeDetectorRef.markForCheck();
						} 
					});
				} else {
					this.isLoadingListModal = false;
					this.responseBody = [];

					// Mark for check
					this._changeDetectorRef.markForCheck();
				}
			}, (err) => {

				this.throwError(err);

				this.isLoadingListModal = false;
				this.responseBody = [];

				// Mark for check
				this._changeDetectorRef.markForCheck();
			});
	}

	modalPopulateResponseBodyByQuery() {
		this._utilApi
			.getDynamicListModal('query/list/modal/query', {selectOne: this.fatherScreen.whereClause })
			.subscribe((response) => {
				if (response.length > 0) {
					this.heardsSearch = response[0].columns;
					response.forEach((item, i) => {
						let arrayHeader = [];
						item.columns.forEach((column, index) => {
							arrayHeader.push({
								columnValue: column.toLowerCase() + '_' + item.values[index]
							});

							if (index == (item.columns.length - 1)) {
								this.responseBody.push(this.toObject(arrayHeader));
							}
						});

						if (i == (response.length - 1)) {
							this.cssAdjustModalList();
							this.isLoadingListModal = false;

							// Mark for check
							this._changeDetectorRef.markForCheck();
						} 
					});
				} else {
					this.isLoadingListModal = false;
					this.responseBody = [];
					// Mark for check
					this._changeDetectorRef.markForCheck();
				}
			}, (err) => {
				this.isLoadingListModal = false;
				this.responseBody = [];
				this.isError = true;
				// Set the alert
				this.erroMessage = {
					type   : 'error',
					message: err.error,
				};
				// Mark for check
				this._changeDetectorRef.markForCheck();
			});
	}

	// -----------------------------------------------------------------------------------------------------
    // @ All Public Methods
    // -----------------------------------------------------------------------------------------------------


	deleteDataClick(item) {
		// Open the confirmation dialog
		const confirmation = this._fuseConfirmationService.open({
			title: this.transSrv.translate(
				'util.delete-title',
				{},
				this.transSrv.getActiveLang()
			),
			message: this.transSrv.translate(
				'util.delete-message',
				{},
				this.transSrv.getActiveLang()
			),
			actions: {
				confirm: {
					label: this.transSrv.translate(
						'util.delete',
						{},
						this.transSrv.getActiveLang()
					),
				},
				cancel: {
					label: this.transSrv.translate(
						'util.cancel',
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
				this.editDataClick(item, true);
			}
		});
	}

	editDataClick(items, del?:any) {
		let whereClause = "";
		items.forEach((item, index) => {
			let columnName = item.columnValue.substring(0, item.columnValue.indexOf("_"))
		

			this.heardsSearch.forEach((head: any) => {
				if (head.searchable == true && head.columnName.toLowerCase() == columnName.toLowerCase()) {
					if ( head.type == 'identity' || head.type == 'number') {
						whereClause += head.columnName.toUpperCase() + " = " + item.columnValue.substring(item.columnValue.indexOf("_") + 1 )
					} else {
						whereClause += head.columnName.toUpperCase() + " = '" + item.columnValue.substring(item.columnValue.indexOf("_") + 1 ) + "'"
					}
					
				}
			})

			if (index == (items.length - 1)) {
				if (del) {
					this.requestBody.operationType = "DELETE ";
					this.requestBody.tableName = this.tabelNameFull;
					this.requestBody.whereValue = whereClause;

					this._utilApi.dynamicQuery('query/delete', this.requestBody).subscribe(() => {
						this.throwSuccess(this.transSrv.translate('util.delete-success',{},this.transSrv.getActiveLang()));

						// Mark for check
						this._changeDetectorRef.markForCheck();
						this.initialMethodList();
					})
				} else {
					const url = window.location.hash.replaceAll('#','').toLowerCase();
					this.router.navigate([ url + '/edit', { state: whereClause } ]);
				}
			} else {
				whereClause += " AND "
			}
		})
		
	}


	// -----------------------------------------------------------------------------------------------------
    // @ All Util Methods
    // -----------------------------------------------------------------------------------------------------


	/**
	 * Method to create css to list
	 * 
	 */
	cssAdjustList() {
		this.style = 'grid-template-columns: ';

		this.heardsSearch.forEach((e, index) => {
			this.style += 'auto ';
			if ((index === (this.heardsSearch.length - 1))) {
				this.style += '40px 40px';
			}
		});
	}

	/**
	 * Method to create css to modal list
	 * 
	 */
	cssAdjustModalList() {
		this.style = 'grid-template-columns: ';

		this.heardsSearch.forEach((e, index) => {
			this.style += 'auto ';
			if ((index === (this.heardsSearch.length - 1))) {
				this.style += '40px';
			}
		});
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
	*
	* Function to reset values
	*
	*/	
	clearValues() {
		this.requestBody = new RequestBody(null);
		this.responseBody = [];

		this.heardsSearch = [];
	}

	/**
     * Track by function for ngFor loops
     *
     * @param index
     * @param item
     */
    trackByFn(index: number, item: any): any
    {
        return item.id || index;
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
