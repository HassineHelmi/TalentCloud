import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JobSuggestion } from '../models/JobSuggestion';

@Injectable({ providedIn: 'root' })
export class JobSuggestionService {
  private readonly apiUrl = '/api/job-suggestions';

  constructor(private http: HttpClient) {}

  getSuggestions(): Observable<JobSuggestion[]> {
    return this.http.get<JobSuggestion[]>(this.apiUrl);
  }
}
