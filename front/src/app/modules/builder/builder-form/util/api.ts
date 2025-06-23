import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environments';
import {
	BehaviorSubject,
	Observable,
	map,
	of,
	switchMap,
	take,
	tap,
	throwError,
} from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BuilderApi{
	public url = environment.apiUrl + environment.apiPort + '/api/v1/fuse/';

	private _item: BehaviorSubject<any | null> = new BehaviorSubject(null);

	/**
	 * Constructor
	 */
	constructor(private _httpClient: HttpClient) {}

	/**
	 * Getter for item
	 */
	get item$(): Observable<any> {
		return this._item.asObservable();
	}

	/**
	 * Get item by id
	 */
	getItemById(id?): Observable<any> {
		return this._httpClient
			.get<any>(this.url + 'builder/screen/' + id)
			.pipe(
				take(1),
				map((item) => {
					this._httpClient
						.get<any>(this.url + 'builder/div/list/' + id)
						.pipe(
							tap((divs) => {
								divs.content.forEach((div) => {
									this._httpClient
										.get<any>(
											this.url +
												'builder/field/div/list/',
											div.id
										)
										.pipe(
											tap((fields: any) => {
												div['fields'] = fields.content;
											})
										);
								});

								item['divs'] = divs.content;
							})
						);
					// Update the item
					this._item.next(item);

					// Return the item
					return item;
				}),
				switchMap((item) => {
					if (!item) {
						return throwError(
							'Could not found the item with id of ' + id + '!'
						);
					}

					return of(item);
				})
			);
	}
}
