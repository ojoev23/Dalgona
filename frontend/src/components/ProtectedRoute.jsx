import { Navigate, Outlet } from 'react-router-dom';
import { isAuthenticated } from '../services/api';

function ProtectedRoute() {
    return isAuthenticated() ? <Outlet /> : <Navigate to="/login" replace />;
}

export default ProtectedRoute;

