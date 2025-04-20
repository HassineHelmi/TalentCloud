export interface Notification {
    id: string;
    type: 'message' | 'connection_request' | 'job_alert' | 'interview_invite';
    message: string;
    createdAt: Date;
  }
  