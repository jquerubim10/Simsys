import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "environments/environments";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class TherapeuticPlanningApi {
    public url = environment.apiUrl + environment.apiPort + '/api/v1/therapeutic/planning';

    constructor(private _httpClient: HttpClient) {}

	getAll(item): Observable<any> {
		return this._httpClient.post(`${this.url}/list`, {tpTreatment: item.tpTreatment, whereValue: item.whereValue, secondWhereValue: item.secondWhereValue});
	}
}