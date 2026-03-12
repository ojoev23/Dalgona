import { NavLink, useNavigate } from "react-router-dom";
import { isAuthenticated, logout } from "../services/api";
function Navbar() {
    const navigate = useNavigate();
    const loggedIn = isAuthenticated();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const linkClass = ({ isActive }) =>
        `navbar-link${isActive ? ' active' : ''}`;

    return (
        <nav className="navbar">
            <NavLink to="/home" className="navbar-brand"><img src="/dalgeezy.png" width="100px" height="auto"/></NavLink>
            <div className="navbar-links">
                {loggedIn ? (
                    <>
                        <NavLink to="/home" className={linkClass}>Home</NavLink>
                        <NavLink to="/stations" className={linkClass}>Stations</NavLink>
                        <NavLink to="/orders" className={linkClass}>Orders</NavLink>
                        <NavLink to="/account" className={linkClass}>Account</NavLink>
                        <button onClick={handleLogout} className="navbar-logout">Logout</button>
                    </>
                ) : (
                    <>
                        <NavLink to="/login" className={linkClass}>Login</NavLink>
                        <NavLink to="/register" className={linkClass}>Register</NavLink>
                    </>
                )}
            </div>
        </nav>
    );
}

export default Navbar;