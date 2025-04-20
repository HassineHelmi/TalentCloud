import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'https://auth.talentcloud-dev.com/api/auth';
  private jwtTokenKey = 'jwt_token';

  private isLoggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
  isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(data: { username: string; password: string }): Observable<string> {
    return this.http.post(this.apiUrl + '/login', data, { responseType: 'text' }).pipe(
      tap(token => {
        localStorage.setItem(this.jwtTokenKey, token);
        this.isLoggedInSubject.next(true);
      })
    );
  }

  register(data: any): Observable<any> {
    return this.http.post(this.apiUrl + '/register', data);
  }

  logout() {
    localStorage.removeItem(this.jwtTokenKey);
    this.isLoggedInSubject.next(false);
  }

  getToken(): string | null {
    return localStorage.getItem(this.jwtTokenKey);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem(this.jwtTokenKey);
  }
}
