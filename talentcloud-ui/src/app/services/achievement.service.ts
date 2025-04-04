import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Achievement } from '../models/Achievement';

@Injectable({ providedIn: 'root' })
export class AchievementService {
  private readonly apiUrl = '/api/achievements';

  constructor(private http: HttpClient) {}

  getAchievements(): Observable<Achievement[]> {
    return this.http.get<Achievement[]>(this.apiUrl);
  }
}

