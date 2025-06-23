import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Location, NgClass, NgFor, NgIf, TitleCasePipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule, UntypedFormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
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
import { RouterModule, ActivatedRoute } from '@angular/router';
import { fuseAnimations } from '@fuse/animations';
import { FuseConfirmationService } from '@fuse/services/confirmation';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { material } from 'app/mock-api/ui/icons/data';
import { finalize } from 'rxjs';
import { UtilApi } from 'app/app-api/default/api-util';
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: 'app-add-group',
    templateUrl: './add-group.component.html',
    styles: [
        /* language=SCSS */
        `
			.disabled-f {
				pointer-events: none;
				opacity: 0.5;
			}

			.inventory-grid-field {
				grid-template-columns: auto 40px;
	
				@screen sm {
					grid-template-columns: auto 40px;
				}
	
				@screen md {
					grid-template-columns: auto 40px;
				}
	
				@screen lg {
					grid-template-columns: auto 40px;
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
        MatProgressBarModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        FormsModule,
        ReactiveFormsModule,
        TitleCasePipe,
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
export class AddGroupComponent implements OnInit{
  isLoading: boolean = true;
	iconList: any[] = material;
	fieldList: any[] = [];
	groupScreenList: any[] = [];

	dataEdit?: any = null;
	selectField: any;
	selectedProductForm: UntypedFormGroup;

	/**
	 * Constructor
	 */
	constructor(
		private _formBuilder: UntypedFormBuilder,
		private _location: Location,
		private activatedroute: ActivatedRoute,
		private _toastr: ToastrService,
		private _changeDetectorRef: ChangeDetectorRef,
		private _defaultApi: UtilApi,
		private transSrv: TranslocoService,
		private _fuseConfirmationService: FuseConfirmationService,
	) {}

	// -----------------------------------------------------------------------------------------------------
	// @ Lifecycle hooks
	// -----------------------------------------------------------------------------------------------------

	/**
	 * On init
	 */
	ngOnInit(): void {
		this.getScreenList();

		// Create the selected product form
		this.selectedProductForm = this._formBuilder.group({
			id: [],
			title: ['', [Validators.required]],
			icon: ['', [Validators.required]],
			active: [true],
		});

		this.activatedroute.params.subscribe((e) => {
			if (e.id) {
				this._defaultApi.getOne('menu/group/', e.id).subscribe((item) => {
					this.dataEdit = item;

					// Create the selected product form
					this.selectedProductForm.get('id').setValue(this.dataEdit.id);
					this.selectedProductForm.get('title').setValue(this.dataEdit.title);
					this.selectedProductForm.get('icon').setValue(this.dataEdit.icon);
					this.selectedProductForm.get('active').setValue(this.dataEdit.active);

					this.getScreenByGroup(this.dataEdit.id);

          			this.isLoading = false

					// Mark for check
					this._changeDetectorRef.markForCheck();
				});
			} else {
				this.isLoading = false
			}
		});
	}

	/**
	 * Send object to create
	 */
	additem(): void {
		// Return if the form is invalid
		if (this.selectedProductForm.invalid) {
			return;
		}

		// Disable the form
		this.selectedProductForm.disable();

		this._defaultApi
			.post('menu/group/', this.selectedProductForm.value)
			.pipe(
				finalize(() => {
					// Re-enable the form
					this.selectedProductForm.enable();
				})
			)
			.subscribe((response) => {
				this._location.back();
			});
	}

	deleteSelectedProduct() {

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
				this.isLoading = true;
				// Delete the contact
				this._defaultApi.delete('menu/group/', this.dataEdit.id).subscribe((isDeleted) => {
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
		this._defaultApi.put('menu/group/', this.selectedProductForm.value).subscribe((e) => {
			this.return()
		})
	}

	getScreenList() {
		this._defaultApi.getAll('builder/screen/').subscribe((e) => {
			this.fieldList = e.content;
			// Mark for check
			this._changeDetectorRef.markForCheck();
		})
	}

	getScreenByGroup(id) {
		this._defaultApi.getAll('menu/group/' + id + '/screens').subscribe((e) => {
			this.groupScreenList = e.content;
			// Mark for check
			this._changeDetectorRef.markForCheck();
		})
	}

	addToGroup() {
		this._defaultApi.addToGroup('menu/group/' + this.dataEdit.id + '/screen/' + this.selectField.id)
			.subscribe((res) => {

				this.throwSuccess(this.transSrv.translate('util.update-success',{},this.transSrv.getActiveLang()));

				this.getScreenByGroup(this.dataEdit.id);
				this.selectField = null;

			}, (err) => {

				this.throwError(err);
			})
	}

	removeToGroup(id) {
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

				// Delete the screen to group
				this._defaultApi.addToGroup('menu/group/' + id + '/screen/remove')
					.subscribe((res) => {
						this.throwSuccess(this.transSrv.translate('util.delete-success',{},this.transSrv.getActiveLang()));

						this.getScreenByGroup(this.dataEdit.id);

					}, (err) => {
						this.throwError(err);
					})

				// Mark for check
				this._changeDetectorRef.markForCheck();
			}
		});
	}

	return() {
		this._location.back();
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
