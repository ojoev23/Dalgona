import { useState, useEffect } from 'react';
import { getStations, getActiveRentals, rentUmbrella, returnUmbrella } from './services/api';

function Stations() {
    const [stations, setStations] = useState([]);
    const [activeRental, setActiveRental] = useState(null);
    const [message, setMessage] = useState('');
    const [isError, setIsError] = useState(false);

    const fetchStations = async () => {
        try {
            const res = await getStations();
            setStations(res.data);
        } catch {
            setIsError(true);
            setMessage('Failed to load stations.');
        }
    };

    const fetchActiveRental = async () => {
        try {
            const res = await getActiveRentals();
            // Backend enforces max 1 active rental per account.
            setActiveRental(res.data?.[0] ?? null);
        } catch {
            setActiveRental(null);
        }
    };

    useEffect(() => {
        const loadStationsData = async () => {
            await Promise.all([fetchStations(), fetchActiveRental()]);
        };
        loadStationsData();
    }, []);

    const handleRent = async (stationId) => {
        try {
            const res = await rentUmbrella(stationId);
            setIsError(false);
            setMessage(`Rented umbrella #${res.data.umbrellaId} from ${res.data.pickupStationLocation}`);
            fetchStations();
            fetchActiveRental();
        } catch (err) {
            setIsError(true);
            setMessage(err.response?.data?.message || err.response?.data || 'Rent failed.');
        }
    };

    const handleReturn = async (stationId) => {
        if (!activeRental?.umbrellaId) {
            setIsError(true);
            setMessage('No active rental found to return.');
            return;
        }

        try {
            const res = await returnUmbrella(stationId, activeRental.umbrellaId);
            setIsError(false);
            setMessage(`Returned umbrella #${res.data.umbrellaId} to ${res.data.returnStationLocation}`);
            fetchStations();
            fetchActiveRental();
        } catch (err) {
            setIsError(true);
            setMessage(err.response?.data?.message || err.response?.data || 'Return failed.');
        }
    };

    return (
        <div className="page-wide">
            <h1>Stations</h1>
            <p className="subtitle">Browse stations, rent umbrellas, and return your active rental</p>

            {activeRental && (
                <div className="alert alert-info">
                    Active rental: umbrella #{activeRental.umbrellaId} from {activeRental.pickupStationLocation}
                </div>
            )}

            {message && (
                <div className={`alert ${isError ? 'alert-error' : 'alert-info'}`}>
                    {message}
                </div>
            )}

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
                                <td>{s.location}</td>
                                <td>{s.capacity}</td>
                                <td>{s.occupied}</td>
                                <td>{s.available}</td>
                                <td>
                                    <button
                                        className="btn btn-primary"
                                        onClick={() => handleRent(s.stationId)}
                                        disabled={s.available === 0 || !!activeRental}
                                    >
                                        Rent
                                    </button>
                                    <button
                                        className="btn btn-outline"
                                        onClick={() => handleReturn(s.stationId)}
                                        disabled={!activeRental || s.available === 0}
                                        style={{ marginLeft: '8px' }}
                                    >
                                        Return Here
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}


export default Stations;

