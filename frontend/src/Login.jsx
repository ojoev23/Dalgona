import {useState} from 'react';
import {login} from './services/api';
import {Link, useNavigate} from 'react-router-dom';

function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const [isError, setIsError] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await login(email, password);
            setIsError(false);
            setMessage("Login Successful!");
            navigate('/home');
        } catch (err) {
            setIsError(true);
            setMessage("Login failed. Check your credentials.");
        }
    };

    return (
        <div className="page">
            <h1>Login</h1>
            <p className="subtitle">Sign in to your Dalgona account</p>

            {message && (
                <div className={`alert ${isError ? 'alert-error' : 'alert-info'}`}>
                    {message}
                </div>
            )}

            <form onSubmit={handleSubmit} className="auth-form">
                <input
                    className="input"
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <input
                    className="input"
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button type="submit" className="btn btn-primary">
                    Log In
                </button>
            </form>

            <p className="auth-footer">
                Need to register an account? <Link to="/register">Register Here</Link>
            </p>
        </div>
    );
}

export default Login;