import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Painel } from "app/modules/admin/painel/model/painel.model";
import { BehaviorSubject, Observable, map, switchMap, take, tap } from "rxjs";

@Injectable({ providedIn: 'root' })
export class PanelApi {
	public url = 'http://localhost:8080/api/v1/fuse/panel/';
	private _items: BehaviorSubject<Painel[] | null>;

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
	get items$(): Observable<Painel[]> {
		return this._items.asObservable();
	}

	// -----------------------------------------------------------------------------------------------------
	// @ Public methods
	// -----------------------------------------------------------------------------------------------------

	/**
	 * list sidebar
	 *
	 */
	getPanels(): Observable<Painel[]> {
		return this._httpClient.get<Painel[]>(this.url).pipe(
			map((response: any) => response.content.map((item) => item)),
			tap((boards) => this._items.next(boards))
		);
	}

	get(id): Observable<Painel> {
		return this._httpClient.get<Painel>(this.url + id).pipe(
			tap((user) => {
				user
			})
		)
	}

	/**
	 * Create Item
	 *
	 * @param item
	 */
	createItem(item): Observable<any> {
		return this.items$.pipe(
			take(1),
			switchMap( items => this._httpClient.post<any>(this.url, item).pipe(
				map((newItem) => {
					// Update the items with the new item
					this._items.next([newItem, ...items]);

					// Return the new item
                    return newItem;
				})
			))
		)
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