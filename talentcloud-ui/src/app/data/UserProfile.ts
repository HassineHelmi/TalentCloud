export interface UserProfile {
    id: string; // UUID
    name: string;
    email: string;
    profileImageUrl: string;
    experience: number; // Years of experience
    languages: number; // Count of languages spoken
    projects: number; // Count of projects completed
    jobTitle: string;
    education: string;
    skills: string[];
    location: string;
    connections: number;
    posts: number;
  }
  