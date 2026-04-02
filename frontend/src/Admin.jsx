import { useEffect, useState } from 'react';
import {
    adminCreateStation,
    adminCreateUmbrella,
    adminDeleteAccount,
    adminDeleteOrder,
    adminDeleteStation,
    adminDeleteUmbrella,
    adminGetAccounts,
    adminGetOrders,
    adminGetStations,
    adminGetUmbrellas,
    adminUpdateAccount,
    adminUpdateStation,
} from './services/api';

const ADMIN_SECTIONS = ['accounts', 'orders', 'umbrellas', 'stations'];

function Admin() {
    const [accounts, setAccounts] = useState([]);
    const [orders, setOrders] = useState([]);
    const [umbrellas, setUmbrellas] = useState([]);
    const [stations, setStations] = useState([]);
    const [activeSection, setActiveSection] = useState('accounts');
    const [stationEdits, setStationEdits] = useState({});
    const [message, setMessage] = useState('');
    const [isError, setIsError] = useState(false);
    const [newStation, setNewStation] = useState({ location: '', capacity: 1 });

    const loadAdminData = async () => {
        try {
            const [accountsRes, ordersRes, umbrellasRes, stationsRes] = await Promise.all([
                adminGetAccounts(),
                adminGetOrders(),
                adminGetUmbrellas(),
                adminGetStations(),
            ]);
            setAccounts(accountsRes.data);
            setOrders(ordersRes.data);
            setUmbrellas(umbrellasRes.data);
            setStations(stationsRes.data);
        } catch {
            setIsError(true);
            setMessage('Failed to load admin data.');
        }
    };

    useEffect(() => {
        const load = async () => {
            await loadAdminData();
        };
        load();
    }, []);

    const showSuccess = (text) => {
        setIsError(false);
        setMessage(text);
    };

    const showFailure = (err, fallback) => {
        setIsError(true);
        setMessage(err.response?.data?.message || err.response?.data || fallback);
    };

    const handleRoleToggle = async (account) => {
        const nextRole = account.role === 'ADMIN' ? 'USER' : 'ADMIN';
        try {
            await adminUpdateAccount(account.accountId, { role: nextRole });
            showSuccess(`Updated ${account.email} to ${nextRole}`);
            loadAdminData();
        } catch (err) {
            showFailure(err, 'Failed to update account role.');
        }
    };

    const handleDeleteAccount = async (accountId) => {
        try {
            await adminDeleteAccount(accountId);
            showSuccess(`Deleted account #${accountId}`);
            loadAdminData();
        } catch (err) {
            showFailure(err, 'Failed to delete account.');
        }
    };

    const handleDeleteOrder = async (orderId) => {
        try {
            await adminDeleteOrder(orderId);
            showSuccess(`Deleted order #${orderId}`);
            loadAdminData();
        } catch (err) {
            showFailure(err, 'Failed to delete order.');
        }
    };

    const handleCreateUmbrella = async (e) => {
        e.preventDefault();
        try {
            await adminCreateUmbrella();
            showSuccess('Created umbrella.');
            loadAdminData();
        } catch (err) {
            showFailure(err, 'Failed to create umbrella.');
        }
    };

    const handleDeleteUmbrella = async (umbrellaId) => {
        try {
            await adminDeleteUmbrella(umbrellaId);
            showSuccess(`Deleted umbrella #${umbrellaId}`);
            loadAdminData();
        } catch (err) {
            showFailure(err, 'Failed to delete umbrella.');
        }
    };

    const handleCreateStation = async (e) => {
        e.preventDefault();
        try {
            await adminCreateStation(newStation.location, Number(newStation.capacity));
            setNewStation({ location: '', capacity: 1 });
            showSuccess('Created station.');
            loadAdminData();
        } catch (err) {
            showFailure(err, 'Failed to create station.');
        }
    };

    const startStationEdit = (station) => {
        setStationEdits((prev) => ({
            ...prev,
            [station.stationId]: {
                location: station.location,
                capacity: String(station.capacity),
            },
        }));
    };

    const cancelStationEdit = (stationId) => {
        setStationEdits((prev) => {
            const next = { ...prev };
            delete next[stationId];
            return next;
        });
    };

    const updateStationEdit = (stationId, field, value) => {
        setStationEdits((prev) => ({
            ...prev,
            [stationId]: {
                ...(prev[stationId] || {}),
                [field]: value,
            },
        }));
    };

    const saveStationEdit = async (stationId) => {
        const edit = stationEdits[stationId];
        const parsedCapacity = Number(edit?.capacity);
        if (!edit || !edit.location?.trim()) {
            setIsError(true);
            setMessage('Station location is required.');
            return;
        }
        if (!Number.isInteger(parsedCapacity) || parsedCapacity <= 0) {
            setIsError(true);
            setMessage('Capacity must be a positive integer.');
            return;
        }

        try {
            await adminUpdateStation(stationId, {
                location: edit.location.trim(),
                capacity: parsedCapacity,
            });
            showSuccess(`Updated station #${stationId}`);
            cancelStationEdit(stationId);
            loadAdminData();
        } catch (err) {
            showFailure(err, 'Failed to update station.');
        }
    };

    const handleDeleteStation = async (stationId) => {
        try {
            await adminDeleteStation(stationId);
            showSuccess(`Deleted station #${stationId}`);
            loadAdminData();
        } catch (err) {
            showFailure(err, 'Failed to delete station.');
        }
    };

    return (
        <div className="page-wide">
            <h1>Admin Console</h1>
            <p className="subtitle">Manage accounts, orders, umbrellas, and stations</p>

            <div className="inline-form" style={{ marginBottom: '20px' }}>
                <select
                    className="input"
                    value={activeSection}
                    onChange={(e) => setActiveSection(e.target.value)}
                    style={{ maxWidth: '240px' }}
                >
                    {ADMIN_SECTIONS.map((section) => (
                        <option key={section} value={section}>
                            {section.charAt(0).toUpperCase() + section.slice(1)}
                        </option>
                    ))}
                </select>
            </div>

            {message && (
                <div className={`alert ${isError ? 'alert-error' : 'alert-info'}`}>
                    {message}
                </div>
            )}

            {activeSection === 'accounts' && (
                <>
            <h2 className="section-title">Accounts</h2>
            <div className="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Email</th>
                            <th>Name</th>
                            <th>Role</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {accounts.map((a) => (
                            <tr key={a.accountId}>
                                <td>{a.accountId}</td>
                                <td>{a.email}</td>
                                <td>{a.name || '-'}</td>
                                <td>{a.role}</td>
                                <td>
                                    <button className="btn btn-outline" onClick={() => handleRoleToggle(a)}>
                                        Make {a.role === 'ADMIN' ? 'User' : 'Admin'}
                                    </button>
                                    <button
                                        className="btn btn-outline"
                                        onClick={() => handleDeleteAccount(a.accountId)}
                                        style={{ marginLeft: '8px' }}
                                    >
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
                </>
            )}

            {activeSection === 'orders' && (
                <>
            <h2 className="section-title">Orders</h2>
            <div className="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Umbrella</th>
                            <th>Pickup</th>
                            <th>Return</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map((o) => (
                            <tr key={o.orderId}>
                                <td>{o.orderId}</td>
                                <td>#{o.umbrellaId}</td>
                                <td>{o.pickupStationLocation}</td>
                                <td>{o.returnStationLocation || '-'}</td>
                                <td>{o.active ? 'ACTIVE' : 'COMPLETED'}</td>
                                <td>
                                    <button
                                        className="btn btn-outline"
                                        onClick={() => handleDeleteOrder(o.orderId)}
                                        disabled={o.active}
                                    >
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
                </>
            )}

            {activeSection === 'umbrellas' && (
                <>
            <h2 className="section-title">Umbrellas</h2>
            <form onSubmit={handleCreateUmbrella} className="inline-form">
                <button type="submit" className="btn btn-primary">Add Umbrella</button>
            </form>
            <div className="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>In Use</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {umbrellas.map((u) => (
                            <tr key={u.umbrellaId}>
                                <td>{u.umbrellaId}</td>
                                <td>{u.inUse ? 'Yes' : 'No'}</td>
                                <td>
                                    <button
                                        className="btn btn-outline"
                                        onClick={() => handleDeleteUmbrella(u.umbrellaId)}
                                    >
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
                </>
            )}

            {activeSection === 'stations' && (
                <>
            <h2 className="section-title">Stations</h2>
            <form onSubmit={handleCreateStation} className="inline-form">
                <input
                    className="input"
                    placeholder="Location"
                    value={newStation.location}
                    onChange={(e) => setNewStation({ ...newStation, location: e.target.value })}
                    required
                />
                <input
                    className="input"
                    type="number"
                    min="1"
                    value={newStation.capacity}
                    onChange={(e) => setNewStation({ ...newStation, capacity: e.target.value })}
                    required
                />
                <button type="submit" className="btn btn-primary">Create Station</button>
            </form>
            <div className="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Location</th>
                            <th>Capacity</th>
                            <th>Occupied</th>
                            <th>Available</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {stations.map((s) => (
                            <tr key={s.stationId}>
                                <td>{s.stationId}</td>
                                <td>
                                    {stationEdits[s.stationId] ? (
                                        <input
                                            className="input"
                                            value={stationEdits[s.stationId].location}
                                            onChange={(e) => updateStationEdit(s.stationId, 'location', e.target.value)}
                                        />
                                    ) : (
                                        s.location
                                    )}
                                </td>
                                <td>
                                    {stationEdits[s.stationId] ? (
                                        <input
                                            className="input"
                                            type="number"
                                            min="1"
                                            value={stationEdits[s.stationId].capacity}
                                            onChange={(e) => updateStationEdit(s.stationId, 'capacity', e.target.value)}
                                        />
                                    ) : (
                                        s.capacity
                                    )}
                                </td>
                                <td>{s.occupied}</td>
                                <td>{s.available}</td>
                                <td>
                                    {stationEdits[s.stationId] ? (
                                        <>
                                            <button
                                                className="btn btn-outline"
                                                onClick={() => saveStationEdit(s.stationId)}
                                            >
                                                Save
                                            </button>
                                            <button
                                                className="btn btn-outline"
                                                onClick={() => cancelStationEdit(s.stationId)}
                                                style={{ marginLeft: '8px' }}
                                            >
                                                Cancel
                                            </button>
                                        </>
                                    ) : (
                                        <button className="btn btn-outline" onClick={() => startStationEdit(s)}>
                                            Edit
                                        </button>
                                    )}
                                    <button
                                        className="btn btn-outline"
                                        onClick={() => handleDeleteStation(s.stationId)}
                                        style={{ marginLeft: '8px' }}
                                    >
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
                </>
            )}
        </div>
    );
}

export default Admin;

