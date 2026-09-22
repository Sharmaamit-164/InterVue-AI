import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { evaluationApi } from '../api/evaluation';
import { Award, CheckCircle, AlertTriangle, ArrowLeft, RotateCcw, Sparkles } from 'lucide-react';

export const EvaluationResult = () => {
  const { id } = useParams();
  const [evaluation, setEvaluation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchOrGenerateEvaluation = async () => {
      try {
        setLoading(true);
        setError(null);

        // First try to fetch existing evaluation
        try {
          const data = await evaluationApi.getEvaluation(id);
          setEvaluation(data);
          setLoading(false);
          return;
        } catch (fetchErr) {
          // If not found, generate it!
          setGenerating(true);
          const generatedData = await evaluationApi.generateEvaluation(id);
          setEvaluation(generatedData);
        }
      } catch (err) {
        console.error('Failed to load evaluation:', err);
        setError(err.response?.data?.message || 'Failed to generate evaluation. Ensure the interview is completed with answered questions.');
      } finally {
        setLoading(false);
        setGenerating(false);
      }
    };

    fetchOrGenerateEvaluation();
  }, [id]);

  const getScoreColorClass = (score) => {
    if (score >= 8) return 'score-high';
    if (score >= 5) return 'score-med';
    return 'score-low';
  };

  const getRecommendationBadge = (recommendation) => {
    switch (recommendation?.toUpperCase()) {
      case 'STRONG_HIRE':
        return <span className="badge badge-strong-hire" style={{ fontSize: '1rem', padding: '6px 16px' }}>STRONG HIRE</span>;
      case 'HIRE':
        return <span className="badge badge-hire" style={{ fontSize: '1rem', padding: '6px 16px' }}>HIRE</span>;
      case 'CONSIDER':
        return <span className="badge badge-consider" style={{ fontSize: '1rem', padding: '6px 16px' }}>CONSIDER</span>;
      case 'NO_HIRE':
        return <span className="badge badge-no-hire" style={{ fontSize: '1rem', padding: '6px 16px' }}>NO HIRE</span>;
      default:
        return <span className="badge badge-consider">{recommendation}</span>;
    }
  };

  if (loading || generating) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        <div style={{ display: 'inline-flex', padding: 18, borderRadius: '50%', background: 'rgba(99, 102, 241, 0.15)', color: 'var(--accent-primary)', marginBottom: 16 }}>
          <Sparkles size={40} className="sound-bounce" />
        </div>
        <h3>Analyzing Interview Performance...</h3>
        <p style={{ color: 'var(--text-muted)', maxWidth: 460, margin: '8px auto 0' }}>
          Our AI evaluation engine is reviewing your technical answers, scoring communication and problem solving, and formulating tailored feedback.
        </p>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ maxWidth: 600, margin: '60px auto', textAlign: 'center' }}>
        <div className="alert alert-error">{error}</div>
        <Link to={`/interview/${id}`} className="btn btn-secondary" style={{ marginTop: 16 }}>
          <ArrowLeft size={16} /> Return to Interview
        </Link>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 980, margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 28, flexWrap: 'wrap', gap: 14 }}>
        <div>
          <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
            INTERVIEW EVALUATION REPORT #{id}
          </span>
          <h2>Technical Interview Assessment</h2>
        </div>

        <div style={{ display: 'flex', gap: 12 }}>
          <Link to="/" className="btn btn-secondary">
            <ArrowLeft size={16} /> Back to Dashboard
          </Link>
          <Link to="/interviews/new" className="btn btn-primary">
            <RotateCcw size={16} /> Take Another Interview
          </Link>
        </div>
      </div>

      {/* Main Scores Row */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 20, marginBottom: 32 }}>
        <div className="glass-card" style={{ textAlign: 'center' }}>
          <div className={`score-circle ${getScoreColorClass(evaluation.overallScore)}`}>
            {evaluation.overallScore}
            <span style={{ fontSize: '0.65rem', fontWeight: 500, color: 'var(--text-muted)' }}>/ 10</span>
          </div>
          <div style={{ fontWeight: 700 }}>Overall Score</div>
          <div style={{ marginTop: 8 }}>{getRecommendationBadge(evaluation.recommendation)}</div>
        </div>

        <div className="glass-card" style={{ textAlign: 'center' }}>
          <div className={`score-circle ${getScoreColorClass(evaluation.technicalScore)}`}>
            {evaluation.technicalScore}
            <span style={{ fontSize: '0.65rem', fontWeight: 500, color: 'var(--text-muted)' }}>/ 10</span>
          </div>
          <div style={{ fontWeight: 700 }}>Technical Depth</div>
          <div style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginTop: 4 }}>Accuracy & Concept Knowledge</div>
        </div>

        <div className="glass-card" style={{ textAlign: 'center' }}>
          <div className={`score-circle ${getScoreColorClass(evaluation.communicationScore)}`}>
            {evaluation.communicationScore}
            <span style={{ fontSize: '0.65rem', fontWeight: 500, color: 'var(--text-muted)' }}>/ 10</span>
          </div>
          <div style={{ fontWeight: 700 }}>Communication</div>
          <div style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginTop: 4 }}>Clarity & Articulation</div>
        </div>

        <div className="glass-card" style={{ textAlign: 'center' }}>
          <div className={`score-circle ${getScoreColorClass(evaluation.problemSolvingScore)}`}>
            {evaluation.problemSolvingScore}
            <span style={{ fontSize: '0.65rem', fontWeight: 500, color: 'var(--text-muted)' }}>/ 10</span>
          </div>
          <div style={{ fontWeight: 700 }}>Problem Solving</div>
          <div style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginTop: 4 }}>Reasoning & Approach</div>
        </div>
      </div>

      {/* Detailed Analysis Breakdown */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: 24 }}>
        {/* Strengths */}
        <div className="glass-card" style={{ borderColor: 'rgba(16, 185, 129, 0.3)' }}>
          <h3 style={{ color: 'var(--accent-emerald)', display: 'flex', alignItems: 'center', gap: 10, marginBottom: 16 }}>
            <CheckCircle size={22} /> Key Strengths
          </h3>
          <div style={{ whiteSpace: 'pre-wrap', lineHeight: '1.7', color: '#e2e8f0' }}>
            {evaluation.strengths || 'Strong conceptual baseline.'}
          </div>
        </div>

        {/* Weaknesses / Growth Areas */}
        <div className="glass-card" style={{ borderColor: 'rgba(245, 158, 11, 0.3)' }}>
          <h3 style={{ color: 'var(--accent-amber)', display: 'flex', alignItems: 'center', gap: 10, marginBottom: 16 }}>
            <AlertTriangle size={22} /> Areas for Improvement
          </h3>
          <div style={{ whiteSpace: 'pre-wrap', lineHeight: '1.7', color: '#e2e8f0' }}>
            {evaluation.weaknesses || 'Opportunity to deepen technical explanations.'}
          </div>
        </div>

        {/* Actionable Feedback */}
        <div className="glass-card" style={{ borderColor: 'rgba(99, 102, 241, 0.3)' }}>
          <h3 style={{ color: '#a5b4fc', display: 'flex', alignItems: 'center', gap: 10, marginBottom: 16 }}>
            <Award size={22} /> Detailed Interviewer Feedback & Action Items
          </h3>
          <div style={{ whiteSpace: 'pre-wrap', lineHeight: '1.7', color: '#cbd5e1' }}>
            {evaluation.feedback || 'Great job completing your interview practice!'}
          </div>
        </div>
      </div>
    </div>
  );
};
