import { HttpClient, HttpEvent, HttpHeaders, HttpRequest, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "environments/environments";
import { map, Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class SignService {
	public url = environment.apiUrl + environment.apiPort + '/api/v1/sign/';

	constructor(private _httpClient: HttpClient) {}

    sign(form): Observable<any> {
		return this._httpClient.post(this.url + "login", { logon: form.username, password: form.pass});
	}

	newSign(file, jsonObj, pin): Observable<HttpEvent<any>> {
		const formData: FormData = new FormData();

		formData.append('file', file);
		formData.append('userData', jsonObj.logon);
		formData.append('pin', pin);

		const req = new HttpRequest(
			'POST', 
			`${this.url}new`, 
			formData, 
			{
				responseType: 'blob'
			});

		return this._httpClient.request(req);
	}

	signingWithDownload(file, pin, tableName, lastTableId, jsonObj, whereClauseColumn, signColumnName): Observable<any> {
		const formData: FormData = new FormData();

		formData.append('file', file);
		formData.append('pin', pin);
		formData.append('tableName', tableName);
		formData.append('tableId', lastTableId);
		formData.append('userId', jsonObj.usuario);
		formData.append('whereClauseColumn', whereClauseColumn);
		formData.append('signColumnName', signColumnName);

		const req = new HttpRequest(
			'POST', 
			`${this.url}signing/document`, 
			formData, 
			{
				responseType: 'json',
			});

		return this._httpClient.request(req);
	}

	downloadPDF(id?: number): Observable<any> {

		return this._httpClient.get(this.url + `signed/${id}`, { observe: 'response', responseType: 'blob' });
	}
}