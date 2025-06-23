import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
	FuseNavigationService,
	FuseVerticalNavigationComponent,
} from '@fuse/components/navigation';
import { environment } from 'environments/environments';
import { BehaviorSubject, Observable, map, switchMap, take, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class DefaultApi {
	public url = environment.apiUrl + environment.apiPort + '/api/v1/fuse/';
	private _items: BehaviorSubject<any[] | null>;
	private _item: BehaviorSubject<any | null>;
	/**
	 * Constructor
	 */
	constructor(
		private _httpClient: HttpClient,
		private _navigationService: FuseNavigationService
	) {
		// Set the private defaults
		this._items = new BehaviorSubject(null);
	}

	// -----------------------------------------------------------------------------------------------------
	// @ Accessors
	// -----------------------------------------------------------------------------------------------------

	/**
	 * Getter for items
	 */
	get items$(): Observable<any[]> {
		return this._items.asObservable();
	}

	// -----------------------------------------------------------------------------------------------------
	// @ Public methods
	// -----------------------------------------------------------------------------------------------------

	/**
	 * List all items
	 *
	 * @param path
	 *
	 */
	getAllItems(path): Observable<any[]> {
		return this._httpClient.get<any[]>(this.url + path).pipe(
			map((response: any) => response.content.map((item) => item)),
			tap((boards) => this._items.next(boards))
		);
	}

	/**
	 * Get one item
	 *
	 * @param path
	 * @param id
	 */
	getOneItem(path, id): Observable<any> {
		return this._httpClient.get<any>(this.url + path + id).pipe(
			tap((user) => {
				user;
			})
		);
	}

	/**
	 * Create Item
	 *
	 * @param path
	 * @param item
	 */
	createItem(path, item): Observable<any> {
		return this.items$.pipe(
			take(1),
			switchMap((items) =>
				this._httpClient.post<any>(this.url + path, item).pipe(
					map((newItem) => {
						// Update the items with the new item
						this._items.next([newItem, ...items]);

						// Return the new item
						return newItem;
					})
				)
			)
		);
	}

	updateSidebar(path, item): Observable<any> {
		return this._httpClient.put<any>(this.url + path, item);
	}

	updateItem(path, item): Observable<any> {
		return this.items$.pipe(
			take(1),
			switchMap((items) =>
				this._httpClient.put<any>(this.url + path, item).pipe(
					map((newItem) => {
						// Update the items with the new item
						this._items.next([...items, newItem]);

						// Return new item from observable
						return newItem;
					})
				)
			)
		);
	}

	/**
	 * Delete Item
	 *
	 * @param path
	 * @param id
	 */
	deleteItem(path, id): Observable<any> {
		return this.items$.pipe(
			take(1),
			switchMap((items) =>
				this._httpClient.delete(this.url + path + id).pipe(
					map((isDeleted: boolean) => {
						const index = items.findIndex((item) => item.id === id);

						items.splice(index, 1);

						this._items.next(items);

						return isDeleted;
					})
				)
			)
		);
	}

	updateNav(id) {
		// Get the component -> navigation data -> item
		const navComponent =
			this._navigationService.getComponent<FuseVerticalNavigationComponent>(
				'mainNavigation'
			);
		// Get the flat navigation data
		const navigation = navComponent.navigation;
		navigation;

		const index = navigation.findIndex((item) => item.id === id);

		navigation.splice(index, 1);

		navComponent.navigation = navigation;
		navComponent.refresh();
	}

	editNav(item) {
		// Get the component -> navigation data -> item
		const navComponent =
			this._navigationService.getComponent<FuseVerticalNavigationComponent>(
				'mainNavigation'
			);
		// Get the flat navigation data
		const navigation = navComponent.navigation

		navigation.forEach((itemOld,index) => { 
			if (itemOld.id === item.id) {
				navigation[index] = item;
			}
		});

		navComponent.navigation = navigation;
		navComponent.refresh();
	}
	

	addNav(item) {
		// Get the component -> navigation data -> item
		const navComponent =
			this._navigationService.getComponent<FuseVerticalNavigationComponent>(
				'mainNavigation'
			);
		// Get the flat navigation data
		const navigation = navComponent.navigation;

		navigation.push(item);

		navComponent.navigation = navigation;
		navComponent.refresh();
	}
}
