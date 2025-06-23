import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { FuseNavigationItem } from '@fuse/components/navigation';
import { Navigation } from 'app/core/navigation/navigation.types';
import {
	compactNavigation,
	defaultNavigation,
	futuristicNavigation,
	horizontalNavigation,
} from 'app/mock-api/common/navigation/data';
import { environment } from 'environments/environments';
import { Observable, ReplaySubject, tap, filter } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class NavigationService {
	public url = environment.apiUrl + environment.apiPort + '/api/v1/navigation';
	private _httpClient = inject(HttpClient);
	private _navigation: ReplaySubject<Navigation> =
		new ReplaySubject<Navigation>(1);

	private _compactNavigation: FuseNavigationItem[] = compactNavigation;
	private _defaultNavigation: FuseNavigationItem[] = defaultNavigation;
	private _futuristicNavigation: FuseNavigationItem[] = futuristicNavigation;
	private _horizontalNavigation: FuseNavigationItem[] = horizontalNavigation;

	// -----------------------------------------------------------------------------------------------------
	// @ Accessors
	// -----------------------------------------------------------------------------------------------------

	/**
	 * Getter for navigation
	 */
	get navigation$(): Observable<Navigation> {
		return this._navigation.asObservable();
	}

	// -----------------------------------------------------------------------------------------------------
	// @ Public methods
	// -----------------------------------------------------------------------------------------------------

	/**
	 * Get all navigation data
	 */
	get(): Observable<Navigation> {
		return this._httpClient.get<any[]>(this.url).pipe(
			tap((navigation: any) => {
				this._compactNavigation = navigation.content.filter((item) => item.childrenSidebar === false);

				this._compactNavigation.forEach((item, index) => {
					item['children'] = [];
					navigation.content.forEach((child) => {
						if (child.idSidebarMenu === item.id) {
							item.children.push(child);
						}
					});

					if ((this._compactNavigation.length - 1 ) == index ) {
						let t: Navigation = {
							compact: this._compactNavigation,
							default: this._defaultNavigation,
							futuristic: this._futuristicNavigation,
							horizontal: this._horizontalNavigation,
						};
		
						this._navigation.next(t);
					}
				});
			})
		);
	}
}
