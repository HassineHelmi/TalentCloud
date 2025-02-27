# TalentCloud - Monorepo Setup Guide

## 📌 Prerequisites

Ensure you have the following installed before proceeding:

- [Git](https://git-scm.com/downloads)
- [Node.js (LTS Version)](https://nodejs.org/)
- [Nx CLI](https://nx.dev/getting-started/installation)
- [Angular CLI](https://angular.io/cli)
- [Java 21](https://adoptium.net/)
- [PostgreSQL](https://www.postgresql.org/download/)
- [Docker](https://www.docker.com/products/docker-desktop/)
- [Maven](https://maven.apache.org/download.cgi)
- [Lombok](https://projectlombok.org/download)

---

## 🔑 Setting Up SSH Key for GitHub

1. Check if you already have an SSH key:
```sh
   ls -al ~/.ssh
```
If you see id_rsa.pub or id_ed25519.pub, you already have an SSH key.

2. Generate a new SSH key (if needed)
```sh
   ssh-keygen -t ed25519 -C "your-email@example.com"
```
Press Enter to save in the default location (~/.ssh/id_ed25519).   
Set a passphrase (optional but recommended).

3. Add your SSH key to the SSH agent
```sh
   eval "$(ssh-agent -s)" 
   ssh-add ~/.ssh/id_ed25519
```
4. Copy your SSH key and add it to GitHub
```sh
   cat ~/.ssh/id_ed25519.pub
```
Copy the output and go to GitHub → Settings → SSH and GPG keys → New SSH Key.  
Paste the key and save.
5. Test the SSH connection
```sh
   ssh -T git@github.com
```
Expected output:  
**Hi <username>! You've successfully authenticated.**

## Cloning the Repository
1. Frontend
```sh
   git clone git@github.com:tekboot/TalentCloud.git
   cd talentcloud-ui
```
2. Backend
```sh
   git clone git@github.com:tekboot/TalentCloud.git
   cd talentcloud-api
```

## Setting up the Frontend
### Requirements
- Node.js 18+
- Nx CLI (for monorepo management)
- Angular CLI
### Installation Steps
```sh
# Check Node.js version
node -v

# Install Nx CLI globally
npm install -g nx

# Install dependencies
cd talentcloud-ui
npm install
```
### Set up environment files
Copy .env.example to .env and update necessary values.
### Run the project locally
```sh
nx serve shell
```
The frontend will be available at http://localhost:4200/.

## Setting up the Backend
### Requirements
- Java 21
- Spring Boot 3
- PostgreSQL
- Liquibase (for database migrations)
- Docker 
### Installation Steps
```sh
# Check Java version
java -version

# Set up PostgreSQL
createdb talentcloud_db
```
### Configure environment variables
Copy .env.example to .env and update:
```sh
DATABASE_URL=jdbc:postgresql://localhost:5432/talentcloud_db
DATABASE_USER=your_user
DATABASE_PASSWORD=your_password
```
### Run database migrations
```sh
./mvnw liquibase:update
```
### Start the backend
```sh
./mvnw spring-boot:run
```
The backend will be available at http://localhost:8080/.

## Running Tests
### Frontend Tests
- Unit tests
```sh
nx test profile-mfe
```
- E2E tests
```sh
nx e2e profile-mfe-e2e
```
### Backend Tests
- Run JUnit tests
```sh
./mvnw test
```

## How to Push Code
```sh
# Create a new feature branch (use Jira)
git checkout -b feature/your-feature-name

# Commit your changes
git add .
git commit -m "feat: add new feature"

# Push the branch to GitHub
git push origin feature/your-feature-name
```
Create a Pull Request (PR) on GitHub and request a review (use Jira).


## Merging Strategy
1. **Branching Model**  
  main → Stable production branch.  
  dev → Development branch.  
  qa → Quality assurance/testing branch.  
  feature/* → Feature branches.  
2. **Merging Rules**  
   Feature branches merge into dev.  
   dev merges into qa for testing.  
   Only tested and approved code from qa is merged into main.

## API Documentation
**Swagger UI:**  
Once the backend is running, open:  
👉 http://localhost:8080/swagger-ui.html

## Troubleshooting

| **Issue**                        | **Solution**                                      |
|----------------------------------|--------------------------------------------------|
| Permission denied (publickey)    | Ensure your SSH key is added to GitHub           |
| Node.js version mismatch        | Use `nvm use 18` or reinstall Node.js            |
| Port already in use             | Run `lsof -i :8080` and kill the process         |
| Database connection issues      | Ensure PostgreSQL is running and credentials are correct |


**Now you are ready to start contributing!**

   


