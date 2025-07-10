import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable, OnInit } from '@angular/core';
import { catchError, map } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { GaraLottiinfoResponse } from '../model/garaLottiInfo-response';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RestService implements OnInit {

  constructor(private http: HttpClient) {

  }

  ngOnInit() {

  }

  putJsonRequest(dgueRequest: any) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let body = JSON.stringify(dgueRequest);
    return this.http.post(environment.restURL + "request/export", body, { headers: headers, responseType: 'blob' as 'json' }).pipe(
      catchError((error: HttpErrorResponse) => {
        throw (error);
      })
    );
  }

  putJsonResponse(dgueRequest: any) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let body = JSON.stringify(dgueRequest);
    return this.http.post(environment.restURL + "response/export", body, { headers: headers, responseType: 'blob' as 'json' }).pipe(
      catchError((error: HttpErrorResponse) => {
        throw (error);
      })
    );
  }

  getDatiGaraLotti(queryParams: string): any {
    let url = environment.restURL + "getGaraLotti";
    if (queryParams != "") {
      url = url + queryParams;
    }    
    return this.http.get<Array<GaraLottiinfoResponse>>(url);
  }

  putXmlRequest(importXmlBase64: string, whoIs: string) {
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };

    let body = {      
      "whoIs": whoIs,
      "base64Xml": importXmlBase64
    };
    return this.http.post(environment.restURL + "request/import", body, httpOptions);
  }


  putXmlResponse(importXmlBase64: string, whoIs: string) {
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };

    let body = {
      "whoIs": whoIs,
      "base64Xml": importXmlBase64
    };
    return this.http.post(environment.restURL + "response/import", body, httpOptions);
  }
  


  getToken(user: string, pass: string): any {
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };

    let body = {
      "user": user,
      "password": pass
    };
    return this.http.post(environment.restURL + "getToken", body, httpOptions);
  }


  getInfoTest(url: string, token: string): any {
    const headerDict = {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    }

    const requestOptions = {
      headers: new HttpHeaders(headerDict),
    };

    return this.http.get<any>(url, requestOptions);
  }


  getEcertisInfo(uuid: string[], lang:string): Observable<String> {    
    let httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };

    
    return this.http.post<any>(environment.restURL + "getEcertisInfo",{uuid,lang}, httpOptions).pipe(
      map((res: any) => {
          return res.response.subCriteria;
      })
    );
  }


}



