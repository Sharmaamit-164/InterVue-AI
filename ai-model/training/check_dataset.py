import pandas as pd

# Load the interview question dataset
df = pd.read_csv("dataset/interview_questions.csv")

# Display the complete dataset
print(df)

print("\nDataset shape:")
print(df.shape)

print("\nCategories:")
print(df["category"].value_counts())

print("\nDifficulties:")
print(df["difficulty"].value_counts())