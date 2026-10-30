import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { tap, catchError, throwError } from "rxjs";
import { IText } from "./IText";

@Injectable({
  providedIn: "root",
})
export class LandingService {
  textMain = "/api/text";
  constructor(private httpClient: HttpClient) {}

  getHomeText(): Observable<IText> {
    return this.httpClient.get<IText>(`${this.textMain}/gethometext`).pipe(
      catchError(this.errorHandler),
    );
  }

  getAboutText(): Observable<IText> {
    return this.httpClient.get<IText>(`${this.textMain}/getabouttext`).pipe(
      catchError(this.errorHandler),
    );
  }

  saveHomeText(text: IText): Observable<IText> {
    return this.httpClient.put<IText>(`${this.textMain}/savehometext`, text).pipe(
      catchError(this.errorHandler),
    );
  }

  saveAboutText(text: IText): Observable<IText> {
    return this.httpClient.put<IText>(`${this.textMain}/saveabouttext`, text).pipe(
      catchError(this.errorHandler),
    );
  }

    private errorHandler(error: HttpErrorResponse) {
    return throwError(() => error);
  }
}