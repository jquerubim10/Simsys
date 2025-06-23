import { Component, Inject, ViewChild, ViewEncapsulation } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { FormsModule, NgForm, ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOptionModule, MatRippleModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule, MatDialogActions, MatDialogContent, MatDialogClose, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { fuseAnimations } from '@fuse/animations';
import { TranslocoModule } from '@ngneat/transloco';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DIALOG_DATA } from '@angular/cdk/dialog';
import { PreSaveService } from './service/pre-save.service';
import { FuseAlertComponent, FuseAlertType } from '@fuse/components/alert';

@Component({
    selector: 'app-pre-save',
    templateUrl: './pre-save.component.html',
    encapsulation: ViewEncapsulation.None,
    animations: fuseAnimations,
    standalone: true,
    imports: [
        NgIf,
        CommonModule,
        FuseAlertComponent,
        MatProgressSpinnerModule,
        MatDialogModule,
        MatButtonModule,
        MatDialogActions,
        MatDialogContent,
        MatDialogClose,
        MatDatepickerModule,
        MatTooltipModule,
        MatMenuModule,
        RouterModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        FormsModule,
        ReactiveFormsModule,
        MatSortModule,
        MatPaginatorModule,
        MatSlideToggleModule,
        MatSelectModule,
        MatOptionModule,
        MatCheckboxModule,
        MatRippleModule,
        TranslocoModule,
        MatTabsModule
    ],
    styles: [
        /* language=SCSS */
        `
			@screen sm {
				.my-dialog-save {
					max-width: 80vw !important;
					max-height: 80vh !important;
				}
			}

			@screen md {
				.my-dialog-save {
					max-width: 60vw !important;
					max-height: 70vh !important;
				}
			}

			@screen lg {
				.my-dialog-save {
					max-width: 60vw !important;		
					max-height: 70vh !important;
				}
			}
			
		`,
    ]
})
export class PreSaveComponent {
	@ViewChild('signInNgForm') signInNgForm: NgForm;

	showAlert: boolean = false;
	signInForm: UntypedFormGroup;
	
  	alert: { type: FuseAlertType; message: string } = {
		type   : 'success',
		message: '',
	};

	userInputControl: UntypedFormControl = new UntypedFormControl();
	passwordInputControl: UntypedFormControl = new UntypedFormControl();

  constructor(
		@Inject(DIALOG_DATA) public data: { comp: any; item: any },
		public preSaveLoginModal: MatDialogRef<PreSaveComponent>,
		private _formBuilder: UntypedFormBuilder,
		public dialog: MatDialog, public _preSaveService: PreSaveService
	) { }

	ngOnInit(): void {
		// Create the form
        this.signInForm = this._formBuilder.group({
            user     : ['', [Validators.required]],
            password  : ['', Validators.required],
        });
  	}	

	ngAfterContentInit(): void { }

	submitUser() {
		// Return if the form is invalid
        if ( this.signInForm.invalid ) {
            return;
        }

        // Disable the form
        this.signInForm.disable();

		this.showAlert = false;
		this._preSaveService.authenticator(this.signInForm.get("user").value, this.signInForm.get("password").value)
			.subscribe(
				(res) => {

					localStorage.setItem("userScreen", JSON.stringify(res));

					// close
					this.preSaveLoginModal.close(true)
				},
				(error) => {
					// Set the alert
                    this.alert = {
                        type   : 'error',
                        message: error.error.message,
                    };

                    // Show the alert
                    this.showAlert = true;

					// Re-enable the form
                    this.signInForm.enable();
				}
		)
	}
}
