import client from './client';

export const jobApi = {
  createJob: async (jobData) => {
    const response = await client.post('/api/jobs', jobData);
    return response.data;
  },

  getMyJobs: async () => {
    const response = await client.get('/api/jobs');
    return response.data;
  },

  getMyJob: async (jobId) => {
    const response = await client.get(`/api/jobs/${jobId}`);
    return response.data;
  },

  updateJob: async (jobId, jobData) => {
    const response = await client.put(`/api/jobs/${jobId}`, jobData);
    return response.data;
  },

  deleteMyJob: async (jobId) => {
    const response = await client.delete(`/api/jobs/${jobId}`);
    return response.data;
  },
};
