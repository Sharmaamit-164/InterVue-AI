import React, { useState, useEffect } from 'react';
import { jobApi } from '../api/job';
import { Briefcase, Plus, Trash2, Edit3, CheckCircle, Sparkles } from 'lucide-react';

export const JobManagement = () => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingJobId, setEditingJobId] = useState(null);
  const [message, setMessage] = useState(null);

  const [formData, setFormData] = useState({
    title: '',
    companyName: '',
    description: '',
    requiredSkills: '',
  });

  const fetchJobs = async () => {
    try {
      setLoading(true);
      const data = await jobApi.getMyJobs();
      setJobs(data);
    } catch (err) {
      console.error('Failed to fetch jobs:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchJobs();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingJobId) {
        await jobApi.updateJob(editingJobId, formData);
        setMessage({ type: 'success', text: 'Job description updated successfully!' });
      } else {
        await jobApi.createJob(formData);
        setMessage({ type: 'success', text: 'Target job added successfully!' });
      }
      resetForm();
      fetchJobs();
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Failed to save job.' });
    }
  };

  const handleEdit = (job) => {
    setEditingJobId(job.id);
    setFormData({
      title: job.title,
      companyName: job.companyName,
      description: job.description,
      requiredSkills: job.requiredSkills || '',
    });
    setShowForm(true);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleDelete = async (jobId) => {
    if (!window.confirm('Are you sure you want to delete this job description?')) return;
    try {
      await jobApi.deleteMyJob(jobId);
      setJobs(jobs.filter((j) => j.id !== jobId));
      setMessage({ type: 'success', text: 'Job removed successfully.' });
    } catch (err) {
      setMessage({ type: 'error', text: 'Failed to delete job.' });
    }
  };

  const resetForm = () => {
    setFormData({ title: '', companyName: '', description: '', requiredSkills: '' });
    setEditingJobId(null);
    setShowForm(false);
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 32, flexWrap: 'wrap', gap: 16 }}>
        <div>
          <h2>Job Descriptions</h2>
          <p style={{ color: 'var(--text-muted)', marginTop: 4 }}>
            Manage the job descriptions and skills used to guide the AI interview questioning.
          </p>
        </div>
        {!showForm && (
          <button className="btn btn-primary" onClick={() => setShowForm(true)}>
            <Plus size={18} />
            Add Target Job
          </button>
        )}
      </div>

      {message && (
        <div className={`alert ${message.type === 'error' ? 'alert-error' : 'alert-success'}`}>
          {message.text}
        </div>
      )}

      {/* Add / Edit Form */}
      {showForm && (
        <div className="glass-card" style={{ marginBottom: 36, borderColor: 'var(--border-active)' }}>
          <h3 style={{ marginBottom: 20 }}>
            {editingJobId ? 'Edit Job Description' : 'Add New Target Job'}
          </h3>

          <form onSubmit={handleSubmit}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20 }}>
              <div className="form-group">
                <label className="form-label">Job Title *</label>
                <input
                  type="text"
                  className="form-input"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="e.g. Senior Java Backend Engineer"
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Company Name *</label>
                <input
                  type="text"
                  className="form-input"
                  value={formData.companyName}
                  onChange={(e) => setFormData({ ...formData, companyName: e.target.value })}
                  placeholder="e.g. Google, Amazon, Acme Corp"
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Required Skills (Comma-separated) *</label>
              <input
                type="text"
                className="form-input"
                value={formData.requiredSkills}
                onChange={(e) => setFormData({ ...formData, requiredSkills: e.target.value })}
                placeholder="e.g. Java, Spring Boot, PostgreSQL, Microservices, REST APIs, Docker"
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Job Description *</label>
              <textarea
                className="form-textarea"
                rows={5}
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="Paste the full job posting requirements, responsibilities, and qualifications..."
                required
              />
            </div>

            <div style={{ display: 'flex', gap: 12, justifyContent: 'flex-end' }}>
              <button type="button" className="btn btn-secondary" onClick={resetForm}>
                Cancel
              </button>
              <button type="submit" className="btn btn-primary">
                {editingJobId ? 'Update Job' : 'Save Job'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Jobs List */}
      <div className="glass-card">
        <h3 style={{ marginBottom: 20 }}>Your Target Jobs</h3>

        {loading ? (
          <p style={{ color: 'var(--text-muted)' }}>Loading jobs...</p>
        ) : jobs.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '32px 0', color: 'var(--text-dim)' }}>
            <Briefcase size={40} style={{ marginBottom: 10 }} />
            <p>No jobs added yet. Add a job description above to start interview practice.</p>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            {jobs.map((job) => (
              <div
                key={job.id}
                style={{
                  border: '1px solid var(--border-subtle)',
                  borderRadius: 'var(--radius-sm)',
                  padding: 20,
                  background: 'rgba(15, 23, 42, 0.5)',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 12 }}>
                  <div>
                    <h4 style={{ fontSize: '1.15rem' }}>{job.title}</h4>
                    <div style={{ color: 'var(--accent-cyan)', fontSize: '0.9rem', fontWeight: 600, marginTop: 2 }}>
                      {job.companyName}
                    </div>
                  </div>

                  <div style={{ display: 'flex', gap: 8 }}>
                    <button className="btn btn-secondary btn-sm" onClick={() => handleEdit(job)}>
                      <Edit3 size={14} /> Edit
                    </button>
                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(job.id)}>
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>

                {job.requiredSkills && (
                  <div style={{ marginTop: 14, display: 'flex', flexWrap: 'wrap', gap: 6 }}>
                    {job.requiredSkills.split(',').map((skill, index) => (
                      <span
                        key={index}
                        style={{
                          background: 'rgba(99, 102, 241, 0.15)',
                          color: '#a5b4fc',
                          padding: '3px 10px',
                          borderRadius: 'var(--radius-full)',
                          fontSize: '0.78rem',
                          border: '1px solid rgba(99, 102, 241, 0.25)',
                        }}
                      >
                        {skill.trim()}
                      </span>
                    ))}
                  </div>
                )}

                <p
                  style={{
                    color: 'var(--text-muted)',
                    fontSize: '0.875rem',
                    marginTop: 12,
                    display: '-webkit-box',
                    WebkitLineClamp: 3,
                    WebkitBoxOrient: 'vertical',
                    overflow: 'hidden',
                  }}
                >
                  {job.description}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
