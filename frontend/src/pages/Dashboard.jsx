import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { interviewApi } from '../api/interview';
import { resumeApi } from '../api/resume';
import { jobApi } from '../api/job';
import { useAuth } from '../context/AuthContext';
import { Play, FileText, Briefcase, Award, Clock, ArrowRight, PlusCircle, CheckCircle } from 'lucide-react';

export const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [resumes, setResumes] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [interviews, setInterviews] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadDashboardData = async () => {
      try {
        const [resumesData, jobsData, interviewsData] = await Promise.all([
          resumeApi.getMyResumes(),
          jobApi.getMyJobs(),
          interviewApi.getMyInterviews(),
        ]);
        setResumes(resumesData);
        setJobs(jobsData);
        setInterviews(interviewsData);
      } catch (err) {
        console.error('Failed to load dashboard data:', err);
      } finally {
        setLoading(false);
      }
    };

    loadDashboardData();
  }, []);

  const completedCount = interviews.filter((i) => i.status === 'COMPLETED').length;
  const inProgressCount = interviews.filter((i) => i.status === 'IN_PROGRESS' || i.status === 'CREATED').length;

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0', color: 'var(--text-muted)' }}>
        Loading your interview dashboard...
      </div>
    );
  }

  return (
    <div>
      {/* Header Banner */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 36, flexWrap: 'wrap', gap: 16 }}>
        <div>
          <h1>Welcome, {user?.email?.split('@')[0]} 👋</h1>
          <p style={{ color: 'var(--text-muted)', marginTop: 4 }}>
            Prepare, practice, and master your technical interviews with talking AI interviewers.
          </p>
        </div>
        <Link to="/interviews/new" className="btn btn-primary btn-lg">
          <Play size={20} />
          Start New Interview
        </Link>
      </div>

      {/* Metrics Row */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 20, marginBottom: 36 }}>
        <div className="glass-card" style={{ display: 'flex', alignItems: 'center', gap: 18 }}>
          <div style={{ padding: 14, borderRadius: 'var(--radius-sm)', background: 'rgba(99, 102, 241, 0.15)', color: 'var(--accent-primary)' }}>
            <FileText size={28} />
          </div>
          <div>
            <div style={{ fontSize: '1.75rem', fontWeight: 800 }}>{resumes.length}</div>
            <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Resumes Uploaded</div>
          </div>
        </div>

        <div className="glass-card" style={{ display: 'flex', alignItems: 'center', gap: 18 }}>
          <div style={{ padding: 14, borderRadius: 'var(--radius-sm)', background: 'rgba(6, 182, 212, 0.15)', color: 'var(--accent-cyan)' }}>
            <Briefcase size={28} />
          </div>
          <div>
            <div style={{ fontSize: '1.75rem', fontWeight: 800 }}>{jobs.length}</div>
            <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Target Jobs Added</div>
          </div>
        </div>

        <div className="glass-card" style={{ display: 'flex', alignItems: 'center', gap: 18 }}>
          <div style={{ padding: 14, borderRadius: 'var(--radius-sm)', background: 'rgba(16, 185, 129, 0.15)', color: 'var(--accent-emerald)' }}>
            <Award size={28} />
          </div>
          <div>
            <div style={{ fontSize: '1.75rem', fontWeight: 800 }}>{completedCount}</div>
            <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Interviews Completed</div>
          </div>
        </div>

        <div className="glass-card" style={{ display: 'flex', alignItems: 'center', gap: 18 }}>
          <div style={{ padding: 14, borderRadius: 'var(--radius-sm)', background: 'rgba(245, 158, 11, 0.15)', color: 'var(--accent-amber)' }}>
            <Clock size={28} />
          </div>
          <div>
            <div style={{ fontSize: '1.75rem', fontWeight: 800 }}>{inProgressCount}</div>
            <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>In Progress / Ready</div>
          </div>
        </div>
      </div>

      {/* Recent Interviews Table */}
      <div className="glass-card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
          <h3>Recent Interviews</h3>
          {interviews.length > 0 && (
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              Total: {interviews.length} sessions
            </span>
          )}
        </div>

        {interviews.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '48px 16px' }}>
            <div style={{ marginBottom: 12, color: 'var(--text-dim)' }}>
              <Award size={48} />
            </div>
            <h4>No interviews taken yet</h4>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', maxWidth: 400, margin: '8px auto 20px' }}>
              Upload your resume and select a target job to start your first voice-based AI technical interview!
            </p>
            <Link to="/interviews/new" className="btn btn-primary">
              <PlusCircle size={16} />
              Setup First Interview
            </Link>
          </div>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid var(--border-subtle)', color: 'var(--text-muted)', fontSize: '0.85rem' }}>
                  <th style={{ padding: '12px 16px' }}>INTERVIEW ID</th>
                  <th style={{ padding: '12px 16px' }}>STATUS</th>
                  <th style={{ padding: '12px 16px' }}>STARTED AT</th>
                  <th style={{ padding: '12px 16px' }}>ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {interviews.map((interview) => (
                  <tr key={interview.id} style={{ borderBottom: '1px solid var(--border-subtle)' }}>
                    <td style={{ padding: '16px' }}>
                      <span style={{ fontWeight: 600 }}>#{interview.id}</span>
                    </td>
                    <td style={{ padding: '16px' }}>
                      {interview.status === 'COMPLETED' ? (
                        <span className="badge badge-strong-hire">Completed</span>
                      ) : (
                        <span className="badge badge-progress">In Progress</span>
                      )}
                    </td>
                    <td style={{ padding: '16px', color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                      {interview.startedAt ? new Date(interview.startedAt).toLocaleDateString() : 'N/A'}
                    </td>
                    <td style={{ padding: '16px' }}>
                      {interview.status === 'COMPLETED' ? (
                        <Link to={`/evaluation/${interview.id}`} className="btn btn-secondary btn-sm">
                          <CheckCircle size={14} color="var(--accent-emerald)" />
                          View Evaluation
                        </Link>
                      ) : (
                        <Link to={`/interview/${interview.id}`} className="btn btn-primary btn-sm">
                          <Play size={14} />
                          Continue Interview
                        </Link>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
