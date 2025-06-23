import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FuseNavigationItem } from '@fuse/components/navigation';
import { environment } from 'environments/environments';
import { BehaviorSubject, Observable, ReplaySubject, map, switchMap, take, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SidebarApi {
	public url = environment.apiUrl + environment.apiPort + '/api/v1/navigation';
	private _items: BehaviorSubject<FuseNavigationItem[] | null>;

	/**
	 * Constructor
	 */
	constructor(private _httpClient: HttpClient) {
		// Set the private defaults
		this._items = new BehaviorSubject(null);
	}

	// -----------------------------------------------------------------------------------------------------
	// @ Accessors
	// -----------------------------------------------------------------------------------------------------

	/**
	 * Getter for items
	 */
	get items$(): Observable<FuseNavigationItem[]> {
		return this._items.asObservable();
	}

	// -----------------------------------------------------------------------------------------------------
	// @ Public methods
	// -----------------------------------------------------------------------------------------------------

	/**
	 * List Item
	 *
	 */
	getSidebars(): Observable<FuseNavigationItem[]> {
		return this._httpClient.get<FuseNavigationItem[]>(this.url).pipe(
			map((response: any) => response.content.map((item) => item)),
			tap((boards) => this._items.next(boards))
		);
	}

	/**
	 * Create Item
	 *
	 * @param item
	 */
	createItem(item: FuseNavigationItem): Observable<any> {
		return this._httpClient.post(this.url, item);
	}

	/**
	 * Delete Item
	 *
	 * @param item
	 */
	deleteItem(id): Observable<any> {
		return this.items$.pipe(
			take(1),
			switchMap( items => this._httpClient.delete(this.url + id).pipe(
				map((isDeleted: boolean) => {
					const index = items.findIndex(item => item.id === id);

					items.splice(index, 1);

					this._items.next(items);

					return isDeleted;
				}),
			)),
		);
	}
}
