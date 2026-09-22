import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { resumeApi } from '../api/resume';
import { jobApi } from '../api/job';
import { matchingApi } from '../api/matching';
import { interviewApi } from '../api/interview';
import { Play, Sparkles, AlertCircle, CheckCircle2, ArrowRight } from 'lucide-react';

export const InterviewSetup = () => {
  const navigate = useNavigate();

  const [resumes, setResumes] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [selectedResumeId, setSelectedResumeId] = useState('');
  const [selectedJobId, setSelectedJobId] = useState('');
  const [matchingResult, setMatchingResult] = useState(null);
  const [matchingLoading, setMatchingLoading] = useState(false);
  const [starting, setStarting] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [resumesData, jobsData] = await Promise.all([
          resumeApi.getMyResumes(),
          jobApi.getMyJobs(),
        ]);
        setResumes(resumesData);
        setJobs(jobsData);
        if (resumesData.length > 0) setSelectedResumeId(resumesData[0].id.toString());
        if (jobsData.length > 0) setSelectedJobId(jobsData[0].id.toString());
      } catch (err) {
        console.error('Failed to load setup options:', err);
      }
    };

    fetchData();
  }, []);

  // Compute match score when resume or job changes
  useEffect(() => {
    if (selectedResumeId && selectedJobId) {
      const checkMatch = async () => {
        try {
          setMatchingLoading(true);
          const result = await matchingApi.matchResumeWithJob(selectedResumeId, selectedJobId);
          setMatchingResult(result);
        } catch (e) {
          console.warn('Match calculation note:', e);
          setMatchingResult(null);
        } finally {
          setMatchingLoading(false);
        }
      };
      checkMatch();
    }
  }, [selectedResumeId, selectedJobId]);

  const handleStartInterview = async () => {
    if (!selectedResumeId || !selectedJobId) {
      setError('Please select both a resume and a target job to proceed.');
      return;
    }

    try {
      setStarting(true);
      setError(null);
      const newInterview = await interviewApi.createInterview(
        parseInt(selectedResumeId, 10),
        parseInt(selectedJobId, 10)
      );
      navigate(`/interview/${newInterview.id}`);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to initialize interview.');
      setStarting(false);
    }
  };

  return (
    <div style={{ maxWidth: 840, margin: '0 auto' }}>
      <div style={{ textAlign: 'center', marginBottom: 36 }}>
        <h2>Configure Your AI Interview</h2>
        <p style={{ color: 'var(--text-muted)', marginTop: 4 }}>
          Pair your resume with a job profile for targeted, conversational AI technical questions.
        </p>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="glass-card" style={{ padding: 36 }}>
        {/* Step 1: Select Resume */}
        <div className="form-group">
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
            <label className="form-label" style={{ marginBottom: 0 }}>1. Select Your Resume</label>
            <Link to="/resumes" style={{ fontSize: '0.85rem' }}>+ Upload new resume</Link>
          </div>

          {resumes.length === 0 ? (
            <div className="alert alert-info">
              You haven't uploaded any resumes yet. <Link to="/resumes">Upload a resume</Link> first.
            </div>
          ) : (
            <select
              className="form-select"
              value={selectedResumeId}
              onChange={(e) => setSelectedResumeId(e.target.value)}
            >
              {resumes.map((r) => (
                <option key={r.id} value={r.id}>
                  {r.fileName} (Uploaded: {new Date(r.uploadedAt).toLocaleDateString()})
                </option>
              ))}
            </select>
          )}
        </div>

        {/* Step 2: Select Job */}
        <div className="form-group" style={{ marginTop: 24 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
            <label className="form-label" style={{ marginBottom: 0 }}>2. Select Target Job</label>
            <Link to="/jobs" style={{ fontSize: '0.85rem' }}>+ Add new job</Link>
          </div>

          {jobs.length === 0 ? (
            <div className="alert alert-info">
              You haven't added any job descriptions yet. <Link to="/jobs">Add a target job</Link> first.
            </div>
          ) : (
            <select
              className="form-select"
              value={selectedJobId}
              onChange={(e) => setSelectedJobId(e.target.value)}
            >
              {jobs.map((j) => (
                <option key={j.id} value={j.id}>
                  {j.title} at {j.companyName}
                </option>
              ))}
            </select>
          )}
        </div>

        {/* Live Resume-to-Job Matching Feedback */}
        {matchingResult && (
          <div
            style={{
              margin: '28px 0',
              padding: 20,
              background: 'rgba(15, 23, 42, 0.6)',
              borderRadius: 'var(--radius-sm)',
              border: '1px solid var(--border-active)',
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 14 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontWeight: 600, color: 'var(--accent-cyan)' }}>
                <Sparkles size={18} /> Resume-to-Job Fit
              </div>
              <span className="badge badge-hire" style={{ fontSize: '0.85rem' }}>
                {matchingResult.matchScore}% Match
              </span>
            </div>

            {matchingResult.matchedSkills?.length > 0 && (
              <div style={{ marginBottom: 10 }}>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: 4 }}>
                  Matched Skills in Resume:
                </div>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
                  {matchingResult.matchedSkills.map((s, i) => (
                    <span key={i} className="badge badge-strong-hire">{s}</span>
                  ))}
                </div>
              </div>
            )}

            {matchingResult.missingSkills?.length > 0 && (
              <div>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: 4 }}>
                  Skills to prepare for in this interview:
                </div>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
                  {matchingResult.missingSkills.map((s, i) => (
                    <span key={i} className="badge badge-consider">{s}</span>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {/* Start Button */}
        <button
          className="btn btn-primary btn-lg"
          style={{ width: '100%', marginTop: 24 }}
          onClick={handleStartInterview}
          disabled={starting || !selectedResumeId || !selectedJobId}
        >
          <Play size={20} />
          {starting ? 'Starting Interview Session...' : 'Enter Interview Room'}
        </button>
      </div>
    </div>
  );
};
