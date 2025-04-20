import { Component } from '@angular/core';
import { JobSuggestionComponent } from '../job-suggestion/job-suggestion.component';
import {CommonModule} from "@angular/common"; // ✅ Import the component

@Component({
    selector: 'app-left-layout-sidebar',
    imports: [CommonModule, JobSuggestionComponent], // ✅ Add it here
    templateUrl: './candidat_mf.component.html',
    styleUrl: './candidat_mf.scss'
})
export class Candidat_mfComponent {

}
