import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private keycloak: KeycloakService) {}

  async init() {
    await this.keycloak.init({
      config: {
        url: 'https://auth.recruitment.com',
        realm: 'recruitment',
        clientId: 'recruitment-client'
      }
    });
  }
}
