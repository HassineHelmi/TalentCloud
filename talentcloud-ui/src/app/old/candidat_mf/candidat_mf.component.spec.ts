import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Candidat_mfComponent } from './candidat_mf.component';

describe('LeftLayoutSidebarComponent', () => {
  let component: Candidat_mfComponent;
  let fixture: ComponentFixture<Candidat_mfComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Candidat_mfComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Candidat_mfComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
