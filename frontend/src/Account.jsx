import { useState, useEffect } from 'react';
import { getProfile, updateProfile } from './services/api';

function Account() {
    const [profile, setProfile] = useState(null);
    const [editing, setEditing] = useState(false);
    const [form, setForm] = useState({ name: '', address: '', phone: '', age: '' });
    const [message, setMessage] = useState('');
    const [isError, setIsError] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        try {
            const res = await getProfile();
            setProfile(res.data);
            setForm({
                name: res.data.name || '',
                address: res.data.address || '',
                phone: res.data.phone || '',
                age: res.data.age || '',
            });
        } catch (err) {
            setIsError(true);
            setMessage('Failed to load profile.');
        } finally {
            setLoading(false);
        }
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        try {
            const res = await updateProfile({
                name: form.name,
                address: form.address,
                phone: form.phone,
                age: Number(form.age),
            });
            setProfile(res.data);
            setEditing(false);
            setIsError(false);
            setMessage('Profile updated!');
        } catch (err) {
            setIsError(true);
            setMessage('Failed to update profile.');
        }
    };

    if (loading) return <p>Loading...</p>;

    return (
        <div className="page">
            <h1>My Account</h1>
            <p className="subtitle">Manage your profile</p>

            {message && (
                <div className={`alert ${isError ? 'alert-error' : 'alert-info'}`}>
                    {message}
                </div>
            )}

            {!editing ? (
                <div className="card">
                    <div className="profile-row">
                        <span className="profile-label">Email</span>
                        <span className="profile-value">{profile?.email}</span>
                    </div>
                    <div className="profile-row">
                        <span className="profile-label">Name</span>
                        <span className="profile-value">{profile?.name || '—'}</span>
                    </div>
                    <div className="profile-row">
                        <span className="profile-label">Address</span>
                        <span className="profile-value">{profile?.address || '—'}</span>
                    </div>
                    <div className="profile-row">
                        <span className="profile-label">Phone</span>
                        <span className="profile-value">{profile?.phone || '—'}</span>
                    </div>
                    <div className="profile-row">
                        <span className="profile-label">Age</span>
                        <span className="profile-value">{profile?.age || '—'}</span>
                    </div>
                    <button onClick={() => setEditing(true)} className="btn btn-primary" style={{ marginTop: '20px' }}>
                        Edit Profile
                    </button>
                </div>
            ) : (
                <form onSubmit={handleUpdate} className="edit-form">
                    <div>
                        <label className="label">Name</label>
                        <input
                            className="input"
                            type="text"
                            value={form.name}
                            onChange={(e) => setForm({ ...form, name: e.target.value })}
                        />
                    </div>
                    <div>
                        <label className="label">Address</label>
                        <input
                            className="input"
                            type="text"
                            value={form.address}
                            onChange={(e) => setForm({ ...form, address: e.target.value })}
                        />
                    </div>
                    <div>
                        <label className="label">Phone</label>
                        <input
                            className="input"
                            type="text"
                            value={form.phone}
                            onChange={(e) => setForm({ ...form, phone: e.target.value })}
                        />
                    </div>
                    <div>
                        <label className="label">Age</label>
                        <input
                            className="input"
                            type="number"
                            value={form.age}
                            onChange={(e) => setForm({ ...form, age: e.target.value })}
                        />
                    </div>
                    <div className="edit-form-actions">
                        <button type="submit" className="btn btn-primary">Save</button>
                        <button type="button" className="btn btn-ghost" onClick={() => setEditing(false)}>Cancel</button>
                    </div>
                </form>
            )}
        </div>
    );
}


export default Account;

