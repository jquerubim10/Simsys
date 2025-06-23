import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
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
import { FuseConfirmationService } from '@fuse/services/confirmation';
import { TranslocoModule, TranslocoService } from '@ngneat/transloco';
import { DefaultApi } from 'app/app-api/default/api';
import { Observable, Subject } from 'rxjs';

@Component({
    selector: 'app-group',
    templateUrl: './group.component.html',
    styles: [
        /* language=SCSS */
        `
			.inventory-grid {
				grid-template-columns: 150px auto 70px 70px 40px 40px;

				@screen sm {
					grid-template-columns: 150px auto 70px 70px 40px 40px;
				}

				@screen md {
					grid-template-columns: 150px auto 70px 70px 40px 40px;
				}

				@screen lg {
					grid-template-columns: 150px auto 70px 70px 40px 40px;
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
export class GroupComponent implements OnInit, OnDestroy {

  items$: Observable<any[]>;
  isLoading: boolean = true;

  private _unsubscribeAll: Subject<any> = new Subject<any>();
  searchInputControl: UntypedFormControl = new UntypedFormControl();


  /**
   * Constructor
   */
  constructor(
    private _changeDetectorRef: ChangeDetectorRef,
    private _fuseConfirmationService: FuseConfirmationService,
    private transSrv: TranslocoService,
    private _defaultApi: DefaultApi
  ) { }

  ngOnInit(): void {
    this.items$ = this._defaultApi.items$;

    this.isLoading = false;

    // Subscribe to search input field value changes
    this.searchInputControl.valueChanges.subscribe((query) => {
      console.log(query);
    });
  }

  deleteGroup(id) {
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
        this._defaultApi
          .deleteItem('menu/group/', id)
          .subscribe((isDeleted) => {
            // Return if the contact wasn't deleted...
            if (!isDeleted) {
              return;
            }
          });
        this.isLoading = false;

        // Mark for check
        this._changeDetectorRef.markForCheck();
      }
    });
  }

  /**
     * On destroy
     */
  ngOnDestroy(): void {
    // Unsubscribe from all subscriptions
    this._unsubscribeAll.next(null);
    this._unsubscribeAll.complete();
  }
}
