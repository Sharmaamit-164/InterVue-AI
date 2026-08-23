# 🤖 InterVue AI

### AI-Powered Virtual Interview Platform

InterVue AI is a full-stack AI-powered virtual interview platform designed to simulate realistic technical and HR interviews using a **real-time talking AI avatar**.

The platform allows users to upload their resume, provide a job description, configure an interview, and participate in a voice-based interview with an AI interviewer.

The AI interviewer dynamically generates questions, listens to the user's answers, evaluates their responses, and provides a detailed performance report at the end of the interview.

---

## 🎯 Project Goal

The goal of InterVue AI is to provide a realistic and personalized interview-practice environment where users can practice interviews without requiring a human interviewer.

Unlike a traditional chatbot, InterVue AI provides:

* 🤖 Real-time talking AI avatar
* 🎤 Voice-based conversation
* 📄 Resume-aware questions
* 💼 Job-description-aware interviews
* 🧠 Adaptive AI questioning
* 📊 AI-powered answer evaluation
* 📈 Interview performance analytics

---

# 🚀 Core Features

## 1. 🔐 User Authentication

Users can create an account and securely access their interview dashboard.

Features:

* User registration
* User login
* JWT authentication
* Password encryption
* User profile
* Secure API access

---

## 2. 📄 Resume Management

Users can upload and manage their resumes.

Features:

* Upload PDF resume
* View uploaded resume
* Replace resume
* Delete resume
* Resume text extraction
* Resume information processing

The extracted resume information can be used by the AI interviewer to create personalized questions.

---

## 3. 🤖 AI Resume Analysis

The AI analyzes the uploaded resume and extracts useful information such as:

* Technical skills
* Programming languages
* Frameworks
* Projects
* Education
* Experience
* Certifications

Example:

```text
Technical Skills
----------------
Java
Spring Boot
React
PostgreSQL
Git
REST API
```

The extracted information is later used during interview generation.

---

# 4. 💼 Job Description Management

Users can add a job description before starting an interview.

Example:

```text
Position:
Java Backend Developer

Required Skills:
Java
Spring Boot
REST API
PostgreSQL
Docker
AWS
```

The system analyzes the job description and identifies:

* Required skills
* Technologies
* Responsibilities
* Experience requirements
* Important keywords

---

# 5. 🎯 Resume–Job Matching

InterVue AI compares the user's resume with the selected job description.

Example:

```text
Overall Match: 82%

Matched Skills:
✓ Java
✓ Spring Boot
✓ PostgreSQL
✓ REST API

Missing Skills:
✗ Docker
✗ AWS
```

The system can also provide recommendations for improving the user's job readiness.

---

# 6. ⚙️ Interview Configuration

Before starting an interview, the user can configure the interview.

Possible settings:

```text
Interview Type:
Technical / HR / Mixed

Job Role:
Java Backend Developer

Difficulty:
Easy / Medium / Hard

Number of Questions:
5 / 10 / 15

Interview Duration:
15 / 30 / 45 minutes
```

---

# 7. 🎥 AI Video Interview

The main feature of InterVue AI is the virtual interview.

The user's camera remains **OFF**.

The AI interviewer appears as a **real-time talking avatar**.

```text
┌─────────────────────────────────────────┐
│            INTERVUE AI                  │
│                                         │
│             🤖 AI INTERVIEWER           │
│                                         │
│       "Tell me about yourself."         │
│                                         │
│                                         │
│          🎤 Microphone ON               │
│                                         │
│                 End Interview           │
└─────────────────────────────────────────┘
```

The AI avatar can:

* Speak
* Move lips
* Blink
* Move head
* Display natural facial movements
* Respond in real time

---

# 8. 🎤 Voice-Based Interview

The interview is completely voice-based.

### AI → User

```text
AI Text
   ↓
Text-to-Speech
   ↓
AI Avatar
   ↓
User hears question
```

### User → AI

```text
User speaks
   ↓
Microphone
   ↓
Speech-to-Text
   ↓
User Answer
   ↓
AI
```

