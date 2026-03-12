import axios from 'axios';

const BASE_URL = '/api';

const authHeader = () => {
    const token = localStorage.getItem('jwt_token');
    return token ? { Authorization: `Bearer ${token}` } : {};
};

// ── Auth ──

export const register = async (email, password) => {
    return axios.post(`${BASE_URL}/auth/register`, { email, password });
};

export const login = async (email, password) => {
    const response = await axios.post(`${BASE_URL}/auth/login`, { email, password });
    if (response.data && response.data.token) {
        localStorage.setItem('jwt_token', response.data.token);
    }
    return response.data;
};

export const logout = () => {
    localStorage.removeItem('jwt_token');
};

export const isAuthenticated = () => {
    return !!localStorage.getItem('jwt_token');
};

// ── Account ──

export const getProfile = async () => {
    return axios.get(`${BASE_URL}/account/me`, { headers: authHeader() });
};

export const updateProfile = async (data) => {
    return axios.put(`${BASE_URL}/account/me`, data, { headers: authHeader() });
};

// ── Stations ──

export const getStations = async () => {
    return axios.get(`${BASE_URL}/stations`, { headers: authHeader() });
};

export const getStation = async (id) => {
    return axios.get(`${BASE_URL}/stations/${id}`, { headers: authHeader() });
};

export const createStation = async (location, capacity) => {
    return axios.post(`${BASE_URL}/stations`, { location, capacity }, { headers: authHeader() });
};

// ── Umbrellas (Rent / Return) ──

export const rentUmbrella = async (stationId) => {
    return axios.post(`${BASE_URL}/umbrellas/rent`, { stationId }, { headers: authHeader() });
};

export const returnUmbrella = async (stationId, umbrellaId) => {
    return axios.post(`${BASE_URL}/umbrellas/return`, { stationId, umbrellaId }, { headers: authHeader() });
};

// ── Orders ──

export const getOrders = async () => {
    return axios.get(`${BASE_URL}/orders`, { headers: authHeader() });
};

export const getActiveRentals = async () => {
    return axios.get(`${BASE_URL}/orders/active`, { headers: authHeader() });
};

export const getOrder = async (id) => {
    return axios.get(`${BASE_URL}/orders/${id}`, { headers: authHeader() });
};
