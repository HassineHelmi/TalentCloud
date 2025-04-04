export interface InterviewSession {
    id: string;
    date: Date;
    participantCount: number;
    interviewMode: 'Online' | 'In-Person';
  }
  