import client from './client';

export const resumeApi = {
  uploadResume: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await client.post('/api/resumes/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  getMyResumes: async () => {
    const response = await client.get('/api/resumes');
    return response.data;
  },

  getMyResume: async (resumeId) => {
    const response = await client.get(`/api/resumes/${resumeId}`);
    return response.data;
  },

  deleteMyResume: async (resumeId) => {
    const response = await client.delete(`/api/resumes/${resumeId}`);
    return response.data;
  },
};
