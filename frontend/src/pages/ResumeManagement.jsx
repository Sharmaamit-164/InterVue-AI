import React, { useState, useEffect } from 'react';
import { resumeApi } from '../api/resume';
import { Upload, FileText, Trash2, CheckCircle2, Eye, EyeOff } from 'lucide-react';

export const ResumeManagement = () => {
  const [resumes, setResumes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState(null);
  const [expandedResumeId, setExpandedResumeId] = useState(null);

  const fetchResumes = async () => {
    try {
      setLoading(true);
      const data = await resumeApi.getMyResumes();
      setResumes(data);
    } catch (err) {
      console.error('Failed to fetch resumes:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchResumes();
  }, []);

  const handleFileUpload = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (file.type !== 'application/pdf') {
      setMessage({ type: 'error', text: 'Only PDF resume files are accepted.' });
      return;
    }

    try {
      setUploading(true);
      setMessage(null);
      const res = await resumeApi.uploadResume(file);
      setMessage({ type: 'success', text: `Resume "${file.name}" parsed & uploaded successfully!` });
      fetchResumes();
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Failed to upload and parse resume.' });
    } finally {
      setUploading(false);
      e.target.value = '';
    }
  };

  const handleDelete = async (resumeId) => {
    if (!window.confirm('Are you sure you want to delete this resume?')) return;
    try {
      await resumeApi.deleteMyResume(resumeId);
      setResumes(resumes.filter((r) => r.id !== resumeId));
      setMessage({ type: 'success', text: 'Resume deleted successfully.' });
    } catch (err) {
      setMessage({ type: 'error', text: 'Failed to delete resume.' });
    }
  };

  return (
    <div>
      <div style={{ marginBottom: 32 }}>
        <h2>Resume Management</h2>
        <p style={{ color: 'var(--text-muted)', marginTop: 4 }}>
          Upload PDF resumes for automated text extraction and skill-targeted interview questions.
        </p>
      </div>

      {message && (
        <div className={`alert ${message.type === 'error' ? 'alert-error' : 'alert-success'}`}>
          {message.text}
        </div>
      )}

      {/* Upload Box */}
      <div
        className="glass-card"
        style={{
          border: '2px dashed var(--border-active)',
          textAlign: 'center',
          padding: '40px 24px',
          marginBottom: 36,
          cursor: 'pointer',
        }}
        onClick={() => document.getElementById('resume-file-input').click()}
      >
        <input
          id="resume-file-input"
          type="file"
          accept=".pdf,application/pdf"
          style={{ display: 'none' }}
          onChange={handleFileUpload}
        />
        <div style={{ display: 'inline-flex', padding: 14, borderRadius: '50%', background: 'rgba(99, 102, 241, 0.15)', color: 'var(--accent-primary)', marginBottom: 12 }}>
          <Upload size={32} />
        </div>
        <h4>{uploading ? 'Parsing PDF & Uploading...' : 'Click to Upload Your Resume'}</h4>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: 4 }}>
          Supports PDF format. AI will extract technical skills, experience, and projects.
        </p>
      </div>

      {/* Resumes List */}
      <div className="glass-card">
        <h3 style={{ marginBottom: 20 }}>Your Uploaded Resumes</h3>

        {loading ? (
          <p style={{ color: 'var(--text-muted)' }}>Loading resumes...</p>
        ) : resumes.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '32px 0', color: 'var(--text-dim)' }}>
            <FileText size={40} style={{ marginBottom: 10 }} />
            <p>No resumes uploaded yet. Upload a PDF resume above.</p>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            {resumes.map((resume) => (
              <div
                key={resume.id}
                style={{
                  border: '1px solid var(--border-subtle)',
                  borderRadius: 'var(--radius-sm)',
                  padding: 16,
                  background: 'rgba(15, 23, 42, 0.5)',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 10 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                    <div style={{ padding: 10, borderRadius: 'var(--radius-sm)', background: 'rgba(6, 182, 212, 0.15)', color: 'var(--accent-cyan)' }}>
                      <FileText size={22} />
                    </div>
                    <div>
                      <div style={{ fontWeight: 600 }}>{resume.fileName}</div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                        Uploaded: {new Date(resume.uploadedAt).toLocaleDateString()}
                      </div>
                    </div>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => setExpandedResumeId(expandedResumeId === resume.id ? null : resume.id)}
                    >
                      {expandedResumeId === resume.id ? <EyeOff size={14} /> : <Eye size={14} />}
                      {expandedResumeId === resume.id ? 'Hide Text' : 'View Parsed Text'}
                    </button>
                    <button
                      className="btn btn-danger btn-sm"
                      onClick={() => handleDelete(resume.id)}
                      title="Delete Resume"
                    >
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>

                {/* Parsed Text Preview */}
                {expandedResumeId === resume.id && (
                  <div
                    style={{
                      marginTop: 16,
                      padding: 16,
                      background: 'rgba(0, 0, 0, 0.4)',
                      borderRadius: 'var(--radius-sm)',
                      fontSize: '0.85rem',
                      fontFamily: 'monospace',
                      maxHeight: 240,
                      overflowY: 'auto',
                      whiteSpace: 'pre-wrap',
                      color: '#cbd5e1',
                      border: '1px solid var(--border-subtle)',
                    }}
                  >
                    {resume.parsedText || 'No text extracted from this PDF.'}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
