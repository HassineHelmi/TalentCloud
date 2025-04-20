import openai
class GPTClient:
    def __init__(self, api_key, model="gpt-4"):
        openai.api_key = api_key
        self.model = model

    def query(self, prompt):
        response = openai.ChatCompletion.create(
            model=self.model,
            messages=[{"role": "user", "content": prompt}]
        )
        return response["choices"][0]["message"]["content"]
