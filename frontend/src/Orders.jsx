import { useState, useEffect } from 'react';
import { getOrders, getActiveRentals } from './services/api';

function Orders() {
    const [orders, setOrders] = useState([]);
    const [view, setView] = useState('all');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const fetchOrders = async (type) => {
        setLoading(true);
        setError('');
        try {
            const res = type === 'active' ? await getActiveRentals() : await getOrders();
            setOrders(res.data);
        } catch {
            setError('Failed to load orders.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchOrders(view);
    }, [view]);

    const formatDate = (dateStr) => {
        if (!dateStr) return '—';
        return new Date(dateStr).toLocaleString();
    };

    return (
        <div className="page-wide">
            <h1>Orders</h1>
            <p className="subtitle">Your rental history</p>

            <div className="toggle-group">
                <button
                    onClick={() => setView('all')}
                    className={`toggle-btn${view === 'all' ? ' active' : ''}`}
                >
                    All Orders
                </button>
                <button
                    onClick={() => setView('active')}
                    className={`toggle-btn${view === 'active' ? ' active' : ''}`}
                >
                    Active Rentals
                </button>
            </div>

            {error && <div className="alert alert-error">{error}</div>}
            {loading ? (
                <p>Loading...</p>
            ) : orders.length === 0 ? (
                <p>No orders found.</p>
            ) : (
                <div className="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>Order ID</th>
                                <th>Umbrella</th>
                                <th>Pickup Station</th>
                                <th>Return Station</th>
                                <th>Rented At</th>
                                <th>Returned At</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            {orders.map((o) => (
                                <tr key={o.orderId}>
                                    <td>{o.orderId}</td>
                                    <td>#{o.umbrellaId}</td>
                                    <td>{o.pickupStationLocation}</td>
                                    <td>{o.returnStationLocation || '—'}</td>
                                    <td>{formatDate(o.rentedAt)}</td>
                                    <td>{formatDate(o.returnedAt)}</td>
                                    <td>
                                        <span className={`badge ${o.active ? 'badge-active' : 'badge-completed'}`}>
                                            {o.active ? 'Active' : 'Completed'}
                                        </span>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

export default Orders;

