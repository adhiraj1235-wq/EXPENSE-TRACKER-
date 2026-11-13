import axios from 'axios';

/**
 * Axios instance configured to call the API Gateway.
 * During development, Vite proxies /api → http://localhost:8080
 * so we set baseURL to '' (empty) and all calls use relative paths.
 *
 * Request interceptor automatically attaches the JWT token from
 * localStorage to every request as "Authorization: Bearer <token>".
 */
const api = axios.create({
  baseURL: '', // Vite proxy handles /api → http://localhost:8080
  headers: {
    'Content-Type': 'application/json',
  },
});

// Attach JWT token to every outgoing request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// If the server returns 401, clear token and redirect to login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;

