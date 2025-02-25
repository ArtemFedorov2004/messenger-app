import axios from 'axios';

const token = localStorage.getItem('access_token');

const api = axios.create({
    baseURL: 'http://localhost:8080',
});

if (token) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
}

export default api;
