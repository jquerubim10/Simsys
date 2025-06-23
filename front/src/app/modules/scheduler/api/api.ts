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
export class SchedulerApi{
	public url = environment.apiUrl + environment.apiPort + '/api/v1';
	public url_save = environment.apiUrl + environment.apiPort + '/api/v1/savemed';

	public logged = JSON.parse(localStorage.getItem("loggedUser"));

	private _item: BehaviorSubject<any | null> = new BehaviorSubject(null);


    /**
	 * Constructor
	 */
	constructor(private _httpClient: HttpClient) {}

	getAllZone(): Observable<any> {
		return this._httpClient.get(this.url_save + '/centrocusto');
	}

	getAllSector(): Observable<any> {
		return this._httpClient.get(`${this.url}/leito`);
	}

	getAllDoctors(): Observable<any> {
		return this._httpClient.get(this.url_save + '/medico');
	}
	
	getAllResources(): Observable<any> {
		return this._httpClient.get(`${this.url}/scheduler/recurso`);
	}

	getAllResoucesAtendimento(id): Observable<any> {
		return this._httpClient.get(`${this.url}/scheduler/${id}/resources`);
	}

	getAllTeamByAgendamento(id): Observable<any> {
		return this._httpClient.get(`${this.url}/scheduler/${id}/teams`);
	}

	getAllResourcesSql(value, value_2, value_3): Observable<any> {
		return this._httpClient.post(`${this.url}/scheduler/recurso/search`, { whereValue : value , operationType: value_2, valueLong : value_3 });
	}

	getAllDoctorsSql( value ): Observable<any> {
		return this._httpClient.post(`${this.url}/query/doctor/find`, { whereValue : value });
	}
	
	getAllDoctorsByCenter(id_center): Observable<any> {
		return this._httpClient.get(`${this.url}/scheduler/logged/${this.logged.usuario}/center/${id_center}`);
	}

	getAllSetor(): Observable<any> {
		return this._httpClient.get(`${this.url}/scheduler/recurso/${this.logged.usuario}/setor`);
	}

	getAllConvenio(): Observable<any> {
		return this._httpClient.get(`${this.url}/scheduler/convenio`);
	}

	getAllScheduler(): Observable<any> {
		return this._httpClient.get(`${this.url}/scheduler`);
	}

	handleUpdateStatus(item): Observable<any> {
		return this._httpClient.put(`${this.url}/scheduler/update/status`, item);
	}

	getAllSchedulerFiltered(value_1, value_2): Observable<any> {
		return this._httpClient.post(`${this.url}/scheduler/filtered`, {valueLong: value_1, valueMedico: value_2});
	}

	addScheduler(item): Observable<any> {
		return this._httpClient.post(`${this.url}/scheduler`, item );
	}

	editScheduler(item): Observable<any> {
		return this._httpClient.put(`${this.url}/scheduler`, item );
	}

	getAllDoctorScale(data, centro, medico, valueScheduler, valueInsider): Observable<any> {
		return this._httpClient.post(`${this.url}/query/grade/hours`, { whereValue: data, 
																		valueLong: centro, 
																		valueMedico: medico, 
																		valueScheduler: valueScheduler,
																		valueInsider: valueInsider
																	});
	}


	getMaps(data): Observable<any> {
		return this._httpClient.post(`${this.url}/query/maps`, { valueScheduler: data });	
	}

	handleValidateSaveScheduler(sqlValid): Observable<any> {
		return this._httpClient.post(`${this.url}/query/validate/scheduler`, {sqlValidation: sqlValid});
	}

	/**
	 * Getter for item
	 */
	get item$(): Observable<any> {
		return this._item.asObservable();
	}

	toObject(arr) {
		// target object
		let rv = {};
		// Traverse array using loop
		for (let i = 0; i < arr.length; ++i) {
			let name;
			if ((arr[i].columnValue.match(/_/g) || []).length > 1) {
				name = arr[i].columnValue.replace('_', ''); // valor tela anterior coluna [coluns_value]
			} else {
				name = arr[i].columnValue; // valor anterior coluna [coluns_value]
			}

			// Assign each element to object
			rv[name.substring(0, name.indexOf('_'))] = name.substring(name.indexOf('_') + 1);
		}
			
		return rv;
	}

	/**
	 * Get item by id
	 
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
			*/
}
