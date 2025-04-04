import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InterviewSession } from '../models/InterviewSession';

@Injectable({ providedIn: 'root' })
export class InterviewSessionService {
  private readonly apiUrl = '/api/interview-sessions';

  constructor(private http: HttpClient) {}

  getSession(): Observable<InterviewSession> {
    return this.http.get<InterviewSession>(this.apiUrl);
  }
}
