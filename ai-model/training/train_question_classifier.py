import pandas as pd
import joblib

from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.metrics import accuracy_score, classification_report

# --------------------------------------------------
# 1. Load dataset
# --------------------------------------------------

df = pd.read_csv("dataset/interview_questions.csv")

X = df["question"]
y = df["category"]

# --------------------------------------------------
# 2. Split dataset
# --------------------------------------------------

X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.20,
    random_state=42,
    stratify=y
)

# --------------------------------------------------
# 3. Create ML pipeline
# --------------------------------------------------

model = Pipeline([
    ("tfidf", TfidfVectorizer()),
    ("classifier", LogisticRegression(max_iter=1000))
])

# --------------------------------------------------
# 4. Train model
# --------------------------------------------------

print("Training model...")
model.fit(X_train, y_train)

print("Training completed.")

# --------------------------------------------------
# 5. Evaluate model
# --------------------------------------------------

y_pred = model.predict(X_test)

accuracy = accuracy_score(y_test, y_pred)

print("\nModel Accuracy:")
print(accuracy)

print("\nClassification Report:")
print(classification_report(y_test, y_pred))

# --------------------------------------------------
# 6. Test with new questions
# --------------------------------------------------

test_questions = [
    "What is dependency injection?",
    "What is a binary search tree?",
    "What is a primary key?",
    "What is JVM?",
    "Why should we hire you?"
]

predictions = model.predict(test_questions)

print("\nPredictions:")

for question, prediction in zip(test_questions, predictions):
    print(f"Question: {question}")
    print(f"Predicted Category: {prediction}")
    print()
    
    # --------------------------------------------------
# 7. Save trained model
# --------------------------------------------------

model_path = "models/question_classifier.pkl"

joblib.dump(model, model_path)

print(f"Model saved successfully to: {model_path}")