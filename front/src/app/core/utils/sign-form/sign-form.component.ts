import { NgIf, CommonModule, TitleCasePipe, DatePipe, NgStyle, JsonPipe, NgFor, NgTemplateOutlet, NgClass, AsyncPipe, CurrencyPipe } from '@angular/common';
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormsModule, ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOptionModule, MatRippleModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule, MatDialogActions, MatDialogContent, MatDialogClose, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { fuseAnimations } from '@fuse/animations';
import { FuseAlertComponent, FuseAlertType } from '@fuse/components/alert';
import { TranslocoModule } from '@ngneat/transloco';
import { BuilderFormComponent } from 'app/modules/builder/builder-form/builder-form.component';
import { FieldComponent } from 'app/modules/builder/builder-form/util/field/field.component';
import { EqualsPipe } from 'app/modules/builder/builder-screen/util/equals.pipe';
import { DifeAgePipe } from 'app/modules/hospital/nursing/attendence/utils/pipe/diffAge.pipe';
import { EvolutionComponent } from 'app/modules/hospital/pep/evolution/evolution.component';
import { PreSaveComponent } from '../pre-save/pre-save.component';
import { SignService } from './service/sign.service';

@Component({
  selector: 'app-sign-form',
  templateUrl: './sign-form.component.html',
  encapsulation: ViewEncapsulation.None,
  animations: fuseAnimations,
  standalone: true,
  imports: [
    NgIf,
    CommonModule,
    FuseAlertComponent,
    BuilderFormComponent,
    MatProgressSpinnerModule,
    EvolutionComponent,
    FieldComponent,
    MatDialogModule,
    MatButtonModule,
    MatDialogActions,
    MatDialogContent,
    MatDialogClose,
    TitleCasePipe,
    MatDatepickerModule,
    MatTooltipModule,
    MatMenuModule,
    EqualsPipe,
    DifeAgePipe,
    DatePipe,
    NgStyle,
    RouterModule,
    JsonPipe,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    MatSortModule,
    NgFor,
    NgTemplateOutlet,
    MatPaginatorModule,
    NgClass,
    MatSlideToggleModule,
    MatSelectModule,
    MatOptionModule,
    MatCheckboxModule,
    MatRippleModule,
    AsyncPipe,
    CurrencyPipe,
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
  ],
})
export class SignFormComponent implements OnInit {

  loginForm: UntypedFormGroup;

  isLoading = true;
  
  showAlert: boolean = false;
  alert: { type: FuseAlertType; message: string } = {
    type   : 'success',
    message: '',
	};

  constructor(
    private _formBuilder: UntypedFormBuilder,
    public dialogRef: MatDialogRef<PreSaveComponent>,
    public _signService: SignService
  ) { }

  ngOnInit(): void {
    // Create the selected product form
    this.loginForm = this._formBuilder.group({
      username: ['', [Validators.required]],
      pass: ['', [Validators.required]],
    });

    this.isLoading = false;
  }

  signModal() {
    // Disable the form
    this.loginForm.disable();

    this._signService.sign(this.loginForm.value)
      .subscribe(
        (res) => {
          localStorage.setItem("userScreen", JSON.stringify(res));

          // Re-enable the form
          this.loginForm.enable();
          this.dialogRef.close(true)
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
          this.loginForm.enable();
        }
      )
  }

}
