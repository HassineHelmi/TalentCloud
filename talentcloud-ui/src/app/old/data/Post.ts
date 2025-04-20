export interface Post {
    id: string;
    userId: string;
    userName: string;
    userProfileImage: string;
    content: string;
    tags: string[];
    images?: string[];
    createdAt: Date;
  }
  