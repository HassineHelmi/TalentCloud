import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PopupComponent } from './popup.component';
import { By } from '@angular/platform-browser';

describe('PopupComponent', () => {
  let component: PopupComponent;
  let fixture: ComponentFixture<PopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PopupComponent], // ✅ Import as it's a standalone component
    }).compileComponents();

    fixture = TestBed.createComponent(PopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should not display the popup initially', () => {
    fixture.detectChanges();

    const dialog = fixture.debugElement.query(By.css('p-dialog'));
    expect(dialog).toBeTruthy(); // ✅ Ensure the dialog exists in the DOM
    expect(component.visible).toBeFalse(); // ✅ Ensure visible=false
    expect(dialog.nativeElement.getAttribute('ng-reflect-visible')).toBe('false'); // ✅ Ensure PrimeNG hides it
  });

  // ✅ FIXED TEST FOR DISPLAYING THE POPUP
  it('should display the popup when visible is true', async () => {
    component.visible = true;
    fixture.detectChanges();

    await fixture.whenStable(); // ✅ Ensure Angular stabilizes changes
    await new Promise(resolve => setTimeout(resolve, 200)); // ✅ Ensure PrimeNG modal is fully rendered

    const dialog = fixture.debugElement.query(By.css('p-dialog'));
    expect(dialog).toBeTruthy(); // ✅ Ensure popup exists
    expect(dialog.nativeElement.hasAttribute('ng-reflect-visible')).toBeTrue(); // ✅ Ensure visibility
  });

  // ✅ FIXED TEST FOR CLOSING THE POPUP
  it('should emit close event when Close button is clicked', async () => {
    spyOn(component.close, 'emit');

    // ✅ Open the popup first
    component.visible = true;
    fixture.detectChanges();

    await fixture.whenStable(); // ✅ Wait for Angular to update UI
    await new Promise(resolve => setTimeout(resolve, 200)); // ✅ Ensure PrimeNG modal is fully rendered

    // ✅ Query the close button again to ensure it's available
    fixture.detectChanges();
    const closeButton = fixture.debugElement.query(By.css('button'));

    expect(closeButton).toBeTruthy(); // ✅ Ensure button exists before clicking

    closeButton.nativeElement.click(); // ✅ Click the button

    fixture.detectChanges(); // ✅ Update UI after click

    expect(component.close.emit).toHaveBeenCalled(); // ✅ Verify close event emitted
  });
});
