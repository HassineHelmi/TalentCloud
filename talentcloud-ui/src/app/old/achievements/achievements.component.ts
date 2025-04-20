import { Component } from '@angular/core';
import {FormsModule} from "@angular/forms";
import { InterviewSessionComponent } from '../interview/interview.component'; // ✅ Import the component

@Component({
  selector: 'app-right-layout-sidebar',
  imports: [
    FormsModule, InterviewSessionComponent
  ],
  templateUrl: './achievements.component.html',
  standalone: true,
  styleUrl: './achievements.component.scss'
})
export class AchievementsComponent {

}
