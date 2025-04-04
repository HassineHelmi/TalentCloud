import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    const token = this.auth.getToken();

    if (!token) {
      return this.router.parseUrl('/login');
    }

    const expectedRoles = route.data['roles'] as string[] | undefined;

    if (!expectedRoles || expectedRoles.length === 0) {
      return true; // no roles specified → any authenticated user
    }

    const helper = new JwtHelperService();
    const decoded = helper.decodeToken(token);

    const userRoles = decoded?.realm_access?.roles || decoded?.roles || [];

    const hasRequiredRole = expectedRoles.some(role => userRoles.includes(role));
    return hasRequiredRole || this.router.parseUrl('/unauthorized');
  }
}
