import { Navigate, Outlet } from 'react-router-dom';
import { isAuthenticated } from '../services/api';

function GuestRoute() {
    return isAuthenticated() ? <Navigate to="/home" replace /> : <Outlet />;
}

export default GuestRoute;