This creates a natural conversational interview experience.

---

# 9. 🧠 Adaptive AI Interviewer

The AI interviewer does not simply ask a predefined list of questions.

It considers:

```text
Resume
+
Job Description
+
Interview Type
+
Difficulty
+
Previous Questions
+
Previous Answers
```

and generates the next question dynamically.

Example:

```text
AI:
"Can you explain dependency injection in Spring Boot?"

User:
"Dependency injection is a design pattern..."

AI:
"Good. Can you explain how Spring manages dependency injection internally?"
```

If the user's answer is weak, the AI can ask a simpler follow-up question.

---

# 10. 📊 AI Answer Evaluation

After every answer, the AI evaluates the response.

Possible evaluation criteria:

```text
Technical Accuracy
Relevance
Completeness
Clarity
Communication
Answer Structure
```

Example:

```text
Technical Accuracy: 8/10
Relevance:          9/10
Clarity:            7/10
Completeness:       8/10
Communication:      7/10
```

The evaluation can be stored for generating the final report.

---

# 11. 📈 Interview Performance Report

After the interview is completed, the system generates a detailed report.

Example:

```text
================================
       INTERVIEW REPORT
================================

Overall Score: 78/100

Technical Knowledge: 82/100
Communication:       75/100
Relevance:           84/100
Clarity:             72/100

Strong Areas:
✓ Java
✓ Spring Boot
✓ REST APIs

Needs Improvement:
• Docker
• Spring Security
• Answer structure

Recommendations:
• Practice explaining projects
• Improve Spring Security concepts
• Give more structured answers
```

---

# 12. 📚 Interview History

Users can view their previous interviews.

```text
Java Backend Developer
Score: 78%
Date: August 23, 2026

Spring Boot Developer
Score: 71%
Date: August 20, 2026

Backend Developer
Score: 65%
Date: August 17, 2026
```

---

# 13. 📈 Performance Analytics

The system tracks interview performance over time.

Example:

```text
Interview 1 → 65%
Interview 2 → 71%
Interview 3 → 78%
Interview 4 → 84%
```

Users can identify whether their interview performance is improving.

---

# 🏗️ System Architecture

```text
                         ┌──────────────────────┐
                         │        USER          │
                         │                      │
                         │  Microphone + Browser│
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │      React.js        │
                         │      Frontend        │
                         │                      │
                         │ Dashboard            │
                         │ Resume                │
                         │ Jobs                  │
                         │ Interview             │
                         │ AI Avatar              │
                         │ Reports               │
                         └──────────┬───────────┘
                                    │
                           REST + WebSocket
                                    │
                                    ▼
                    ┌─────────────────────────────┐
                    │        Spring Boot         │
                    │          Backend           │
                    │                             │
                    │ Authentication              │
                    │ Resume Management           │
                    │ Job Management              │
                    │ Interview Management        │
                    │ AI Interview Engine         │
                    │ Evaluation                  │
                    │ Reports                     │
                    └─────────────┬───────────────┘
                                  │
             ┌────────────────────┼────────────────────┐
             │                    │                    │
             ▼                    ▼                    ▼
      ┌─────────────┐      ┌─────────────┐      ┌──────────────┐
      │ PostgreSQL  │      │    MinIO    │      │ AI Services  │
      │             │      │             │      │              │
      │ Users       │      │ Resume PDF  │      │ LLM          │
      │ Jobs        │      │ Audio       │      │ STT          │
      │ Interviews  │      │ Documents   │      │ TTS          │
      │ Questions   │      │ Reports     │      │ Avatar API   │
      │ Answers     │      │             │      │              │
      └─────────────┘      └─────────────┘      └──────────────┘
```

---

# 🧠 AI Interview Architecture

