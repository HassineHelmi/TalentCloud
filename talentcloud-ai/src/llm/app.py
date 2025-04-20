import os
import time
import json
import requests
import streamlit as st
import boto3
import psycopg2
# from pymongo import MongoClient
from dotenv import load_dotenv
from bson import ObjectId
import io
import pandas as pd
import google.generativeai as genai
import re
from datetime import datetime

# --- Load .env ---
load_dotenv()

# PostgreSQL connection config
PG_CONN = psycopg2.connect(
    dbname="profiledb",
    user="postgres",
    password="postgres",
    host="localhost",
    port="5432"
)
PG_CURSOR = PG_CONN.cursor()

# --- Config ---
S3_BUCKET = "tekbootwebsite2"
REGION = "us-east-1"
POLLING_INTERVAL = 2
# MONGO_URI = "mongodb://localhost:27017"
DB_NAME = "tekboot"
COLLECTION_NAME = "candidat_inf"
GOOGLE_API_KEY = os.getenv("GOOGLE_API_KEY")

# --- Verify API Key ---
if not GOOGLE_API_KEY:
    st.error("Missing Google API Key!")
    st.stop()

# --- Clients ---
s3_client = boto3.client("s3", region_name=REGION) 
textract_client = boto3.client("textract", region_name=REGION)
# mongo_client = MongoClient(MONGO_URI)
# mongo_collection = mongo_client[DB_NAME][COLLECTION_NAME]

# --- Gemini config ---
genai.configure(api_key=GOOGLE_API_KEY)
model = genai.GenerativeModel("gemini-2.0-flash-thinking-exp-01-21")

# --- Textract Helpers ---
def start_textract_job(bucket, file):
    return textract_client.start_document_text_detection(
        DocumentLocation={'S3Object': {'Bucket': bucket, 'Name': file}}
    )["JobId"]

def check_job_complete(job_id):
    while True:
        time.sleep(POLLING_INTERVAL)
        response = textract_client.get_document_text_detection(JobId=job_id)
        if response["JobStatus"] in ["SUCCEEDED", "FAILED"]:
            return response["JobStatus"] == "SUCCEEDED"

def get_textract_results(job_id):
    pages = []
    response = textract_client.get_document_text_detection(JobId=job_id)
    pages.append(response)
    while "NextToken" in response:
        response = textract_client.get_document_text_detection(JobId=job_id, NextToken=response["NextToken"])
        pages.append(response)
    return pages

def extract_text(pages):
    return "\n".join(block["Text"] for page in pages for block in page["Blocks"] if block["BlockType"] == "LINE")

# --- Regex NER Extraction --- 
def extract_information(text):
    info = {
        "name": None, "email": None, "contact": None,
        "job_category": None, "experience": [], "skills": [], "certifications": []
    }

    # Regex to extract name
    name_match = re.search(r"(nom|name|prénom|first name):?\s*([a-zA-Z]+(?:\s[a-zA-Z]+)*)", text, re.IGNORECASE)
    if name_match:
        info['name'] = name_match.group(2).strip().title()

    # Regex to extract email
    email_match = re.findall(r"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}", text)
    if email_match:
        info['email'] = email_match[0].strip()

    # Regex to extract phone number
    phone_match = re.search(r"(?:\+\d{1,3}\s?)?\(?\d{3}\)?[\s.-]?\d{3}[\s.-]?\d{4}", text)
    if phone_match:
        info['contact'] = ""

    # Regex to extract skills (focused on technical skills)
    skills_match = re.findall(r"\b(Java|Spring|PostgreSQL|MongoDB|Angular|Vue.js|Docker|Kubernetes|Python|SQL|Hibernate|ElasticSearch|JUnit|Mockito|CI/CD|Git|Bitbucket|Jenkins|Azure DevOps)\b", text)
    if skills_match:
        info["skills"] = list(set([skill.strip() for skill in skills_match]))  # Remove duplicates

    # Regex to extract experience (company, role, period, etc.)
    experience_match = re.findall(r"Company:\s*([^\n]+).+?Role:\s*([^\n]+).+?Project name:\s*([^\n]+).+?\((\d{4}[-/]\d{4}|[A-Za-z]+\s\d{4})\)", text, re.DOTALL)
    for exp in experience_match:
        info["experience"].append({
            "company": exp[0].strip(),
            "role": exp[1].strip(),
            "project_name": exp[2].strip(),
            "time_period": exp[3].strip()
        })

    return info

# --- Gemini AI Helper Function --- 
def analyze_resume_with_gemini(cv_text):
    if not isinstance(cv_text, str):
        return {"error": "CV text is not a valid string."}

    prompt = f"""
Tu es un expert RH. À partir du CV ci-dessous, détecte automatiquement la catégorie de métier, puis analyse les compétences actuelles, celles à améliorer, les forces, faiblesses et suggère des formations.

Format JSON strict :
{{
  "job_category": "ex: Chargé(e) de Communication/Marketing",
  "overall_evaluation": "Résumé global",
  "strengths": ["Force 1", "Force 2"],
  "weaknesses": ["Faiblesse 1", "Faiblesse 2"],
  "missing_skills": ["Compétence manquante"],
  "recommended_courses": ["Cours 1", "Cours 2"],
  "skills": ["Skill 1", "Skill 2"]
}}

CV :
{cv_text}

Réponds uniquement avec un JSON valide.
    """
    response = model.generate_content(prompt)
    
    try:
        return json.loads(re.sub(r"```json|```", "", response.text.strip()))
    except Exception as e:
        return {"error": "Invalid JSON", "raw": response.text}

