import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Skill } from '../data/Skill';

@Component({
  selector: 'app-career',
  templateUrl: './career.component.html',
  styleUrl: './career.component.scss',
  standalone: true,
  imports: [CommonModule, RouterModule ]
})
export class CareerComponent {

  skills: Skill[] = [
    {src:"#", name: 'Programming', description: ['Code Craftsmanship.'] },
    {src:"#", name: 'Backend', description: ['Key frameworks for backend development.' ]},
    { src:"#",name: 'Frontend', description: ['Modern frontend technologies.'] },
    { src:"#",name: 'Databases', description: ['Skills with relational and NoSQL databases.'] },
    { src:"#",name: 'Cloud', description: ['Platforms and tools for cloud development and CI/CD pipelines.'] },
    { src:"#",name: 'Methodologies', description: ['Agile principles and PMP certified consultants.'] }
  ];

  
  

  jobs = [
    {
      title: 'Ferrybox Dashboard (CLAIM)',
      description: "Developed Africa's first web solution for tracking, analyzing, and sharing Mediterranean surface water data.",
      backend: 'Java',
      frontend: 'Angular',
      integration:'Docker',
      database:'PostgreSQL',
      routerLink:"career-details/Ferrybox"
    },
    {
      title: "Sof'TT",
      description: "Built a platform to enhance productivity at Sofrecom: features like API/LDAP integration, cron jobs, and CI/CD.",
      backend: 'Java',
      frontend: 'Angular',
      integration:'Gitlab',
      database:'PostgreSQL',
      routerLink:"/career-details/TT"
    },
    {
      title: 'TeamTool',
      description: "Maintained microservices architecture for the project, ESP32 connection.",
      backend: 'Java',
      frontend: 'Angular',
      integration:'AWS',
      database:'MySQL',
      routerLink:"/career-details/TeamTool"
    },
    {
      title: 'Teams+',
      description: "Created a tool to streamline collaboration for Eurostar, backend/frontend integration, automated data refresh processes.",
      backend: 'Java',
      frontend: 'Angular',
      integration:'Azure',
      database:'MongoDB',
      routerLink:"/career-details/Teams"
    }
  ];

}
