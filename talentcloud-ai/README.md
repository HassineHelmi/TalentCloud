# AI-Powered Resume Analyzer

## 🙌 **How to run on Windows ?**

- Create local virtual env
- Install Rust (https://rustup.rs/)
- Add .cargo/bin to PATH
- Import requirements
- Run: streamlit run app.py
- Visit: http://localhost:8501/

**AI-Powered Resume Analyzer**, a cutting-edge application designed to mimic the expertise of an HR professional! This tool leverages the power of **Google Generative AI** to analyze resumes, evaluate job compatibility, and offer actionable insights for career enhancement.

---

## 📋 **Project Overview**

The **AI-Powered Resume Analyzer** serves as a virtual HR assistant, providing:

- Detailed resume evaluation, including strengths and weaknesses.
- Suggestions for skill improvement and recommended courses.
- Job-specific resume analysis to measure compatibility and alignment with job descriptions.

Whether you’re a job seeker or a recruiter, this tool simplifies resume assessment and improvement.

---

## 🔑 **Features**

### 1️⃣ **General Resume Analysis**

- Summarizes the resume in one line.
- Highlights existing skill sets.
- Identifies skill gaps and suggests improvements.
- Recommends popular courses to enhance the resume.
- Provides a thorough evaluation of strengths and weaknesses.

### 2️⃣ **Resume Matching with Job Description**

- Analyzes resume compatibility with a specific job description.
- Provides a match score in percentage.
- Highlights missing skills and areas needing improvement.
- Suggests whether the resume is ready for the job or requires further enhancements.

---

## 🛠️ **Tech Stack**

| **Component**          | **Technology**                                                           |
| ---------------------- | ------------------------------------------------------------------------ |
| **Frontend**           | [Streamlit](https://streamlit.io/)                                       |
| **Backend**            | Python                                                                   |
| **AI Model**           | [Google Generative AI (Gemini)](https://developers.generativeai.google/) |
| **PDF Parsing**        | `pdfplumber`                                                             |
| **OCR Fallback**       | `pytesseract`                                                            |
| **Environment Config** | `.env` for API key security                                              |

---

## 📊 **How It Works**

1. **Resume Parsing**

   - Extracts text from PDF files using `pdfplumber` or OCR as a fallback.

2. **AI Analysis**

   - Utilizes Google Generative AI to summarize and analyze resume content.
   - Matches skills with job descriptions for compatibility scoring.

3. **Insightful Feedback**
   - Provides actionable suggestions for skill enhancement, including course recommendations.
   - Highlights strengths and weaknesses to refine resumes for better opportunities.

---

![image](https://github.com/user-attachments/assets/418e54ef-82d0-474b-a6bc-9a30d72f27f5)

## 🙌 **Contributing**

Welcome contributions to make this tool better!

1. **Fork** the repository.
2. **Create a new branch** for your feature or bug fix.
3. **Submit a pull request** with detailed information about your changes.

Ensure that you have access to the API key for Google Gemini and that you've set up authentication correctly using:
Downloading google_auth_oauthlib-1.2.1-py2.py3-none-any.whl (24 kB)

# 🧠 Generative AI Resume Processor

This project processes resumes (CVs) using Gemini and OpenAI models, extracts key candidate information, and stores results in PostgreSQL. It's designed using a modular, scalable structure for easy maintenance and experimentation.

---

## 📂 Project Structure

```bash
generative_ai_project/
│
├── config/                  # Configuration (YAML-based)
│   ├── __init__.py
│   ├── model_config.yaml    # LLM keys and model names
│   ├── prompt_templates.yaml# Few-shot and system prompts
│   └── logging_config.yaml  # Logging levels and formats
│
├── src/                     # Source code
│   ├── llm/                 # Language model clients
│   │   ├── __init__.py
│   │   ├── base.py          # Abstract LLM base class
│   │   ├── gpt_client.py    # OpenAI client implementation
│   │   └── gemini_client.py # Gemini client (Google)
│   │
│   ├── prompt_engineering/  # Prompt formatting logic
│   │   ├── __init__.py
│   │   ├── templates.py
│   │   ├── few_shot.py
│   │   └── chainer.py
│   │
│   ├── utils/               # Common tools
│   │   ├── __init__.py
│   │   ├── rate_limiter.py
│   │   ├── token_counter.py
│   │   ├── cache.py
│   │   └── logger.py
│   │
│   └── handlers/            # Resume analysis and DB writer
│       ├── __init__.py
│       └── error_handler.py
│
├── data/                    # Input/Output
│   ├── cache/               # Intermediate cache
│   ├── prompts/             # Prompt templates
│   ├── outputs/             # Generated SQL dumps
│   └── embeddings/          # Saved vectors (future use)
│
├── examples/                # Use case samples
│   ├── basic_completion.py
│   ├── chat_session.py
│   └── chain_prompts.py
│
├── notebooks/               # Exploration notebooks
│   ├── prompt_testing.ipynb
│   ├── response_analysis.ipynb
│   └── model_experimentation.ipynb
│
├── requirements.txt         # Pip dependencies
├── setup.py                 # Optional package installer
├── README.md                # This file
└── Dockerfile               # Containerization setup

```

⚙️ Key Components

- config/: Stores model settings, prompt formats, and logging setup.

- src/llm/: Implements language model wrappers for Gemini and OpenAI.

- src/prompt_engineering/: Centralizes reusable prompt strategies.

- src/handlers/: Manages resume parsing, job execution, and DB insertion.

- data/: Storage for raw text, structured outputs, and prompt variations.

- notebooks/: For testing, tuning, and inspecting model behavior.

🚀 Getting Started

- Clone this repo

- Run pip install -r requirements.txt

- Add your .env file with:

- OPENAI_API_KEY=your_key
- GOOGLE_API_KEY=your_key

- Add CVs to S3

- Run main.py or launch the Streamlit UI
