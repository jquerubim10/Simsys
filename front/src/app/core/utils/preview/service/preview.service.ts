import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "environments/environments";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class PreviewService {
	public url = environment.apiUrl + environment.apiPort + '/api/v1/preview/';

	constructor(private _httpClient: HttpClient) {}

    getHeader(): Observable<any> {
		return this._httpClient.get(this.url + "header");
	}


	handleGenericPreview(id, type?): Observable<any> {
		if (type != "") {
			return this._httpClient.post(`${this.url}generic/${id}`, {selectOne: type})
		}
		return this._httpClient.get(`${this.url}evolution/${id}`);
	}
}