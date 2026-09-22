import client from './client';

export const interviewApi = {
  createInterview: async (resumeId, jobId) => {
    const response = await client.post('/api/interviews', { resumeId, jobId });
    return response.data;
  },

  getMyInterviews: async () => {
    const response = await client.get('/api/interviews');
    return response.data;
  },

  getMyInterview: async (interviewId) => {
    const response = await client.get(`/api/interviews/${interviewId}`);
    return response.data;
  },

  getInterviewQuestions: async (interviewId) => {
    const response = await client.get(`/api/interviews/${interviewId}/questions`);
    return response.data;
  },

  // AI Interview Flow
  getFirstQuestion: async (interviewId) => {
    const response = await client.post(`/api/ai/interview/${interviewId}/first-question`);
    return response.data;
  },

  submitAnswerAndGetNextQuestion: async (interviewId, candidateAnswer) => {
    const response = await client.post(`/api/ai/interview/${interviewId}/next-question`, {
      candidateAnswer,
    });
    return response.data;
  },

  completeInterview: async (interviewId) => {
    const response = await client.put(`/api/interviews/${interviewId}/complete`);
    return response.data;
  },
};
