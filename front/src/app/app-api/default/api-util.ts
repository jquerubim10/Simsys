import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'environments/environments';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UtilApi {
	public url = environment.apiUrl + environment.apiPort + '/api/v1/fuse/';
	public url_query = environment.apiUrl + environment.apiPort + '/api/v1/';
	public urlTutor = environment.apiUrl + environment.apiPort + '/api/v1/tutor';

	constructor(private _httpClient: HttpClient) {}

	getAll(path): Observable<any> {
		return this._httpClient.get(this.url + path);
	}
	
	getSidebarChildren(path): Observable<any> {
		return this._httpClient.get(this.url + path);
	}

	buildingScreenWithPreview(path): Observable<any> {
		return this._httpClient.get(this.url + path);
	}

	getTutor():Observable<any> {
		return this._httpClient.get(this.urlTutor + "/list");
	}

    getOne(path, id): Observable<any> {
		return this._httpClient.get(this.url + path + id);
	}

	getBuildingScreen(path) {
		return this._httpClient.get(this.url + path);
	}

	getOneQuery(path, data): Observable<any> {
		return this._httpClient.post(this.url_query + path, data);
	}

	getIndetityField(id) {
		return this._httpClient.get(`${this.url}builder/field/${id}/identity`);
	}

	handlePostSqlExecute(data): Observable<any> {
		return this._httpClient.post(this.url_query + 'query/post/sql/execute', data);
	}

	handleGetLastId() {
		return this._httpClient.get(`${this.url_query}query/lastId()`);
	}
	
	getQueryPreOrUpdate(quey): Observable<any> {
		return this._httpClient.post(this.url_query + 'query/preOrUp/execute', quey)
	}

	getQuerySelectField(data): Observable<any> {
		return this._httpClient.post(this.url_query + 'query/search/field', data);
	}
	
	getSaveMenu(item): Observable<any> {
		return this._httpClient.post('http://localhost:3000/', item);
	}

	post(path, item): Observable<any> {
		return this._httpClient.post(this.url + path, item);
	}

	postTeste(path, item): Observable<any> {
		return this._httpClient.post(this.url_query + path, item);
	}

	postResponseBody(path, item): Observable<any> {
		return this._httpClient.post(this.url_query + path, item);
	}

	getDynamicListModal(path, item): Observable<any> {
		return this._httpClient.post(this.url_query + path, item);
	}

	dynamicQuery(path, item): Observable<any> {
		return this._httpClient.post(this.url_query + path, item);
	}

    put(path, item): Observable<any> {
		return this._httpClient.put(this.url + path, item);
	}

	addToGroup(path): Observable<any> {
		return this._httpClient.patch(this.url + path, {});
	}

    delete(path, id): Observable<any> {
		return this._httpClient.delete(this.url + path + id);
	}
}
