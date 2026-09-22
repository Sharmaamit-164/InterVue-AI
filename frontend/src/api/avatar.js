import client from './client';

export const avatarApi = {
  createSession: async (sessionConfig = {}) => {
    const response = await client.post('/api/avatar/session', sessionConfig);
    return response.data;
  },

  speak: async (text, sessionId = null, taskType = 'QUESTION') => {
    const response = await client.post('/api/avatar/speak', {
      text,
      sessionId,
      taskType,
    });
    return response.data;
  },

  endSession: async (sessionId) => {
    const response = await client.delete(`/api/avatar/session/${sessionId}`);
    return response.data;
  },
};
