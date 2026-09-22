import client from './client';

export const evaluationApi = {
  generateEvaluation: async (interviewId) => {
    const response = await client.post(`/api/evaluations/generate/${interviewId}`);
    return response.data;
  },

  getEvaluation: async (interviewId) => {
    const response = await client.get(`/api/evaluations/${interviewId}`);
    return response.data;
  },
};
