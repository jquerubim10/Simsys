import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, map, switchMap, take, tap } from "rxjs";

@Injectable({ providedIn: 'root' })
export class ScreenApi {
	public url = 'http://localhost:8082/api/v1/fuse/panel/';
	private _items: BehaviorSubject<any[] | null>;
    

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
	get items$(): Observable<any[]> {
		return this._items.asObservable();
	}

	// -----------------------------------------------------------------------------------------------------
	// @ Public methods
	// -----------------------------------------------------------------------------------------------------

	/**
	 * list sidebar
	 *
	 */
	get(): Observable<any[]> {
		return this._httpClient.get<any[]>(this.url).pipe(
			map((response: any) => response.content.map((item) => item)),
			tap((boards) => this._items.next(boards))
		);
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