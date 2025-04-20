from src.llm.gemini_client import GeminiClient
from src.prompt_engineering.templates import build_prompt

client = GeminiClient(api_key="...")
cv_text = open("data/sample_resume.txt").read()
prompt = build_prompt(cv_text)
result = client.query(prompt)
print(result)
