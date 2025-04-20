import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserProfile {
  id: string;
  name: string;
  email: string;
  jobTitle: string;
  location: string;
}

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private apiUrl = 'https://profile.talentcloud-dev.com/api/profile';

  constructor(private http: HttpClient) {}

  getProfile(id: string): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/${id}`);
  }
}

// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable } from 'rxjs';
// import { UserProfile } from '../models/UserProfile';
//
// @Injectable({ providedIn: 'root' })
// export class ProfileService {
//   private readonly apiUrl = '/api/profile';
//
//   constructor(private http: HttpClient) {}
//
//   getProfile(): Observable<UserProfile> {
//     return this.http.get<UserProfile>(this.apiUrl);
//   }
//
//   updateProfile(profile: Partial<UserProfile>): Observable<UserProfile> {
//     return this.http.patch<UserProfile>(this.apiUrl, profile);
//   }
// }

