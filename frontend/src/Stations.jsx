import { useState, useEffect } from 'react';
import { getStations, rentUmbrella, returnUmbrella } from './services/api';

function Stations() {
    const [stations, setStations] = useState([]);
    const [message, setMessage] = useState('');
    const [isError, setIsError] = useState(false);
    const [returnForm, setReturnForm] = useState({ stationId: '', umbrellaId: '' });

    const fetchStations = async () => {
        try {
            const res = await getStations();
            setStations(res.data);
        } catch (err) {
            setIsError(true);
            setMessage('Failed to load stations.');
        }
    };

    useEffect(() => {
        fetchStations();
    }, []);

    const handleRent = async (stationId) => {
        try {
            const res = await rentUmbrella(stationId);
            setIsError(false);
            setMessage(`Rented umbrella #${res.data.umbrellaId} from ${res.data.pickupStationLocation}`);
            fetchStations();
        } catch (err) {
            setIsError(true);
            setMessage(err.response?.data?.message || err.response?.data || 'Rent failed.');
        }
    };

    const handleReturn = async (e) => {
        e.preventDefault();
        try {
            const res = await returnUmbrella(
                Number(returnForm.stationId),
                Number(returnForm.umbrellaId)
            );
            setIsError(false);
            setMessage(`Returned umbrella #${res.data.umbrellaId} to ${res.data.returnStationLocation}`);
            setReturnForm({ stationId: '', umbrellaId: '' });
            fetchStations();
        } catch (err) {
            setIsError(true);
            setMessage(err.response?.data?.message || err.response?.data || 'Return failed.');
        }
    };

    return (
        <div className="page-wide">
            <h1>Stations</h1>
            <p className="subtitle">Browse available stations and rent an umbrella</p>

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
                            <th>Action</th>
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
                                        disabled={s.available === 0}
                                    >
                                        Rent
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            <h2 className="section-title">Return an Umbrella</h2>
            <form onSubmit={handleReturn} className="inline-form">
                <input
                    className="input"
                    type="number"
                    placeholder="Station ID"
                    value={returnForm.stationId}
                    onChange={(e) => setReturnForm({ ...returnForm, stationId: e.target.value })}
                    required
                />
                <input
                    className="input"
                    type="number"
                    placeholder="Umbrella ID"
                    value={returnForm.umbrellaId}
                    onChange={(e) => setReturnForm({ ...returnForm, umbrellaId: e.target.value })}
                    required
                />
                <button type="submit" className="btn btn-outline">Return</button>
            </form>
        </div>
    );
}


export default Stations;

