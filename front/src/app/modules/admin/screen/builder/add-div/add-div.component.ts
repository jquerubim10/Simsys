import {
	Location,
	NgIf,
} from '@angular/common';
import {
	ChangeDetectionStrategy,
	ChangeDetectorRef,
	Component,
	OnInit,
	ViewEncapsulation,
} from '@angular/core';
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
import { FuseConfirmationService } from '@fuse/services/confirmation';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { DefaultApi } from 'app/app-api/default/api';
import { UtilApi } from 'app/app-api/default/api-util';
import { heroicons } from 'app/mock-api/ui/icons/data';

@Component({
    selector: 'app-add-div',
    templateUrl: './add-div.component.html',
    styles: [
        /* language=SCSS */
        `
			.inventory-grid {
				grid-template-columns: 48px auto 72px 72px 40px;

				@screen sm {
					grid-template-columns: 48px auto 72px 72px 40px;
				}

				@screen md {
					grid-template-columns: 48px auto 72px 72px 40px;
				}

				@screen lg {
					grid-template-columns: 48px auto 72px 72px 40px;
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
        MatButtonModule,
        MatSortModule,
        MatPaginatorModule,
        MatSlideToggleModule,
        MatSelectModule,
        MatOptionModule,
        MatCheckboxModule,
        MatRippleModule,
        TranslocoModule
    ]
})
export class AddDivComponent implements OnInit {
	isLoading: boolean = false;
	iconList: any[] = heroicons;
	dataEdit?: any = null;
	items: any[] = [];

	screenId: any | null;

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
		// Create the selected product form
		this.selectedProductForm = this._formBuilder.group({
			id: [],
			title: ['', [Validators.required]],
			screenId: [''],
			active: [true],
		});

		this.activatedroute.params.subscribe((e) => {
			if (e.id) {
				this.screenId = e.id;
			}
			if (e.idDiv) {
				this._defaultApi
					.getOneItem('builder/div/', e.idDiv)
					.subscribe((item) => {
						this.dataEdit = item;

						// Create the selected product form
						this.selectedProductForm
							.get('id')
							.setValue(this.dataEdit.id);
						this.selectedProductForm
							.get('title')
							.setValue(this.dataEdit.title);
						this.selectedProductForm
							.get('screenId')
							.setValue(this.screenId);
						this.selectedProductForm
							.get('active')
							.setValue(this.dataEdit.active);

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

		this.selectedProductForm
			.get('screenId')
			.setValue(parseInt(this.screenId));

		this._utilApi
			.post('builder/div', this.selectedProductForm.value)
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

				// Delete the contact
				this._utilApi
					.delete('builder/div/', this.dataEdit.id)
					.subscribe((isDeleted) => {
						// Return if the contact wasn't deleted...
						this.return();
					});
			}
		});
	}

	updateItem() {
		this._utilApi
			.put('builder/div', this.selectedProductForm.value)
			.subscribe((e) => {
				this.return();
			});
	}

	return() {
		this._location.back();
	}
}
