import axios from 'axios';

const BASE_URL = '/api';

const authHeader = () => {
    const token = localStorage.getItem('jwt_token');
    return token ? { Authorization: `Bearer ${token}` } : {};
};

const ROLE_KEY = 'auth_role';

// ── Auth ──

export const register = async (email, password) => {
    return axios.post(`${BASE_URL}/auth/register`, { email, password });
};

export const login = async (email, password) => {
    const response = await axios.post(`${BASE_URL}/auth/login`, { email, password });
    if (response.data && response.data.token) {
        localStorage.setItem('jwt_token', response.data.token);
        localStorage.setItem(ROLE_KEY, response.data.role || 'USER');
    }
    return response.data;
};

export const logout = () => {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem(ROLE_KEY);
};

export const isAuthenticated = () => {
    return !!localStorage.getItem('jwt_token');
};

export const getAuthRole = () => {
    return localStorage.getItem(ROLE_KEY) || 'USER';
};

export const isAdmin = () => {
    return getAuthRole() === 'ADMIN';
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

// -- Admin --

export const adminGetAccounts = async () => {
    return axios.get(`${BASE_URL}/admin/accounts`, { headers: authHeader() });
};

export const adminUpdateAccount = async (accountId, payload) => {
    return axios.put(`${BASE_URL}/admin/accounts/${accountId}`, payload, { headers: authHeader() });
};

export const adminDeleteAccount = async (accountId) => {
    return axios.delete(`${BASE_URL}/admin/accounts/${accountId}`, { headers: authHeader() });
};

export const adminGetOrders = async () => {
    return axios.get(`${BASE_URL}/admin/orders`, { headers: authHeader() });
};

export const adminDeleteOrder = async (orderId) => {
    return axios.delete(`${BASE_URL}/admin/orders/${orderId}`, { headers: authHeader() });
};

export const adminGetUmbrellas = async () => {
    return axios.get(`${BASE_URL}/admin/umbrellas`, { headers: authHeader() });
};

export const adminCreateUmbrella = async () => {
    return axios.post(`${BASE_URL}/admin/umbrellas`, {}, { headers: authHeader() });
};


export const adminDeleteUmbrella = async (umbrellaId) => {
    return axios.delete(`${BASE_URL}/admin/umbrellas/${umbrellaId}`, { headers: authHeader() });
};

export const adminGetStations = async () => {
    return axios.get(`${BASE_URL}/admin/stations`, { headers: authHeader() });
};

export const adminCreateStation = async (location, capacity) => {
    return axios.post(`${BASE_URL}/admin/stations`, { location, capacity }, { headers: authHeader() });
};

export const adminUpdateStation = async (stationId, payload) => {
    return axios.put(`${BASE_URL}/admin/stations/${stationId}`, payload, { headers: authHeader() });
};

export const adminDeleteStation = async (stationId) => {
    return axios.delete(`${BASE_URL}/admin/stations/${stationId}`, { headers: authHeader() });
};

