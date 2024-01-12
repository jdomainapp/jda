import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

const baseUrl = 'http://localhost:8080';

@Injectable({
    providedIn: 'root'
})
export class BaseService {
    constructor(
        private httpClient: HttpClient
    ) {  }

    getList(apiName: string): Observable<any> {
        return this.httpClient.get<any>(`${baseUrl}/${apiName}`);
    }

    getById(apiName: string, id: any): Observable<any> {
        return this.httpClient.get<any>(`${baseUrl}/${apiName}/${id}`);
    }

    create(apiName: string, data: any): Observable<any> {
        return this.httpClient.post<any>(`${baseUrl}/${apiName}`, data);
    }

    update(apiName: string, id: any, data: any): Observable<any> {
        return this.httpClient.patch(`${baseUrl}/${apiName}/${id}`, data);
    }

    delete(apiName: string, id: any): Observable<any> {
        return this.httpClient.delete(`${baseUrl}/${apiName}/${id}`);
    }
}
