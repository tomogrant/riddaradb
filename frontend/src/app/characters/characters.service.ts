import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { tap, catchError, throwError } from "rxjs";
import { ICharacter } from "./ICharacter";
import { ICharacterName } from "./ICharacterName";

@Injectable({
  providedIn: "root",
})
export class CharactersService {
  characterMain = "/api/characters";
  constructor(private httpClient: HttpClient) {}

  getCharacters(): Observable<ICharacter[]> {
    return this.httpClient.get<ICharacter[]>(`${this.characterMain}/getcharacters`).pipe(
      tap((data) => console.log("All character data got: " + JSON.stringify(data))),
      catchError(this.errorHandler),
    );
  }

  getCharacterNames(): Observable<ICharacterName[]> {
    return this.httpClient.get<ICharacterName[]>(`${this.characterMain}/getcharacternames`).pipe(
      tap((data) => console.log("All character name data got: " + JSON.stringify(data))),
      catchError(this.errorHandler),
    );
  }

  getCharacterById(id: number): Observable<ICharacter> {
    return this.httpClient.get<ICharacter>(`${this.characterMain}/getcharacterbyid/${id}`).pipe(
      tap((data) => console.log("All character data got: " + JSON.stringify(data))),
      catchError(this.errorHandler),
    );
  }

  postCharacter(character: ICharacter): Observable<ICharacter> {
    console.log("Posting character: " + JSON.stringify(character));
    return this.httpClient.post<ICharacter>(`${this.characterMain}/postcharacter`, character).pipe(
      tap((data) => console.log("Character posted: " + JSON.stringify(data))),
      catchError(this.errorHandler),
    );
  }

  putCharacter(character: ICharacter): Observable<ICharacter> {
    console.log("Putting character: " + JSON.stringify(character));
    return this.httpClient.put<ICharacter>(`${this.characterMain}/putcharacter`, character).pipe(
      tap((data) => console.log("Character updated: " + JSON.stringify(data))),
      catchError(this.errorHandler),
    );
  }

  deleteCharacter(id: number): Observable<null> {
    return this.httpClient.delete<null>(`${this.characterMain}/deletecharacter/${id}`).pipe(catchError(this.errorHandler));
  }

  private errorHandler(error: HttpErrorResponse) {
    return throwError(() => error);
  }
}
