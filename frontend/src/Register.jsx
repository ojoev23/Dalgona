import {useState} from "react";
import {Link, useNavigate} from 'react-router-dom'
import {register} from "./services/api.js";

function Register() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const [isError, setIsError] = useState(false);
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            await register(email, password);
            setIsError(false);
            setMessage("Registered successfully!");
            setTimeout(() => navigate('/login'), 1000);
        } catch (err){
            setIsError(true);
            setMessage("Failed to register. Email may already be in use.");
        }
    };

    return(
        <div className="page">
            <h1>Register</h1>
            <p className="subtitle">Create your Dalgona account</p>

            {message && (
                <div className={`alert ${isError ? 'alert-error' : 'alert-info'}`}>
                    {message}
                </div>
            )}

            <form onSubmit={handleRegister} className="auth-form">
                <input
                    className="input"
                    type="email"
                    placeholder="example@dalgona.com"
                    onChange={e => setEmail(e.target.value)}
                />
                <input
                    className="input"
                    type="password"
                    placeholder="Password"
                    onChange={e => setPassword(e.target.value)}
                />
                <button type="submit" className="btn btn-primary">Register</button>
            </form>

            <p className="auth-footer">
                Already have an account? <Link to="/login">Login Here</Link>
            </p>
        </div>
    )
}

export default Register;