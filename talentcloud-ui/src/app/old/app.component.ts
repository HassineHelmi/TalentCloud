import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { PopupComponent } from './popup/popup.component';
import { ButtonModule } from 'primeng/button';
import { JobSuggestionComponent } from './job-suggestion/job-suggestion.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, PopupComponent, ButtonModule, JobSuggestionComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'career-portal';
  showPopup: boolean = false;
  autoClosePopup: boolean = false; // New property to control auto-close
  constructor(private cdr: ChangeDetectorRef) {}

  openPopup(autoClose: boolean = false) {
    this.autoClosePopup = autoClose; // Set the autoClose parameter
    this.showPopup = false; // Reset the state
    this.cdr.detectChanges(); // Force UI update

    setTimeout(() => {
      this.showPopup = true; // Reopen after a small delay
      this.cdr.detectChanges();
      console.log('Popup Opened:', this.showPopup);
    }, 10);
  }

  closePopup() {
    this.showPopup = false;
    this.cdr.detectChanges(); // Ensure UI updates when closing
    console.log('Popup Closed:', this.showPopup);
  }
}
