import { Navigate, Outlet } from 'react-router-dom';
import { isAdmin, isAuthenticated } from '../services/api';

function AdminRoute() {
    if (!isAuthenticated()) {
        return <Navigate to="/login" replace />;
    }
    return isAdmin() ? <Outlet /> : <Navigate to="/home" replace />;
}

export default AdminRoute;
