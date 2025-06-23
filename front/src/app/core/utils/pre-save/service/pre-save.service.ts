import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "environments/environments";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class PreSaveService {
	public url = environment.apiUrl + environment.apiPort + '/api/v1/user-save/';

	constructor(private _httpClient: HttpClient) {}

    authenticator(username, password): Observable<any> {
		return this._httpClient.post(this.url + "login", { logon: username, password: password});
	}
}