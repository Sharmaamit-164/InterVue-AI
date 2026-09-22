import client from './client';

export const authApi = {
  login: async (email, password) => {
    const response = await client.post('/api/auth/login', { email, password });
    return response.data;
  },

  register: async (email, password) => {
    const response = await client.post('/api/auth/register', { email, password });
    return response.data;
  },

  getProfile: async () => {
    const response = await client.get('/api/users/me');
    return response.data;
  },

  updateProfile: async (email) => {
    const response = await client.put('/api/users/me', { email });
    return response.data;
  },

  deleteAccount: async () => {
    const response = await client.delete('/api/users/me');
    return response.data;
  },
};
