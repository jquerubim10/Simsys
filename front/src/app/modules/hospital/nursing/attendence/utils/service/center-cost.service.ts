import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "environments/environments";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class CenterCostApi {
    public url = environment.apiUrl + environment.apiPort + '/api/v1/center/cost';

    constructor(private _httpClient: HttpClient) {}

	getAll(item): Observable<any> {
		return this._httpClient.post(this.url, {tutorUser: item.user, arrayCenter: item.array});
	}
}