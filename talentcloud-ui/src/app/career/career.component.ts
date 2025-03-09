import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UserProfile } from '../data/UserProfile';
import { Post } from '../data/Post';
import { JobSuggestion } from '../data/JobSuggestion';
import { Achievement } from '../data/Achievement';
import { InterviewSession } from '../data/InterviewSession';
import { Notification } from '../data/Notification';

@Component({
  selector: 'app-career',
  templateUrl: './career.component.html',
  styleUrl: './career.component.scss',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule]
})
export class CareerComponent {

  // Form state management
  isStep1Visible: boolean = true;
  isManualFormVisible: boolean = false;
  isResumeUploadVisible: boolean = false;
  uploadSuccess: boolean = false;

  userProfile: UserProfile = {
    id: 'user-123',
    name: 'Candidate Test',
    email: 'candidate.test@gmail.com',
    profileImageUrl: 'assets/images/user-profile.jpg',
    experience: 2,
    languages: 3,
    projects: 5,
    education:"Esprit",
    jobTitle: 'Development Engineer',
    skills: ['Java', 'Angular'],
    location: 'London',
    connections: 15,
    posts: 2
  };

  posts: Post[] = [
    {
      id: 'post-1',
      userId: 'user-123',
      userName: 'Candidate Test',
      userProfileImage: 'assets/images/user-profile.jpg',
      content: 'New achievements added to my profile!',
      tags: ['JavaProgramming', 'OOP', 'Solid'],
      images: ['assets/images/certification1.jpg', 'assets/images/certification2.jpg'],
      createdAt: new Date()
    }
  ];

  jobSuggestions: JobSuggestion[] = [
    {
      id: 'job-1',
      title: 'Scrum Master',
      companyLogoUrl: 'assets/images/company1.png',
      experienceRequired: '2-3 Years',
      skillsRequired: ['Agile', 'Scrum']
    },
    {
      id: 'job-2',
      title: 'Automation Tester',
      companyLogoUrl: 'assets/images/company2.png',
      experienceRequired: 'Git/Github experience',
      skillsRequired: ['Selenium', 'Cypress']
    }
  ];

  achievements: Achievement[] = [
    {
      id: 'cert-1',
      title: 'Certified Kubernetes Administrator',
      organization: 'Linux Foundation',
      certificateUrl: 'assets/images/certification3.jpg',
      dateAchieved: new Date('2024-03-15')
    }
  ];

  interviewSession: InterviewSession = {
    id: 'interview-1',
    date: new Date('2025-09-05'),
    participantCount: 56,
    interviewMode: 'Online'
  };

  notifications: Notification[] = [
    {
      id: 'notif-1',
      type: 'interview_invite',
      message: 'Your interview starts soon!',
      createdAt: new Date()
    }
  ];

  /**
   * Show the manual profile form
   */
  showManualForm() {
    this.isStep1Visible = false;
    this.isManualFormVisible = true;
  }

  /**
   * Show the resume upload form
   */
  showUploadForm() {
    this.isStep1Visible = false;
    this.isResumeUploadVisible = true;
  }

  /**
   * Handle resume upload event
   */
  uploadResume(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.uploadSuccess = true;
      setTimeout(() => {
        this.isResumeUploadVisible = false;
        this.isManualFormVisible = true;
      }, 1500);
    } else {
      alert("Please select a file before uploading.");
    }
  }

  /**
   * Enable editing a specific field
   */
  editField(field: keyof UserProfile, inputValue: string) {
    (this.userProfile as any)[field] = inputValue;
  }
}