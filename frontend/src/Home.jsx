import { Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { getActiveRentals } from './services/api';

function HomePage() {
    const [activeCount, setActiveCount] = useState(0);

    useEffect(() => {
        getActiveRentals()
            .then((res) => setActiveCount(res.data.length))
            .catch(() => {});
    }, []);

    return (
        <div className="page">
            <h1>Welcome to Dalgona</h1>
            <p className="subtitle">Self-Service Umbrella Rentals</p>

            {activeCount > 0 && (
                <div className="alert alert-info">
                    You have <strong>{activeCount}</strong> active rental{activeCount > 1 ? 's' : ''}.{' '}
                    <Link to="/orders">View orders</Link>
                </div>
            )}

            <div className="home-grid">
                <Link to="/stations" className="card-link">
                    <div className="card">
                        <h2>Stations</h2>
                        <p>View Stations</p>
                    </div>
                </Link>
                <Link to="/orders" className="card-link">
                    <div className="card">
                        <h2>Orders</h2>
                        <p>View orders</p>
                    </div>
                </Link>
                <Link to="/account" className="card-link">
                    <div className="card">
                        <h2>Account</h2>
                        <p>View profile</p>
                    </div>
                </Link>
            </div>
        </div>
    );
}


export default HomePage;