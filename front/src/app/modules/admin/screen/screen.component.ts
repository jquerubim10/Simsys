import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule, UntypedFormControl } from '@angular/forms';
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
import { RouterModule } from '@angular/router';
import { fuseAnimations } from '@fuse/animations';
import { FuseConfirmationService } from '@fuse/services/confirmation/confirmation.service';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { DefaultApi } from 'app/app-api/default/api';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-screen',
    templateUrl: './screen.component.html',
    styles: [
        /* language=SCSS */
        `
			.inventory-grid {
				grid-template-columns: 200px auto 70px 70px 40px 40px;

				@screen sm {
					grid-template-columns: 200px auto 70px 70px 40px 40px;
				}

				@screen md {
					grid-template-columns: 200px auto 70px 70px 40px 40px;
				}

				@screen lg {
					grid-template-columns: 200px auto 70px 70px 40px 40px;
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
        AsyncPipe,
        TranslocoModule,
    ]
})
export class ScreenComponent implements OnInit, AfterViewInit, OnDestroy {
	items$: Observable<any[]>;
	items: any[];
	searchInputControl: UntypedFormControl = new UntypedFormControl();
	isLoading: boolean;

	/**
	 * Constructor
	 */
	constructor(
		private _changeDetectorRef: ChangeDetectorRef,
		private transSrv: TranslocoService,
		private _toastr: ToastrService,
		private _fuseConfirmationService: FuseConfirmationService,
		private _defaultApi: DefaultApi
	) {}

	ngOnInit(): void {
		this.items$ = this._defaultApi.items$;
	}

	deleteItem(id) {
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
				this._defaultApi
					.deleteItem('builder/screen/', id)
					.subscribe((isDeleted) => {
						// Return if the contact wasn't deleted...
						if (!isDeleted) {
							return;
						}
					}, (error) => {
						this.throwError(error.error)
					});

				// Mark for check
				this._changeDetectorRef.markForCheck();
				this.isLoading = false;
			}
		});
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

	ngAfterViewInit(): void {}

	ngOnDestroy(): void {}
}
