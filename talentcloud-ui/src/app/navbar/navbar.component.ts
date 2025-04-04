import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-navbar', // ✅ Ensure standalone is true
    imports: [CommonModule, RouterModule, FormsModule], // ✅ Add necessary imports
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {}
