from fastapi import FastAPI
import joblib

# Create FastAPI application
app = FastAPI(title="InterVue AI Model API")

# Load the trained model
model = joblib.load("models/question_classifier.pkl")


@app.get("/")
def home():
    return {
        "message": "InterVue AI Model API is running"
    }


@app.post("/predict")
def predict_question(question: str):
    prediction = model.predict([question])[0]

    return {
        "question": question,
        "category": prediction
    }