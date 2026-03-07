// src/App.jsx
import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom"
import Login from "./Login.jsx";
import Register from "./Register.jsx";
import Home from "./Home.jsx";
import Layout from "./Layout.jsx";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<Layout/>}>
                    <Route path="/" element={<Navigate to="/login" />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path={"/home"} element={<Home />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default App;