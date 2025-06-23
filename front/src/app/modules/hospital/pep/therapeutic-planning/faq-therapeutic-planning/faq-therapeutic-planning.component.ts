import { ChangeDetectionStrategy, Component, ViewEncapsulation } from '@angular/core';
import { MatDialogContent } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { fuseAnimations } from '@fuse/animations';
import { TranslocoModule } from '@ngneat/transloco';

@Component({
    selector: 'app-faq-therapeutic-planning',
    templateUrl: './faq-therapeutic-planning.component.html',
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: fuseAnimations,
	standalone: true,
    imports: [
        MatDialogContent,
        MatIconModule,
        TranslocoModule,
    ],
    styles: [
        /* language=SCSS */
        `
			@screen sm {
				.faq-therapeutic-dialog {
					max-width: 50vw !important;
				}
			}

			@screen md {
				.faq-therapeutic-dialog {
					max-width: 50vw !important;
				}
			}

			@screen lg {
				.faq-therapeutic-dialog {
					max-width: 50vw !important;
				}
			}
		`,
    ]
})
export class FaqTherapeuticPlanningComponent {}