```text
                    Interview Session
                           │
                           ▼
                  AI Interview Engine
                           │
              ┌────────────┼────────────┐
              │            │            │
              ▼            ▼            ▼
           Resume      Job Description  History
              │            │            │
              └────────────┼────────────┘
                           ▼
                     LLM / AI Model
                           │
                           ▼
                    Generate Question
                           │
                           ▼
                     Text-to-Speech
                           │
                           ▼
                     Avatar Service
                           │
                           ▼
                     🤖 AI Avatar
                           │
                           ▼
                       User hears
                           │
                           ▼
                    User speaks 🎤
                           │
                           ▼
                    Speech-to-Text
                           │
                           ▼
                     User Answer
                           │
                           ▼
                    AI Evaluation
                           │
                           ▼
                   Generate Next Question
```

---

# 🔌 Communication Architecture

The application will use two major communication methods.

### REST API

Used for normal operations:

```text
Authentication
Resume Upload
Job Management
Interview Creation
Reports
Interview History
Profile
```

### WebSocket

Used for real-time interview communication:

```text
AI Speaking
User Listening
Interview State
Question Events
Answer Processing
AI Response
Interview Completion
```

---

# 🗄️ Database Design

Main entities:

```text
User
 │
 ├── Resume
 │
 ├── Job
 │
 └── Interview
        │
        ├── Question
        │      │
        │      └── Answer
        │
        └── Interview Report
```

Expected tables:

```text
users
resumes
jobs
interviews
questions
answers
evaluations
reports
```

---

# 🛠️ Technology Stack

## Frontend

* React.js
* HTML5
* CSS3
* JavaScript
* Axios
* WebSocket Client

## Backend

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* REST APIs
* WebSocket

## Database

* PostgreSQL

## Authentication

* JWT
* Spring Security

## File Storage

* MinIO / Object Storage

## AI

* LLM API
* Speech-to-Text API
* Text-to-Speech API
* Real-Time AI Avatar API

## Documentation & Testing

* Swagger / OpenAPI
* Postman

## Development Tools

* IntelliJ IDEA
* VS Code
* Git
* GitHub
* Docker

---

# 📁 Planned Project Structure

```text
InterVue-AI/
│
├── backend/
│   └── Spring Boot Application
│
├── frontend/
│   └── React Application
│
├── docs/
│   └── System Design
│
├── docker/
│   └── Docker Configuration
│
├── README.md
└── .gitignore
```

---

# 🔐 Security

The application will implement:

* JWT authentication
* Password hashing
* Role-based authorization
* Input validation
* Secure API endpoints
* File upload validation
* Protected user data
* Secure communication using HTTPS in production
* Environment variables for API keys

AI API keys will **never be stored in the frontend**.

---

# 🚧 Development Roadmap

## Phase 1 — Project Setup

* [ ] Create GitHub repository
* [ ] Create Spring Boot backend
* [ ] Create React frontend
* [ ] Configure PostgreSQL
* [ ] Configure Git
* [ ] Configure project structure

## Phase 2 — Authentication

* [ ] User registration
* [ ] User login
* [ ] JWT authentication
* [ ] Spring Security
* [ ] Protected APIs

## Phase 3 — Resume Management

* [ ] Resume upload
* [ ] PDF processing
* [ ] Resume storage
* [ ] Resume management APIs

## Phase 4 — Job Management

* [ ] Add job description
* [ ] Edit job
* [ ] Delete job
* [ ] Job analysis

## Phase 5 — AI Resume Analysis

* [ ] AI resume analysis
* [ ] Skill extraction
* [ ] Resume summary
* [ ] Resume recommendations

## Phase 6 — Resume–Job Matching

* [ ] Skill matching
* [ ] Match score
* [ ] Missing skill detection
* [ ] Recommendations

## Phase 7 — AI Interview Engine

* [ ] Interview configuration
* [ ] Dynamic question generation
* [ ] Resume-based questions
* [ ] Job-based questions
* [ ] Adaptive follow-up questions

## Phase 8 — Voice Interview

* [ ] Microphone integration
* [ ] Speech-to-Text
* [ ] Text-to-Speech
* [ ] Voice conversation

## Phase 9 — Real-Time AI Avatar

* [ ] Integrate avatar provider
* [ ] Real-time avatar connection
* [ ] Talking avatar
* [ ] Lip synchronization
* [ ] Avatar interaction

