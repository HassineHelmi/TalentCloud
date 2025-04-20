# Use a lightweight Python base image
FROM python:3.11-slim

# Set working directory
WORKDIR /app

# Copy dependencies file
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of the application code
COPY . .

# Expose Streamlit default port
EXPOSE 8501

# Optional: prevent Streamlit from opening browser and using dev mode
ENV STREAMLIT_BROWSER_GATHER_USAGE_STATS=false
ENV PYTHONUNBUFFERED=1

# Add this if you use .env for configuration
RUN pip install python-dotenv

# Set the entrypoint
ENTRYPOINT ["streamlit", "run", "main.py", "--server.port=8501", "--server.enableCORS=false"]
