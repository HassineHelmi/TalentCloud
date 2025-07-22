-- Insert candidates
INSERT INTO candidates (
    candidate_id, created_at, updated_at, blocked,
    job_preferences, job_title, profile_picture, resume, visibility_settings
)
VALUES
    (1, NOW(), NOW(), false, 'Remote, Full-time', 'Full Stack Developer', 'profile_pic_001.jpg', 'resume_001.pdf', 'PUBLIC'),
    (2, NOW(), NOW(), false, 'Hybrid, Part-time', 'Data Analyst', 'profile_pic_002.jpg', 'resume_002.pdf', 'PRIVATE');

-- Insert skills
INSERT INTO skills (
    id, created_at, updated_at, custom_skills, programming_languages,
    soft_skills, technical_skills, tools_and_technologies, candidate_id
)
VALUES
    (1, NOW(), NOW(), 'UI/UX Design', 'JavaScript, Python', 'Teamwork, Communication', 'Data Analysis, Web Development', 'Figma, React, PostgreSQL', 1),
    (2, NOW(), NOW(), 'Data Visualization', 'R, SQL', 'Problem Solving', 'Statistics, Dashboarding', 'PowerBI, Tableau', 2);


-- Insert experiences
INSERT INTO experiences (
    id, created_at, updated_at, date_debut, date_fin,
    description, en_cours, entreprise, titre_poste, site_entreprise,
    technologies, type_contrat, candidate_id
)
VALUES
    (1, NOW(), NOW(), '2021-06-01', '2023-03-01', 'Developed a microservices-based e-commerce platform.', false, 'TechCorp', 'Backend Developer', 'https://techcorp.com', 'Spring Boot, Angular, Docker', 'CDI', 1),
    (2, NOW(), NOW(), '2020-01-01', NULL, 'Maintaining data pipelines and dashboards.', true, 'DataStream Inc.', 'Data Engineer', 'https://datastream.io', 'Python, Apache Airflow, PostgreSQL', 'CDI', 2);

-- Insert educations
INSERT INTO educations (
    id, created_at, updated_at, date_debut, date_fin,
    diplome, domaine_etude, en_cours, institution, moyenne, candidate_id
)
VALUES
    (1, NOW(), NOW(), '2017-09-01', '2020-06-30', 'Bachelor of Science', 'Computer Science', false, 'University of Tech', '15.5', 1),
    (2, NOW(), NOW(), '2016-09-01', '2019-06-30', 'Licence', 'Statistiques et analyse de données', false, 'Faculté des Sciences', '16.2', 2);


-- Insert certifications
INSERT INTO certifications (
    id, created_at, updated_at, date_obtention, datevalidite,
    nom, organisme, url_verification, candidate_id
)
VALUES
    (1, NOW(), NOW(), '2021-08-15', '2024-08-15', 'AWS Certified Developer', 'Amazon', 'https://aws.amazon.com/cert/verify/12345', 1),
    (2, NOW(), NOW(), '2022-05-10', '2025-05-10', 'Google Data Analytics', 'Google', 'https://google.com/cert/verify/67890', 2);


