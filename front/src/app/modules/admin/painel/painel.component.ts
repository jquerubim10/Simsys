import { CommonModule } from '@angular/common';
import {
	ChangeDetectionStrategy,
	ChangeDetectorRef,
	Component,
	OnDestroy,
	OnInit,
	ViewEncapsulation,
} from '@angular/core';
import { fuseAnimations } from '@fuse/animations';
import { Painel } from './model/painel.model';
import { IPainel } from './types/painel.types';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PanelApi } from 'app/app-api/admin/panel/api';
import { FuseNavigationItem, FuseNavigationService } from '@fuse/components/navigation';
import { Observable, Subject, takeUntil } from 'rxjs';
import { MatButton, MatButtonModule } from '@angular/material/button';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { MatMenu, MatMenuModule } from '@angular/material/menu';
import { FuseConfirmationService } from '@fuse/services/confirmation';
import { items } from 'app/mock-api/apps/file-manager/data';
import { DefaultApi } from 'app/app-api/default/api';

@Component({
    selector: 'app-painel',
	standalone: true,
    imports: [
        CommonModule,
        MatIconModule,
        RouterModule,
        MatButtonModule,
        TranslocoModule,
        MatMenuModule,
    ],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './painel.component.html',
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
    animations: fuseAnimations
})
export class PainelComponent implements OnInit, OnDestroy {
	isLoading: any;

	painels: Painel[] = [];
	items$: Observable<Painel[]>;
	private _unsubscribeAll: Subject<any> = new Subject<any>();

	/**
	 * Constructor
	 */
	constructor(
		private _defaultApi: DefaultApi,
		private _route: Router,
		private _fuseConfirmationService: FuseConfirmationService,
		private transSrv: TranslocoService,
		private _changeDetectorRef: ChangeDetectorRef,
	) {}

	/**
	 * On init
	 */
	ngOnInit() {
		// Get the products
        this.items$ = this._defaultApi.items$;
	}

	additem() {}

	editItem(item) {
		this._route.navigateByUrl('/admin/edit/' + item.id );
	}

	deleteItem(id) {
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
				this._defaultApi.deleteItem('panel/', id).subscribe((isDeleted) => {
					// Return if the contact wasn't deleted...
					if (!isDeleted) {
						return;
					}
				});

				// Mark for check
				this._changeDetectorRef.markForCheck();
				this.isLoading = false;
			}
		});
	}
	/**
	 * Track by function for ngFor loops
	 *
	 * @param index
	 * @param item
	 */
	trackByFn(index: number, item: any): any {
		return item.id || index;
	}

	/**
     * On destroy
     */
    ngOnDestroy(): void
    {
        // Unsubscribe from all subscriptions
        this._unsubscribeAll.next(null);
        this._unsubscribeAll.complete();
    }
}
