import {useState} from 'react';
import {login} from './services/api';
import {Link} from 'react-router-dom';

function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await login(email, password);
            setMessage("Login Successful! Token saved.");
        } catch (err) {
            setMessage("Login failed. Check backend console.");
        }
    };

    return (
        <div>
            <h1>Login</h1>
            <form onSubmit={handleSubmit}>
                <div>
                    <input
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </div>
                <div>
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                <button type="submit">
                    Log In
                </button>
                <button><Link to="/home">Home</Link>< /button>
            </form>
            {message}

            <p>Need to register an account? <Link to="/register">Register Here</Link></p>
        </div>
    );
}

export default Login;