## Phase 10 — Interview Evaluation

* [ ] Answer evaluation
* [ ] Technical score
* [ ] Communication score
* [ ] Relevance score
* [ ] AI feedback
* [ ] Final report

## Phase 11 — Analytics

* [ ] Interview history
* [ ] Performance tracking
* [ ] Score charts
* [ ] Improvement recommendations

## Phase 12 — Advanced AI

* [ ] Embeddings
* [ ] Vector database
* [ ] RAG
* [ ] Resume knowledge base
* [ ] Advanced personalized interviews

## Phase 13 — Deployment

* [ ] Dockerize backend
* [ ] Dockerize frontend
* [ ] Production database
* [ ] Environment configuration
* [ ] Deploy application
* [ ] Configure HTTPS
* [ ] CI/CD

---

# 🔮 Future Enhancements

Possible future features:

* Multiple AI interviewer personalities
* Different accents and languages
* Company-specific interview modes
* Coding interview mode
* Live coding editor
* System design interview mode
* HR interview mode
* Behavioral interview mode
* Interview difficulty adaptation
* Voice confidence analysis
* Filler-word detection
* Interview recording
* AI-generated learning roadmap
* Mobile application
* RAG-powered personalized interview preparation

---

# 📌 Important Design Principle

InterVue AI will be developed as a **modular system**.

The AI provider, Speech-to-Text provider, Text-to-Speech provider, and Avatar provider should be abstracted behind service interfaces.

```text
Interview Service
       ↓
AI Interview Service
       ↓
AI Provider Interface
       │
       ├── LLM Provider
       ├── STT Provider
       ├── TTS Provider
       └── Avatar Provider
```

This allows individual AI providers to be replaced without changing the core application.

---

# 🎯 Final Objective

The final goal of InterVue AI is to create a realistic virtual interview experience where a user can:

```text
Upload Resume
      ↓
Add Job Description
      ↓
Configure Interview
      ↓
Start Interview
      ↓
🤖 AI Avatar Appears
      ↓
AI Speaks
      ↓
🎤 User Answers
      ↓
AI Understands Answer
      ↓
AI Evaluates Answer
      ↓
AI Generates Next Question
      ↓
Repeat
      ↓
Interview Ends
      ↓
📊 Detailed AI Report
      ↓
📈 Performance Tracking
```
InterVue AI aims to combine **Full-Stack Development, Artificial Intelligence, Real-Time Communication, Voice Technology, and Digital Human/Avatar Technology** into a single practical application.
## 📁 Project Structure

