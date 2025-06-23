import {
	Location,
	NgClass,
	NgFor,
	NgIf} from '@angular/common';
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
import { FuseConfirmationService } from '@fuse/services/confirmation/confirmation.service';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { DefaultApi } from 'app/app-api/default/api';
import { UtilApi } from 'app/app-api/default/api-util';
import { Navigation } from 'app/core/navigation/navigation.types';
import { material } from 'app/mock-api/ui/icons/data';
import { finalize } from 'rxjs';

@Component({
    selector: 'app-add',
    templateUrl: './add.component.html',
    styles: [
        /* language=SCSS */
        `
			.inventory-grid {
				grid-template-columns: 150px auto 70px 70px 70px 40px;

				@screen sm {
					grid-template-columns: 150px auto 70px 70px 70px 40px;
				}

				@screen md {
					grid-template-columns: 150px auto 70px 70px 70px 40px;
				}

				@screen lg {
					grid-template-columns: 150px auto 70px 70px 70px 40px;
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
export class AddComponent implements OnInit {
	isLoading: boolean = false;

	navigation: Navigation;
	dataEdit: any;
	flashMessage: 'success' | 'error' | null = null;
	selectedProductForm: UntypedFormGroup;
	typesList: any[] = [
		{ id: 'aside' },
		{ id: 'basic' },
		/**
		{ id: 'collapsable' },
		{ id: 'group' },
		{ id: 'spacer' },
		 */
	];
	targetList: any[] = [
		{ id: '_blank' },
		{ id: '_self' },
		{ id: '_parent' },
		{ id: '_top' },
	];
	isLoadingChildren: boolean = false;
	sidebarChildrenList: any[];
	sidebarList: any[];
	screenList: any[];
	components: any[] = [];
	iconList: any[] = material;
	location: Location
	/**
	 * Constructor
	 */
	constructor(
		private _formBuilder: UntypedFormBuilder,
		private transSrv: TranslocoService,
		private _fuseConfirmationService: FuseConfirmationService,
		private _changeDetectorRef: ChangeDetectorRef,
		private _defaultApi: DefaultApi, 
		private _utilApi: UtilApi, 
		private _location: Location,
		private activatedroute: ActivatedRoute,
	) {}

	// -----------------------------------------------------------------------------------------------------
	// @ Lifecycle hooks
	// -----------------------------------------------------------------------------------------------------

	/**
	 * On init
	 */
	ngOnInit(): void {
		this.getScreenList();
		this.getSidebarList();
		// Create the selected product form
		this.selectedProductForm = this._formBuilder.group({
			id: [],
			idName: ['', [Validators.required]],
			title: ['', [Validators.required]],
			icon: ['', [Validators.required]],
			type: ['basic', [Validators.required]],

			subtitle: [''],
			tooltip: ['', [Validators.required]],
			link: [''],
			fragment: [''],
			queryParams: [''],
			queryParamsHandling: [''],
			target: ['_self'],
			function: [''],
			classes: [''],
			badge: [''],
			meta: [''],

			screenId: [],
			
			hidden: [false],
			active: [false],
			disabled: [false],
			componentS: [false],
			exactMatch: [false],
			externalLink: [false],
			preserveFragment: [false],
			isActiveMatchOptions: [false],
			childrenSidebar: [false],
			idSidebarMenu: [null]
		});

		this.activatedroute.params.subscribe((e) => {
			if (e.id) {
				this._defaultApi.getOneItem('sidebar/', e.id).subscribe((item) => {
					this.dataEdit = item;

					this.getListChildrenSidebar(e.id);

					// Create the selected product form
					this.selectedProductForm.get('id').setValue(this.dataEdit.id);
					this.selectedProductForm.get('idName').setValue(this.dataEdit.idName);
					this.selectedProductForm.get('title').setValue(this.dataEdit.title);
					this.selectedProductForm.get('icon').setValue(this.dataEdit.icon);
					this.selectedProductForm.get('type').setValue(this.dataEdit.type);
					this.selectedProductForm.get('target').setValue(this.dataEdit.target);
					this.selectedProductForm.get('link').setValue(this.dataEdit.link);
					this.selectedProductForm.get('tooltip').setValue(this.dataEdit.tooltip);

					let screen_id_int = parseInt(this.dataEdit.screenId) || null

					this.selectedProductForm.get('screenId').setValue(screen_id_int);
					this.selectedProductForm.get('componentS').setValue(this.dataEdit.componentS || false);

					this.selectedProductForm.get('childrenSidebar').setValue(this.dataEdit.childrenSidebar || false);
					this.selectedProductForm.get('idSidebarMenu').setValue(this.dataEdit.idSidebarMenu || null);

					this.selectedProductForm.get('active').setValue(this.dataEdit.active);
					this.selectedProductForm.get('disabled').setValue(this.dataEdit.disabled);
					this.selectedProductForm.get('hidden').setValue(this.dataEdit.hidden);

					// Mark for check
					this._changeDetectorRef.markForCheck();
				});
			}
		})
	}

	getScreenList() {
		this.isLoading = true;
		this._utilApi.getAll('builder/screen/').subscribe((e) => {
			this.screenList = e.content;
			let nullObj: any = {id: null, title: null}
			this.screenList.push(nullObj)
			this.isLoading = false;
			// Mark for check
			this._changeDetectorRef.markForCheck();
		})
	}

	getSidebarList() {
		this.isLoading = true;
		this._utilApi.getAll('sidebar/').subscribe((e) => {
			this.sidebarList = e.content;
			this.isLoading = false;
			// Mark for check
			this._changeDetectorRef.markForCheck();
		})
	}

	getListChildrenSidebar(id) {
		this.isLoadingChildren = true;
		this._utilApi.getSidebarChildren('sidebar/children/' + id).subscribe((e) => {
			this.sidebarChildrenList = e.content;
			this.isLoadingChildren = false;
			// Mark for check
			this._changeDetectorRef.markForCheck();
		})
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

		if (this.selectedProductForm.get('type').value == 'basic' && this.selectedProductForm.get('screenId').value != null) {
			this.selectedProductForm.get('link').setValue("/builder/" + this.selectedProductForm.get('title').value.replace(/ /g, "_") + "/" + this.selectedProductForm.get('screenId').value)
		}

		this._defaultApi
			.createItem('sidebar/', this.selectedProductForm.value)
			.pipe(
				finalize(() => {
					// Re-enable the form
					this.selectedProductForm.enable();
				})
			)
			.subscribe((response) => {
				// Subscribe to navigation data
				if (response.status > 299) {
					this.flashMessage = 'error';
				} else {
					this.flashMessage = 'success';
				}
				this._defaultApi.addNav(this.selectedProductForm.value)
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
				this._defaultApi.deleteItem('sidebar/', this.dataEdit.id).subscribe((isDeleted) => {
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
		if (this.selectedProductForm.get('type').value == 'basic' && this.selectedProductForm.get('screenId').value != null) {
			this.selectedProductForm.get('link').setValue("/builder/" + this.selectedProductForm.get('title').value.replace(/ /g, "_") + "/" + this.selectedProductForm.get('screenId').value)
		}

		this._defaultApi.updateSidebar('sidebar/', this.selectedProductForm.value).subscribe(() => {
			this._defaultApi.editNav(this.selectedProductForm.value)
			this.return()
		})
	}

	return() {
		this._location.back();
	}
}
