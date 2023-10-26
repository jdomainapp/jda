import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BaseService {
  public apiName!: string;
  private baseUrl = 'http://localhost:8080/';
  constructor(private httpClient: HttpClient) {
    console.log('Service (Constructor)');
  }

  public create(apiName: string, obj: any): Observable<any> {
    return this.httpClient.post(this.baseUrl + apiName, obj);
  }

  public get(apiName: string): Observable<any> {
    return this.httpClient.get(this.baseUrl + apiName);
  }

  public update(apiName: string, obj: any): Observable<any> {
    return this.httpClient.patch(
      this.baseUrl + apiName + '/' + obj.id,
      obj
    );
  }

  public delete(apiName: string, id: any): Observable<any> {
    return this.httpClient.delete(this.baseUrl + apiName + '/' + id);
  }

  public search(apiName: string, id: any): Observable<any> {
    return this.httpClient.get(this.baseUrl + apiName + '/' + id);
  }

  public getAllChilds(
    apiName: string,
    id: any,
    childrenApi: string
  ): Observable<any> {
    return this.httpClient.get(
      this.baseUrl + apiName + '/' + id + '/' + childrenApi
    );
  }
}

export class BaseServiceContainer {
  instance!: BaseService;
}
