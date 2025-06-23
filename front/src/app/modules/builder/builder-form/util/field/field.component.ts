import {
	NgClass,
	NgFor,
	NgIf,
	NgSwitch,
	NgSwitchCase,
	TitleCasePipe
} from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	Input,
	OnInit,
	ViewChild,
	ViewEncapsulation,
} from '@angular/core';
import {
	ControlContainer,
	FormGroupDirective,
	FormsModule,
	ReactiveFormsModule
} from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatOptionModule, MatRippleModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
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
import { TranslocoModule } from '@ngneat/transloco';

import { QuillEditorComponent } from 'ngx-quill';

@Component({
    selector: 'app-field',
    templateUrl: './field.component.html',
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: fuseAnimations,
	standalone: true,
    imports: [
        MatDatepickerModule,
        NgIf,
        RouterModule,
        TitleCasePipe,
        MatProgressBarModule,
        MatAutocompleteModule,
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
        NgSwitch,
        NgSwitchCase,
        TranslocoModule,
        QuillEditorComponent
    ],
    viewProviders: [
        {
            provide: ControlContainer,
            useExisting: FormGroupDirective,
        },
    ],
    styles: [
        /* language=SCSS */
        `
			.w-field {
				width: -webkit-fill-available;
			}

			.w-field-q {
				width: -webkit-fill-available;
				height: 100% !important
			}

			.disabled-f {
				pointer-events: none;
				opacity: 0.5;
			}

			mat-quill {
				width: 100% !important;
			}

			.editorMat {
				.mat-mdc-text-field-wrapper {
					border-width: 0 !important;

					.mat-mdc-form-field-flex {
						padding: 0 !important;
						height: 100% !important
					}
				}	
			} 
		`,
    ]
})
export class FieldComponent implements OnInit {
	@Input() field: any;
	@Input() screenType: any;
	@Input() selectedProductForm;

	listObj = [];


	quillModules: any = {
        toolbar: [
            ['bold', 'italic', 'underline', 'strike'], 
            [{ align: [] }, { list: 'ordered' }, { list: 'bullet' }],
			[{ 'script': 'sub'}, { 'script': 'super' }],
			[{ 'color': [] }, { 'background': [] }],
            ['clean'],
        ],
    };
	ngOnInit(): void {
		if (this.field.type == 'select') {
			this.getListSelect(this.field);
		}
	}

	getListSelect(field) {
		if (field.defaultsValue == true) this.listObj = JSON.parse(field.sqlText);
		
		return this.listObj;
	}
}
