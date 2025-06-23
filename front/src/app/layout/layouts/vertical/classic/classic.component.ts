import { NgIf } from '@angular/common';
import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { FuseLoadingBarComponent } from '@fuse/components/loading-bar';
import {
	FuseNavigationItem,
	FuseNavigationService,
	FuseVerticalNavigationComponent,
} from '@fuse/components/navigation';
import { FuseMediaWatcherService } from '@fuse/services/media-watcher';
import { SidebarApi } from 'app/app-api/admin/sidebar/api';
import { NavigationService } from 'app/core/navigation/navigation.service';
import { Navigation } from 'app/core/navigation/navigation.types';
import { LanguagesComponent } from 'app/layout/common/languages/languages.component';
import { NotificationsComponent } from 'app/layout/common/notifications/notifications.component';
import { SearchComponent } from 'app/layout/common/search/search.component';
import { ShortcutsComponent } from 'app/layout/common/shortcuts/shortcuts.component';
import { UserComponent } from 'app/layout/common/user/user.component';
import { defaultNavigation } from 'app/mock-api/common/navigation/data';
import { Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'classic-layout',
    templateUrl: './classic.component.html',
	standalone: true,
    encapsulation: ViewEncapsulation.None,
    imports: [
        FuseLoadingBarComponent,
        FuseVerticalNavigationComponent,
        MatButtonModule,
        MatIconModule,
        LanguagesComponent,
        SearchComponent,
        ShortcutsComponent,
        NotificationsComponent,
        UserComponent,
        NgIf,
        RouterOutlet,
    ]
})
export class ClassicLayoutComponent implements OnInit, OnDestroy {
	isScreenSmall: boolean;
	navigation: Navigation;
	items: any[];
	private _unsubscribeAll: Subject<any> = new Subject<any>();

	/**
	 * Constructor
	 */
	constructor(
		private _activatedRoute: ActivatedRoute,
		private _router: Router,
		private _navigationService: NavigationService,
		private _sidebarApi: SidebarApi,
		private _fuseMediaWatcherService: FuseMediaWatcherService,
		private _fuseNavigationService: FuseNavigationService
	) {}

	// -----------------------------------------------------------------------------------------------------
	// @ Accessors
	// -----------------------------------------------------------------------------------------------------

	/**
	 * Getter for current year
	 */
	get currentYear(): number {
		return new Date().getFullYear();
	}

	// -----------------------------------------------------------------------------------------------------
	// @ Lifecycle hooks
	// -----------------------------------------------------------------------------------------------------

	/**
	 * On init
	 */
	ngOnInit(): void {
		// Subscribe to navigation data
		this._navigationService.navigation$
			.pipe(takeUntil(this._unsubscribeAll))
			.subscribe((navigation: Navigation) => {
				this.navigation = navigation;

				this._sidebarApi.items$
					.pipe(takeUntil(this._unsubscribeAll))
					.subscribe((items: FuseNavigationItem[]) => {
						this.items = items;

						this.items.forEach((item) => {
							defaultNavigation.push({
								id: item['idName'],
								title: item.title,
								type: item.type,
								icon: 'heroicons_outline:' + item.icon,
								link: item.link,
                                position: item.meta
							});
						});
					});

				// Subscribe to navigation data
				this._navigationService.navigation$
					.pipe(takeUntil(this._unsubscribeAll))
					.subscribe((navigation: Navigation) => {
                        defaultNavigation.push({
                            id: 'admin',
                            title: 'Admin',
                            type: 'basic',
                            icon: 'heroicons_outline:lock-closed',
                            link: '/admin',
                            position: 999999
                        });
						navigation.default = defaultNavigation.sort( (a, b) => { return a.position[1] - b.position[1]});
					});
			});

		// Subscribe to media changes
		this._fuseMediaWatcherService.onMediaChange$
			.pipe(takeUntil(this._unsubscribeAll))
			.subscribe(({ matchingAliases }) => {
				// Check if the screen is small
				this.isScreenSmall = !matchingAliases.includes('md');
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

	// -----------------------------------------------------------------------------------------------------
	// @ Public methods
	// -----------------------------------------------------------------------------------------------------

	/**
	 * Toggle navigation
	 *
	 * @param name
	 */
	toggleNavigation(name: string): void {
		// Get the navigation
		const navigation =
			this._fuseNavigationService.getComponent<FuseVerticalNavigationComponent>(
				name
			);

		if (navigation) {
			// Toggle the opened status
			navigation.toggle();
		}
	}
}
