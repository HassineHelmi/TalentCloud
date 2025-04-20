import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy, ChangeDetectorRef, OnChanges } from '@angular/core';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-popup',
  standalone: true,
  imports: [DialogModule, ButtonModule],
  templateUrl: './popup.component.html',
  styleUrls: ['./popup.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PopupComponent implements OnChanges {
  @Input() visible: boolean = false;
  @Input() autoClose: boolean = false; // New input to control auto-close behavior
  @Output() close = new EventEmitter();

  constructor(private cdr: ChangeDetectorRef) {}

  ngOnChanges() {
    this.cdr.detectChanges(); // Ensure UI updates when @Input() changes

    if (this.visible && this.autoClose) {
      setTimeout(() => {
        this.closePopup();
      }, 2000); // Close after 2 seconds
    }
  }

  closePopup() {
    this.close.emit();
  }
}
