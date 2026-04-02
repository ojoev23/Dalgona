// src/App.jsx
import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom"
import Login from "./Login.jsx";
import Register from "./Register.jsx";
import Home from "./Home.jsx";
import Layout from "./Layout.jsx";
import Stations from "./Stations.jsx";
import Orders from "./Orders.jsx";
import Account from "./Account.jsx";
import Admin from "./Admin.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import GuestRoute from "./components/GuestRoute.jsx";
import AdminRoute from "./components/AdminRoute.jsx";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<Layout/>}>
                    <Route path="/" element={<Navigate to="/login" />} />

                    {/* Guest-only: redirect to /home if already logged in */}
                    <Route element={<GuestRoute />}>
                        <Route path="/login" element={<Login />} />
                        <Route path="/register" element={<Register />} />
                    </Route>

                    {/* Protected: redirect to /login if not logged in */}
                    <Route element={<ProtectedRoute />}>
                        <Route path="/home" element={<Home />} />
                        <Route path="/stations" element={<Stations />} />
                        <Route path="/orders" element={<Orders />} />
                        <Route path="/account" element={<Account />} />
                    </Route>

                    <Route element={<AdminRoute />}>
                        <Route path="/admin" element={<Admin />} />
                    </Route>
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default App;