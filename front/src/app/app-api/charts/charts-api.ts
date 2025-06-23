import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environments';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ChartsApi {
	public url = environment.apiUrl + environment.apiPort + '/api/v1/';

	constructor(private _httpClient: HttpClient) {}

	getAll(path, item): Observable<any> {
		return this._httpClient.post(this.url + path, item);
	}
}
