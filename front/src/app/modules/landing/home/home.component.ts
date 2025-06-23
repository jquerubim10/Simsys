import { ChangeDetectionStrategy, Component, ViewEncapsulation } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { fuseAnimations } from '@fuse/animations';
import { TranslocoModule } from '@ngneat/transloco';

@Component({
    selector: 'landing-home',
    templateUrl: './home.component.html',
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
    animations: fuseAnimations,
    standalone: true,
    imports: [MatButtonModule, MatIconModule, TranslocoModule]
})
export class LandingHomeComponent
{
    /**
     * Constructor
     */
    constructor()
    {
    }
}
