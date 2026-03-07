import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

export const register = async (email, password) => {
    return axios.post(`${API_URL}/register`, { email, password });
};

export const login = async (email, password) => {
    const response = await axios.post(`${API_URL}/login`, { email, password });
    if (response.data) {
        localStorage.setItem('jwt_token', response.data);
    }
    return response.data;
};