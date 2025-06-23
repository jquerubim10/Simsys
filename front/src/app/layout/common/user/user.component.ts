import { BooleanInput } from '@angular/cdk/coercion';
import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { Router } from '@angular/router';
import { UserService } from 'app/core/user/user.service';
import { TranslocoModule } from '@ngneat/transloco';
import { Subject } from 'rxjs';

@Component({
    selector: 'user',
    templateUrl: './user.component.html',
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
    exportAs: 'user',
    standalone: true,
    imports: [MatButtonModule, MatMenuModule, TranslocoModule, MatIconModule, NgClass, MatDividerModule]
})
export class UserComponent implements OnInit, OnDestroy
{
    /* eslint-disable @typescript-eslint/naming-convention */
    static ngAcceptInputType_showAvatar: BooleanInput;
    /* eslint-enable @typescript-eslint/naming-convention */

    @Input() showAvatar: boolean = true;
    user: any;

    private _unsubscribeAll: Subject<any> = new Subject<any>();

    /**
     * Constructor
     */
    constructor(
        private _changeDetectorRef: ChangeDetectorRef,
        private _router: Router,
        private _userService: UserService,
    )
    {
    }

    // -----------------------------------------------------------------------------------------------------
    // @ Lifecycle hooks
    // -----------------------------------------------------------------------------------------------------

    /**
     * On init
     */
    ngOnInit(): void {

        let logged = JSON.parse(localStorage.getItem("loggedUser")) || null;

        if (logged) {
            logged['avatar'] = "assets/images/avatars/brian-hughes.jpg";
            logged['status'] = "online";

            this.user = logged;

            // Mark for check
            this._changeDetectorRef.markForCheck();
        } else {
            this.user = this.mockUser();

            // Mark for check
            this._changeDetectorRef.markForCheck();
        }
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

    // -----------------------------------------------------------------------------------------------------
    // @ Public methods
    // -----------------------------------------------------------------------------------------------------

    /**
     * Update the user status
     *
     * @param status
     */
    updateUserStatus(status: string): void
    {
        // Return if user is not available
        if ( !this.user )
        {
            return;
        }

        // Update the user
        this._userService.update({
            ...this.user,
            status,
        }).subscribe();
    }

    /**
     * Sign out
     */
    signOut(): void
    {
        this._router.navigate(['/sign-out']);
    }
    
    mockUser() {
        let user = {
            id    : 'cfaad35d-07a3-4447-a6c3-d8c3d54fd5df',
            name  : 'Brian Hughes',
            username  : 'Brian Hughes',
            email : 'hughes.brian@company.com',
            avatar: 'assets/images/avatars/brian-hughes.jpg',
            status: 'online',
        };

        return user
    }
}
