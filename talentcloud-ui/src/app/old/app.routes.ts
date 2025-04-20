import { Routes } from '@angular/router';
import { CareerComponent } from './career/career.component';
import { SettingsPrivacyComponent } from './settings-privacy/settings-privacy.component';
import { AuthGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'career',
    component: CareerComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_CANDIDATE'] }
  },
  {
    path: 'settings-privacy',
    component: SettingsPrivacyComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_ADMIN', 'ROLE_CLIENT'] }
  },
  { path: '', redirectTo: 'career', pathMatch: 'full' },
  { path: '**', redirectTo: 'career' },
  { path: 'unauthorized', component: UnauthorizedComponent }
];
