import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { interviewApi } from '../api/interview';
import { AvatarVisualizer } from '../components/AvatarVisualizer';
import { AudioRecorder } from '../components/AudioRecorder';
import { Sparkles, CheckCircle2, Award, ArrowRight, ShieldAlert } from 'lucide-react';

export const InterviewRoom = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [interview, setInterview] = useState(null);
  const [currentStep, setCurrentStep] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isProcessing, setIsProcessing] = useState(false);
  const [avatarStatus, setAvatarStatus] = useState('idle'); // 'idle' | 'speaking' | 'listening' | 'thinking'
  const [error, setError] = useState(null);
  const [interviewHistory, setInterviewHistory] = useState([]);

  // Load interview details and current/first question
  useEffect(() => {
    const initInterview = async () => {
      try {
        setLoading(true);
        setError(null);

        const interviewData = await interviewApi.getMyInterview(id);
        setInterview(interviewData);

        // Fetch existing questions
        const existingQuestions = await interviewApi.getInterviewQuestions(id);
        setInterviewHistory(existingQuestions);

        if (interviewData.status === 'COMPLETED') {
          setCurrentStep({
            interviewId: interviewData.id,
            questionNumber: existingQuestions.length,
            transitionMessage: 'This interview is completed.',
            question: '',
            status: 'COMPLETED',
            completed: true,
          });
          setLoading(false);
          return;
        }

        // If no questions exist yet, generate Q1
        if (existingQuestions.length === 0) {
          const firstStep = await interviewApi.getFirstQuestion(id);
          setCurrentStep(firstStep);
          setAvatarStatus('speaking');
        } else {
          // Check if latest question was answered
          const latest = existingQuestions[existingQuestions.length - 1];
          if (!latest.candidateAnswer) {
            // Pending answer for this question
            setCurrentStep({
              interviewId: interviewData.id,
              questionNumber: latest.questionNumber,
              transitionMessage: latest.questionNumber === 1 ? "Hello! Let's begin your technical interview." : "Let's continue.",
              question: latest.question,
              status: interviewData.status,
              completed: false,
            });
            setAvatarStatus('speaking');
          } else if (latest.questionNumber >= 10) {
            setCurrentStep({
              interviewId: interviewData.id,
              questionNumber: 10,
              transitionMessage: 'Thank you. That completes your interview.',
              question: '',
              status: 'COMPLETED',
              completed: true,
            });
          }
        }
      } catch (err) {
        console.error('Failed to initialize interview:', err);
        setError(err.response?.data?.message || 'Failed to connect to interview session.');
      } finally {
        setLoading(false);
      }
    };

    initInterview();
  }, [id]);

  // Handle Candidate Answer Submission
  const handleAnswerSubmit = async (candidateAnswer) => {
    if (!candidateAnswer.trim() || isProcessing) return;

    try {
      setIsProcessing(true);
      setAvatarStatus('thinking');
      setError(null);

      // Submit answer and receive structured next question DTO
      const nextStep = await interviewApi.submitAnswerAndGetNextQuestion(id, candidateAnswer);
      setCurrentStep(nextStep);

      // Refresh interview history
      const updatedHistory = await interviewApi.getInterviewQuestions(id);
      setInterviewHistory(updatedHistory);

      if (nextStep.completed) {
        setAvatarStatus('idle');
      } else {
        setAvatarStatus('speaking');
      }
    } catch (err) {
      console.error('Failed to submit answer:', err);
      setError(err.response?.data?.message || 'Failed to submit answer. Please try again.');
      setAvatarStatus('idle');
    } finally {
      setIsProcessing(false);
    }
  };

  const handleSpeechEnd = () => {
    if (!currentStep?.completed) {
      setAvatarStatus('listening');
    } else {
      setAvatarStatus('idle');
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0', color: 'var(--text-muted)' }}>
        Connecting to AI Interview Room...
      </div>
    );
  }

  const questionNumber = currentStep?.questionNumber || 1;
  const progressPercent = Math.min(100, Math.round((questionNumber / 10) * 100));

  return (
    <div style={{ maxWidth: 1100, margin: '0 auto' }}>
      {/* Top Header Bar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <div>
          <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
            INTERVIEW SESSION #{id}
          </span>
          <h3>Live AI Technical Interview</h3>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          {currentStep?.completed ? (
            <span className="badge badge-strong-hire">Completed</span>
          ) : (
            <span className="badge badge-hire">
              Question {questionNumber} of 10
            </span>
          )}
        </div>
      </div>

      {/* Progress Bar */}
      <div className="progress-container">
        <div className="progress-fill" style={{ width: `${progressPercent}%` }}></div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {/* Main Interview Grid */}
      <div className="interview-grid" style={{ marginTop: 24 }}>
        {/* Left Column: Talking AI Avatar */}
        <div>
          <AvatarVisualizer
            transitionText={currentStep?.transitionMessage || ''}
            questionText={currentStep?.question || ''}
            status={avatarStatus}
            onSpeechEnd={handleSpeechEnd}
            autoSpeak={true}
          />

          {/* Transition Bubble */}
          {currentStep?.transitionMessage && (
            <div
              style={{
                marginTop: 20,
                padding: '12px 18px',
                borderRadius: 'var(--radius-sm)',
                background: 'rgba(99, 102, 241, 0.1)',
                border: '1px solid rgba(99, 102, 241, 0.25)',
                fontSize: '0.9rem',
                color: '#c7d2fe',
                display: 'flex',
                alignItems: 'center',
                gap: 8,
              }}
            >
              <Sparkles size={16} color="var(--accent-cyan)" />
              <span>{currentStep.transitionMessage}</span>
            </div>
          )}
        </div>

        {/* Right Column: Question Card & Microphone Controls */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          {/* Question Card */}
          <div className="glass-card" style={{ borderColor: 'var(--border-active)', minHeight: 180 }}>
            <div style={{ fontSize: '0.8rem', color: 'var(--accent-cyan)', fontWeight: 600, textTransform: 'uppercase', marginBottom: 8 }}>
              {currentStep?.completed ? 'Interview Finished' : `Question #${questionNumber}`}
            </div>

            {currentStep?.completed ? (
              <div>
                <h4 style={{ color: 'var(--accent-emerald)', display: 'flex', alignItems: 'center', gap: 8 }}>
                  <CheckCircle2 size={24} />
                  Interview Completed!
                </h4>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem', marginTop: 10 }}>
                  You have successfully completed all 10 technical questions. The AI evaluator is ready to generate your comprehensive performance score and feedback report.
                </p>
                <button
                  className="btn btn-primary btn-lg"
                  style={{ marginTop: 24, width: '100%' }}
                  onClick={() => navigate(`/evaluation/${id}`)}
                >
                  <Award size={20} />
                  Generate & View AI Evaluation Report
                </button>
              </div>
            ) : (
              <div>
                <h3 style={{ fontSize: '1.35rem', lineHeight: '1.4', fontWeight: 600 }}>
                  {currentStep?.question || 'Preparing your next question...'}
                </h3>
              </div>
            )}
          </div>

          {/* Candidate Voice / Text Input Box */}
          {!currentStep?.completed && (
            <div className="glass-card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                <label className="form-label" style={{ marginBottom: 0 }}>
                  Your Voice Answer
                </label>
                <span style={{ fontSize: '0.8rem', color: 'var(--text-dim)' }}>
                  Speak with your mic or type
                </span>
              </div>

              <AudioRecorder
                onAnswerSubmit={handleAnswerSubmit}
                disabled={isProcessing}
                isListening={avatarStatus === 'listening'}
                placeholder="Microphone will open automatically when interviewer stops speaking, or you can type here..."
              />

            </div>
          )}

          {/* Recent Q&A Transcript */}
          {interviewHistory.length > 0 && (
            <div className="glass-card" style={{ maxHeight: 220, overflowY: 'auto' }}>
              <div style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: 12 }}>
                Conversation Transcript ({interviewHistory.length} answered)
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                {interviewHistory.map((q) => (
                  <div key={q.id} style={{ fontSize: '0.85rem', borderLeft: '2px solid var(--accent-primary)', paddingLeft: 10 }}>
                    <div style={{ fontWeight: 600 }}>Q{q.questionNumber}: {q.question}</div>
                    {q.candidateAnswer && (
                      <div style={{ color: 'var(--text-muted)', marginTop: 2 }}>
                        A: {q.candidateAnswer}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
