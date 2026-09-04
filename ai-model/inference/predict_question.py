import joblib

# Load the trained model
model = joblib.load("models/question_classifier.pkl")

print("Model loaded successfully.")

# Questions to classify
questions = [
    "Explain dependency injection in Spring Boot",
    "What is a hash table?",
    "What is a foreign key?",
    "Explain garbage collection in Java",
    "How do you handle failure at work?"
]

# Predict categories
predictions = model.predict(questions)

print("\nPredictions:\n")

for question, prediction in zip(questions, predictions):
    print(f"Question: {question}")
    print(f"Category: {prediction}")
    print()