<pre>
InterVue-AI/
│
├── README.md
├── .gitignore
├── .env.example
├── docker-compose.yml
│
├── docs/
│   ├── system-design.md
│   ├── architecture.md
│   ├── database-design.md
│   ├── api-documentation.md
│   ├── ai-architecture.md
│   ├── websocket-flow.md
│   └── deployment.md
│
├── backend/
│   ├── pom.xml
│   │
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/intervueai/
│       │   │       ├── InterVueAiApplication.java
│       │   │       │
│       │   │       ├── config/
│       │   │       │   ├── SecurityConfig.java
│       │   │       │   ├── WebSocketConfig.java
│       │   │       │   ├── CorsConfig.java
│       │   │       │   ├── OpenApiConfig.java
│       │   │       │   └── ObjectMapperConfig.java
│       │   │       │
│       │   │       ├── security/
│       │   │       │   ├── JwtFilter.java
│       │   │       │   ├── JwtUtil.java
│       │   │       │   ├── CustomUserDetailsService.java
│       │   │       │   └── SecurityUser.java
│       │   │       │
│       │   │       ├── common/
│       │   │       │   ├── ApiResponse.java
│       │   │       │   ├── PageResponse.java
│       │   │       │   ├── Constants.java
│       │   │       │   ├── Enums.java
│       │   │       │   └── Utils.java
│       │   │       │
│       │   │       ├── exception/
│       │   │       │   ├── GlobalExceptionHandler.java
│       │   │       │   ├── ResourceNotFoundException.java
│       │   │       │   ├── BadRequestException.java
│       │   │       │   ├── UnauthorizedException.java
│       │   │       │   └── FileStorageException.java
│       │   │       │
│       │   │       ├── auth/
│       │   │       │   ├── controller/
│       │   │       │   │   └── AuthController.java
│       │   │       │   ├── service/
│       │   │       │   │   ├── AuthService.java
│       │   │       │   │   └── AuthServiceImpl.java
│       │   │       │   ├── dto/
│       │   │       │   │   ├── LoginRequest.java
│       │   │       │   │   ├── RegisterRequest.java
│       │   │       │   │   └── AuthResponse.java
│       │   │       │   └── mapper/
│       │   │       │       └── AuthMapper.java
│       │   │       │
│       │   │       ├── user/
│       │   │       │   ├── controller/
│       │   │       │   │   └── UserController.java
│       │   │       │   ├── service/
│       │   │       │   │   ├── UserService.java
│       │   │       │   │   └── UserServiceImpl.java
│       │   │       │   ├── repository/
│       │   │       │   │   └── UserRepository.java
│       │   │       │   ├── entity/
│       │   │       │   │   └── User.java
│       │   │       │   └── dto/
│       │   │       │       ├── UserResponse.java
│       │   │       │       └── UpdateUserRequest.java
│       │   │       │
│       │   │       ├── resume/
│       │   │       │   ├── controller/
│       │   │       │   │   └── ResumeController.java
│       │   │       │   ├── service/
│       │   │       │   │   ├── ResumeService.java
│       │   │       │   │   └── ResumeServiceImpl.java
│       │   │       │   ├── repository/
│       │   │       │   │   └── ResumeRepository.java
│       │   │       │   ├── entity/
│       │   │       │   │   └── Resume.java
│       │   │       │   ├── dto/
│       │   │       │   │   ├── ResumeResponse.java
│       │   │       │   │   └── ResumeUploadResponse.java
│       │   │       │   └── parser/
│       │   │       │       ├── ResumeParser.java
│       │   │       │       └── PdfResumeParser.java
│       │   │       │
│       │   │       ├── job/
│       │   │       │   ├── controller/
│       │   │       │   │   └── JobController.java
│       │   │       │   ├── service/
│       │   │       │   │   ├── JobService.java
│       │   │       │   │   └── JobServiceImpl.java
│       │   │       │   ├── repository/
│       │   │       │   │   └── JobRepository.java
│       │   │       │   ├── entity/
│       │   │       │   │   └── Job.java
│       │   │       │   └── dto/
│       │   │       │       ├── CreateJobRequest.java
│       │   │       │       └── JobResponse.java
│       │   │       │
│       │   │       ├── matching/
│       │   │       │   ├── controller/
│       │   │       │   │   └── MatchingController.java
│       │   │       │   ├── service/
│       │   │       │   │   ├── MatchingService.java
│       │   │       │   │   └── MatchingServiceImpl.java
│       │   │       │   ├── dto/
│       │   │       │   │   ├── MatchRequest.java
│       │   │       │   │   └── MatchResponse.java
│       │   │       │   └── model/
│       │   │       │       └── SkillMatch.java
│       │   │       │
│       │   │       ├── interview/
│       │   │       │   ├── controller/
│       │   │       │   │   └── InterviewController.java
│       │   │       │   ├── service/
│       │   │       │   │   ├── InterviewService.java
│       │   │       │   │   └── InterviewServiceImpl.java
│       │   │       │   ├── repository/
│       │   │       │   │   └── InterviewRepository.java
│       │   │       │   ├── entity/
│       │   │       │   │   └── Interview.java
│       │   │       │   ├── dto/
│       │   │       │   │   ├── CreateInterviewRequest.java
│       │   │       │   │   ├── InterviewResponse.java
│       │   │       │   │   └── InterviewConfig.java
│       │   │       │   └── websocket/
│       │   │       │       ├── InterviewWebSocketHandler.java
│       │   │       │       ├── InterviewSession.java
│       │   │       │       └── WebSocketMessage.java
│       │   │       │
│       │   │       ├── question/
│       │   │       │   ├── service/
│       │   │       │   │   ├── QuestionService.java
│       │   │       │   │   └── QuestionServiceImpl.java
│       │   │       │   ├── repository/
│       │   │       │   │   └── QuestionRepository.java
│       │   │       │   ├── entity/
│       │   │       │   │   └── Question.java
│       │   │       │   └── dto/
│       │   │       │       └── QuestionResponse.java
│       │   │       │
│       │   │       ├── answer/
│       │   │       │   ├── service/
│       │   │       │   │   ├── AnswerService.java
│       │   │       │   │   └── AnswerServiceImpl.java
│       │   │       │   ├── repository/
│       │   │       │   │   └── AnswerRepository.java
│       │   │       │   ├── entity/
│       │   │       │   │   └── Answer.java
│       │   │       │   └── dto/
│       │   │       │       └── AnswerResponse.java
│       │   │       │
│       │   │       ├── evaluation/
│       │   │       │   ├── controller/
│       │   │       │   │   └── EvaluationController.java
│       │   │       │   ├── service/
│       │   │       │   │   ├── EvaluationService.java
│       │   │       │   │   └── EvaluationServiceImpl.java
│       │   │       │   ├── repository/
│       │   │       │   │   └── EvaluationRepository.java
│       │   │       │   ├── entity/
│       │   │       │   │   └── Evaluation.java
│       │   │       │   └── dto/
│       │   │       │       └── EvaluationResponse.java
│       │   │       │
│       │   │       ├── report/
│       │   │       │   ├── controller/
│       │   │       │   │   └── ReportController.java
│       │   │       │   ├── service/
│       │   │       │   │   ├── ReportService.java
│       │   │       │   │   └── ReportServiceImpl.java
│       │   │       │   ├── repository/
│       │   │       │   │   └── ReportRepository.java
│       │   │       │   ├── entity/
│       │   │       │   │   └── InterviewReport.java
│       │   │       │   └── dto/
│       │   │       │       └── ReportResponse.java
│       │   │       │
│       │   │       ├── analytics/
│       │   │       │   ├── controller/
│       │   │       │   │   └── AnalyticsController.java
│       │   │       │   ├── service/
│       │   │       │   │   ├── AnalyticsService.java
│       │   │       │   │   └── AnalyticsServiceImpl.java
│       │   │       │   └── dto/
│       │   │       │       └── PerformanceResponse.java
│       │   │       │
│       │   │       ├── ai/
│       │   │       │   ├── llm/
│       │   │       │   │   ├── LLMProvider.java
│       │   │       │   │   ├── LLMService.java
│       │   │       │   │   └── LLMServiceImpl.java
│       │   │       │   ├── prompt/
│       │   │       │   │   ├── InterviewPromptBuilder.java
│       │   │       │   │   ├── EvaluationPromptBuilder.java
│       │   │       │   │   ├── ResumePromptBuilder.java
│       │   │       │   │   └── JobPromptBuilder.java
│       │   │       │   ├── interview/
│       │   │       │   │   ├── AIInterviewService.java
│       │   │       │   │   └── AIInterviewServiceImpl.java
│       │   │       │   ├── resume/
│       │   │       │   │   ├── AIResumeService.java
│       │   │       │   │   └── AIResumeServiceImpl.java
│       │   │       │   ├── evaluation/
│       │   │       │   │   ├── AIEvaluationService.java
│       │   │       │   │   └── AIEvaluationServiceImpl.java
│       │   │       │   └── matching/
│       │   │       │       ├── AIMatchingService.java
│       │   │       │       └── AIMatchingServiceImpl.java
│       │   │       │
│       │   │       ├── voice/
│       │   │       │   ├── stt/
│       │   │       │   │   ├── STTProvider.java
│       │   │       │   │   ├── STTService.java
│       │   │       │   │   └── STTServiceImpl.java
│       │   │       │   └── tts/
│       │   │       │       ├── TTSProvider.java
│       │   │       │       ├── TTSService.java
│       │   │       │       └── TTSServiceImpl.java
│       │   │       │
│       │   │       ├── avatar/
│       │   │       │   ├── controller/
│       │   │       │   │   └── AvatarController.java
│       │   │       │   ├── service/
│       │   │       │   │   ├── AvatarService.java
│       │   │       │   │   └── AvatarServiceImpl.java
│       │   │       │   ├── provider/
│       │   │       │   │   ├── AvatarProvider.java
│       │   │       │   │   └── AvatarProviderImpl.java
│       │   │       │   └── dto/
│       │   │       │       ├── AvatarSessionRequest.java
│       │   │       │       └── AvatarSessionResponse.java
│       │   │       │
│       │   │       ├── storage/
│       │   │       │   ├── StorageService.java
│       │   │       │   ├── MinioStorageService.java
│       │   │       │   └── StorageConfig.java
│       │   │       │
│       │   │       └── rag/
│       │   │           ├── EmbeddingService.java
│       │   │           ├── VectorStoreService.java
│       │   │           ├── RetrievalService.java
│       │   │           └── RAGService.java
│       │   │
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── application-dev.yml
│       │       ├── application-prod.yml
│       │       └── prompts/
│       │           ├── interview.txt
│       │           ├── evaluation.txt
│       │           ├── resume-analysis.txt
│       │           └── job-analysis.txt
│       │
│       └── test/
│
│
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   │
│   └── src/
│       ├── app/
│       │   ├── App.jsx
│       │   ├── routes.jsx
│       │   └── providers.jsx
│       │
│       ├── assets/
│       │   ├── images/
│       │   ├── icons/
│       │   └── animations/
│       │
│       ├── components/
│       │   ├── common/
│       │   ├── layout/
│       │   ├── navbar/
│       │   ├── sidebar/
│       │   └── modal/
│       │
│       ├── features/
│       │   ├── auth/
│       │   ├── dashboard/
│       │   ├── resume/
│       │   ├── jobs/
│       │   ├── interview/
│       │   ├── avatar/
│       │   ├── voice/
│       │   ├── evaluation/
│       │   ├── reports/
│       │   └── analytics/
│       │
│       ├── services/
│       │   ├── apiClient.js
│       │   ├── tokenService.js
│       │   └── errorHandler.js
│       │
│       ├── context/
│       │   ├── AuthContext.jsx
│       │   └── InterviewContext.jsx
│       │
│       ├── hooks/
│       │   └── useApi.js
│       │
│       ├── utils/
│       │   ├── constants.js
│       │   ├── validators.js
│       │   └── formatters.js
│       │
│       └── styles/
│           ├── global.css
│           └── variables.css
│
│
├── database/
│   ├── schema.sql
│   ├── seed.sql
│   └── migrations/
│       ├── V1__create_users.sql
│       ├── V2__create_resumes.sql
│       ├── V3__create_jobs.sql
│       ├── V4__create_interviews.sql
│       ├── V5__create_questions.sql
│       ├── V6__create_answers.sql
│       ├── V7__create_evaluations.sql
│       └── V8__create_reports.sql
│
│
├── ai/
│   ├── prompts/
│   │   ├── resume-analysis.txt
│   │   ├── job-analysis.txt
│   │   ├── interview-question.txt
│   │   ├── follow-up-question.txt
│   │   └── answer-evaluation.txt
│   │
│   ├── rag/
│   │   ├── documents/
│   │   ├── embeddings/
│   │   ├── retrieval/
│   │   └── vector-store/
│   │
│   └── evaluation/
│       ├── scoring-rules.md
│       └── evaluation-criteria.md
│
│
├── storage/
│   ├── resumes/
│   ├── audio/
│   ├── reports/
│   └── avatars/
│
│
├── docker/
│   ├── backend.Dockerfile
│   ├── frontend.Dockerfile
│   └── nginx.conf
│
│
└── .github/
    └── workflows/
        ├── ci.yml
        └── cd.yml
</pre>
        ├── ci.yml
        └── cd.yml
