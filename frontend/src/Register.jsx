import {useState} from "react";
import {Link} from 'react-router-dom'
import {register} from "./services/api.js";

function Register() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            await register(email, password);
            alert("Registered");
        } catch (err){
            alert("Failed to register");
        }
    };
    return(
        <div>
            <h2>Register An Account</h2>
            <form onSubmit={handleRegister}>
                <input type="email" placeholder="example@dalgona.com" onChange={e => setEmail(e.target.value)}/>
                <input type="password" placeholder="Password" onChange={e => setPassword(e.target.value)} />
                <button type="submit">Register</button>
            </form>
            <p>Already have an account? <Link to="/login">Login Here</Link></p>
        </div>
    )
}

export default Register;