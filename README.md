TalentCloud - Project Setup Guide 🚀
Welcome to the TalentCloud project! Follow this guide to set up the frontend and backend locally.

1. Setting up SSH Key for GitHub 🔑
To authenticate your GitHub access, follow these steps:

Check for existing SSH keys:

sh
Copy
Edit
ls -al ~/.ssh
If you see id_rsa.pub or id_ed25519.pub, you already have an SSH key.

Generate a new SSH key (if needed):

sh
Copy
Edit
ssh-keygen -t ed25519 -C "your-email@example.com"
Press Enter to save in the default location (~/.ssh/id_ed25519).
Set a passphrase (optional but recommended).
Add your SSH key to the SSH agent:

sh
Copy
Edit
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_ed25519
Copy your SSH key and add it to GitHub:

sh
Copy
Edit
cat ~/.ssh/id_ed25519.pub
Copy the output and go to GitHub → Settings → SSH and GPG keys → New SSH Key.
Paste the key and save.
Test the SSH connection:

sh
Copy
Edit
ssh -T git@github.com
You should see:
"Hi <username>! You've successfully authenticated."

2. Cloning the Repository 🖥️
Frontend
sh
Copy
Edit
git clone git@github.com:your-org/talentcloud-frontend.git
cd talentcloud-frontend
Backend
sh
Copy
Edit
git clone git@github.com:your-org/talentcloud-backend.git
cd talentcloud-backend
3. Setting up the Frontend 🎨
Requirements
Node.js 18+
Nx CLI (for monorepo management)
Angular CLI
Installation Steps
Check Node.js version:

sh
Copy
Edit
node -v
If Node.js is missing or outdated, install it from Node.js official site.

Install Nx CLI:

sh
Copy
Edit
npm install -g nx
Install dependencies:

sh
Copy
Edit
cd talentcloud-frontend
npm install
Set up environment files: Copy .env.example to .env and update necessary values.

Run the project locally:

sh
Copy
Edit
nx serve shell
The frontend will be available at http://localhost:4200/.

4. Setting up the Backend ⚙️
Requirements
Java 17+
Spring Boot 3
PostgreSQL
Liquibase (for database migrations)
Docker (optional but recommended)
Installation Steps
Check Java version:

sh
Copy
Edit
java -version
If Java is missing or outdated, install OpenJDK 17+.

Set up PostgreSQL:

Install PostgreSQL if not installed.
Create a database:
sh
Copy
Edit
createdb talentcloud_db
Configure environment variables: Copy .env.example to .env and update:

ini
Copy
Edit
DATABASE_URL=jdbc:postgresql://localhost:5432/talentcloud_db
DATABASE_USER=your_user
DATABASE_PASSWORD=your_password
Run database migrations:

sh
Copy
Edit
./mvnw liquibase:update
Start the backend:

sh
Copy
Edit
./mvnw spring-boot:run
The backend will be available at http://localhost:8080/.

5. Running Tests 🧪
Frontend Tests
Unit tests:
sh
Copy
Edit
nx test profile-mfe
E2E tests:
sh
Copy
Edit
nx e2e profile-mfe-e2e
Backend Tests
Run JUnit tests:
sh
Copy
Edit
./mvnw test
6. How to Push Code 🚀
Create a new feature branch:

sh
Copy
Edit
git checkout -b feature/your-feature-name
Commit your changes:

sh
Copy
Edit
git add .
git commit -m "feat: add new feature"
Push the branch to GitHub:

sh
Copy
Edit
git push origin feature/your-feature-name
Create a Pull Request (PR) on GitHub and request a review.

7. Merging Strategy 🌱
Branches:

main → Stable production branch.
dev → Development branch.
qa → Quality assurance/testing branch.
feature/* → Feature branches.
Rules:

Feature branches merge into dev.
dev merges into qa for testing.
Only tested and approved code from qa is merged into main.
8. API Documentation 📜
Swagger UI:
Once the backend is running, open:
👉 http://localhost:8080/swagger-ui.html
9. Troubleshooting ❗
Common Issues
Issue	Solution
Permission denied (publickey)	Ensure your SSH key is added to GitHub
Node.js version mismatch	Use nvm use 18 or reinstall Node.js
Port already in use	Run lsof -i :8080 and kill the process
Database connection issues	Ensure PostgreSQL is running and credentials are correct
🎯 Now you are ready to start contributing! 🚀
