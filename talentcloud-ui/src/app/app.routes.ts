import { Routes } from '@angular/router';
import { CareerComponent } from './career/career.component';

export const routes: Routes = [
  { path: 'career', component: CareerComponent },
  { path: '', redirectTo: 'career', pathMatch: 'full' }, 
  { path: '**', redirectTo: 'career' } 
];
