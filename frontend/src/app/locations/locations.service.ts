import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { tap, catchError, throwError } from "rxjs";
import { ILocation } from "./ILocation";

@Injectable({
  providedIn: "root",
})
export class LocationsService {
  locationMain = "/api/locations";
  constructor(private httpClient: HttpClient) {}

  getLocations(): Observable<ILocation[]> {
    return this.httpClient.get<ILocation[]>(`${this.locationMain}/getlocations`).pipe(
      tap((data) => console.log("All location data got: " + JSON.stringify(data))),
      catchError(this.errorHandler),
    );
  }

  getLocationById(id: number): Observable<ILocation> {
    return this.httpClient.get<ILocation>(`${this.locationMain}/getlocationbyid/${id}`).pipe(
      tap((data) => console.log("All location data got: " + JSON.stringify(data))),
      catchError(this.errorHandler),
    );
  }

  postLocation(location: ILocation): Observable<ILocation> {
    console.log("Posting location: " + JSON.stringify(location));
    return this.httpClient.post<ILocation>(`${this.locationMain}/postuser`, location).pipe(
      tap((data) => console.log("Location posted: " + JSON.stringify(data))),
      catchError(this.errorHandler),
    );
  }

  putLocation(location: ILocation): Observable<ILocation> {
    console.log("Putting location: " + JSON.stringify(location));
    return this.httpClient.put<ILocation>(`${this.locationMain}/putuser`, location).pipe(
      tap((data) => console.log("Location updated: " + JSON.stringify(data))),
      catchError(this.errorHandler),
    );
  }

  deleteLocation(id: number): Observable<null> {
    return this.httpClient.delete<null>(`${this.locationMain}/deleteuser/${id}`).pipe(
      catchError(this.errorHandler)
    );
  }

  private errorHandler(error: HttpErrorResponse) {
    return throwError(() => error);
  }
}
