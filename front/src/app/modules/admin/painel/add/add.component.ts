import {
	ChangeDetectionStrategy,
	ChangeDetectorRef,
	Component,
	OnInit,
	ViewEncapsulation,
} from '@angular/core';
import {
	NgClass,
	NgFor,
	NgIf,
	Location,
} from '@angular/common';
import {
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
import { material } from 'app/mock-api/ui/icons/data';
import { finalize } from 'rxjs';
import { FuseConfirmationService } from '@fuse/services/confirmation/confirmation.service';
import { DefaultApi } from 'app/app-api/default/api';

@Component({
    selector: 'app-add-painel',
    templateUrl: './add.component.html',
    styles: [
        /* language=SCSS */
        `
			.inventory-grid {
				grid-template-columns: 48px auto 40px;

				@screen sm {
					grid-template-columns: 48px auto 112px 72px;
				}

				@screen md {
					grid-template-columns: 48px 112px auto 112px 72px;
				}

				@screen lg {
					grid-template-columns: 48px 112px auto 112px 96px 96px 72px;
				}
			}
		`,
    ],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
	standalone: true,
    animations: fuseAnimations,
    imports: [
        NgIf,
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
        NgClass,
        MatSlideToggleModule,
        MatSelectModule,
        MatOptionModule,
        MatCheckboxModule,
        MatRippleModule,
        TranslocoModule,
    ]
})
export class AddPanelComponent implements OnInit {
	isLoading: boolean = false;
	iconList: any[] = material;
	dataEdit?: any = null;
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
		// Create the selected product form
		this.selectedProductForm = this._formBuilder.group({
			id: [],
			idName: ['', [Validators.required]],
			title: ['', [Validators.required]],
			description: ['', [Validators.required]],
			icon: ['', [Validators.required]],
			url: ['', [Validators.required]],
			active: [true],
		});

		this.activatedroute.params.subscribe((e) => {
			if (e.id) {
				this._defaultApi.getOneItem('panel/', e.id).subscribe((item) => {
					this.dataEdit = item;

					// Create the selected product form
					this.selectedProductForm.get('id').setValue(this.dataEdit.id);
					this.selectedProductForm.get('idName').setValue(this.dataEdit.idName);
					this.selectedProductForm.get('title').setValue(this.dataEdit.title);
					this.selectedProductForm.get('description').setValue(this.dataEdit.description);
					this.selectedProductForm.get('icon').setValue(this.dataEdit.icon);
					this.selectedProductForm.get('url').setValue(this.dataEdit.url);
					this.selectedProductForm.get('active').setValue(this.dataEdit.active);

					// Mark for check
					this._changeDetectorRef.markForCheck();
				});
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
			.createItem('panel/', this.selectedProductForm.value)
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
				this._defaultApi.deleteItem('panel/', this.dataEdit.id).subscribe((isDeleted) => {
					// Return if the contact wasn't deleted...
					if (!isDeleted) {
						return;
					}
				});
				this._defaultApi.updateNav(this.dataEdit.id)
				this.return();
			}
		});
	}

	updateItem() {
		this._defaultApi.updateItem('panel/', this.selectedProductForm.value).subscribe((e) => {
			this.return()
		})
	}

	return() {
		this._location.back();
	}
}
