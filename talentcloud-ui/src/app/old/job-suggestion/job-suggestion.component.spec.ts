import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JobSuggestionComponent } from './job-suggestion.component';

describe('JobSuggestionComponent', () => {
  let component: JobSuggestionComponent;
  let fixture: ComponentFixture<JobSuggestionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobSuggestionComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(JobSuggestionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
