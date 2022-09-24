import { Inject, Injectable, Input } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BaseService {
  public apiName!: string;
  private baseUrl = 'http://localhost:8080/';
  constructor(private httpClient: HttpClient) { 
  }

  init(apiName:any): void {
    this.apiName = apiName;
  }

  public create(obj: any): Observable<any> {
    return this.httpClient.post(this.baseUrl + this.apiName, obj)
   }

  public get():Observable<any> {
    return this.httpClient.get(this.baseUrl + this.apiName)
  }

  public update(obj: any):Observable<any> {
    return this.httpClient.patch(this.baseUrl + this.apiName + "/" + obj.id, obj)
  }

  public delete(id: any):Observable<any> {
    return this.httpClient.delete(this.baseUrl + this.apiName + "/" + id)
  }

  public search(id: any): Observable<any> {
    return this.httpClient.get(this.baseUrl + this.apiName + "/" + id)
  }
}

export class BaseServiceContainer {
  instance!: BaseService;
}