# --- Merge and store --- 
def process_resume(file):
    job_id = start_textract_job(S3_BUCKET, file)
    if not check_job_complete(job_id):
        return None

    responses = get_textract_results(job_id)
    text = extract_text(responses)

    # Extract information
    gemini_data = analyze_resume_with_gemini(text)
    regex_data = extract_information(text)

    full_data = {
        "filename": file,
        "resume_text": text,
        "processed_at": time.strftime("%Y-%m-%d %H:%M:%S"),
        **gemini_data,
        **regex_data
    }

    # Insert into PostgreSQL
    PG_CURSOR.execute("""
        INSERT INTO public.user_profile (
            filename, resume_text, processed_at,
            name, email, contact,
            job_category, overall_evaluation,
            strengths, weaknesses, missing_skills, recommended_courses, skills,
            experience, certifications
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """, (
        full_data.get("filename"),
        full_data.get("resume_text"),
        datetime.strptime(full_data.get("processed_at"), "%Y-%m-%d %H:%M:%S"),
        full_data.get("name"),
        full_data.get("email"),
        full_data.get("contact"),
        full_data.get("job_category"),
        full_data.get("overall_evaluation"),
        full_data.get("strengths"),
        full_data.get("weaknesses"),
        full_data.get("missing_skills"),
        full_data.get("recommended_courses"),
        full_data.get("skills"),
        json.dumps(full_data.get("experience") or {}),
        json.dumps(full_data.get("certifications") or {})
    ))

    PG_CONN.commit()
    return full_data
    # job_id = start_textract_job(S3_BUCKET, file)
    # if not check_job_complete(job_id):
    #     return None

    # responses = get_textract_results(job_id)
    # text = extract_text(responses)

    # # Extract information using Gemini
    # gemini_data = analyze_resume_with_gemini(text)

    # # Extract additional information via Regex
    # regex_data = extract_information(text)

    # full_data = {
    #     "filename": file,
    #     "resume_text": text,
    #     "processed_at": time.strftime("%Y-%m-%d %H:%M:%S"),
    #     **gemini_data,
    #     **regex_data
    # }

    # mongo_collection.insert_one(full_data)
    # return full_data

# --- Display helper --- 
def clean_for_display(obj):
    def clean(val):
        if isinstance(val, ObjectId):
            return str(val)
        elif isinstance(val, list):
            return val
        elif isinstance(val, dict):
            return clean_for_display(val)
        return val
    return {k: clean(v) for k, v in obj.items()}

# --- S3 list --- 
def list_s3_pdfs(bucket):
    response = s3_client.list_objects_v2(Bucket=bucket)
    return [obj["Key"] for obj in response.get("Contents", []) if obj["Key"].endswith(".pdf")]

# --- Streamlit UI --- 
def main():
    st.set_page_config(page_title="AI Resume Processor", layout="wide")
    st.title("📄 Analyse automatique de CV (Gemini AI)")

    pdfs = list_s3_pdfs(S3_BUCKET)
    if not pdfs:
        st.warning("Aucun fichier PDF trouvé dans le bucket.")
        return

    st.subheader("📋 CV disponibles dans S3")

    all_results = []

    for pdf in pdfs:
        col1, col2 = st.columns([4, 1])
        with col1:
            st.markdown(f"- **{pdf}**")
        with col2:
            if st.button("Analyser", key=pdf):
                with st.spinner("Traitement..."):
                    result = process_resume(pdf)
                    if result:
                        cleaned = clean_for_display(result)
                        all_results.append(cleaned)
                        st.success("✅ Terminé !")
                        st.json(cleaned)

    # --- Batch Processing --- 
    st.markdown("---")
    st.subheader("🚀 Traitement batch de tous les CV")

    if st.button("📦 Lancer le traitement batch"):
        all_results = []

        with st.spinner("Traitement de tous les CV en cours..."):
            for pdf in pdfs:
                result = process_resume(pdf)
                if result:
                    cleaned = clean_for_display(result)
                    all_results.append(cleaned)

        if all_results:
            df = pd.DataFrame(all_results)
            st.success(f"{len(all_results)} CV traités.")
            st.dataframe(df)

            # Export JSON
            json_data = json.dumps(all_results, indent=4, ensure_ascii=False)
            st.download_button("📥 Télécharger JSON", data=json_data, file_name="resumes.json", mime="application/json")

            # Export CSV
            csv_buffer = io.StringIO()
            df.to_csv(csv_buffer, index=False)
            st.download_button("📥 Télécharger CSV", data=csv_buffer.getvalue(), file_name="resumes.csv", mime="text/csv")
        else:
            st.warning("Aucun résultat à afficher.")

main()
