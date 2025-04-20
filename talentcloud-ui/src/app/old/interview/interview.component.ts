import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'app-interview-session',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './interview.component.html',
  styleUrl: './interview.component.scss'
})
export class InterviewSessionComponent {}
