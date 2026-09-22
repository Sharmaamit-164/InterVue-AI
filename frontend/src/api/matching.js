import client from './client';

export const matchingApi = {
  matchResumeWithJob: async (resumeId, jobId) => {
    const response = await client.post(`/api/matching/resume/${resumeId}/job/${jobId}`);
    return response.data;
  },
};
