import { Routes } from '@angular/router';
import { CareerComponent } from './career/career.component';
import { SettingsPrivacyComponent } from './settings-privacy/settings-privacy.component';

export const routes: Routes = [
  { path: 'career', component: CareerComponent },
  { path: 'settings-privacy', component: SettingsPrivacyComponent },
  { path: '', redirectTo: 'career', pathMatch: 'full' },
  { path: '**', redirectTo: 'career' }
];
