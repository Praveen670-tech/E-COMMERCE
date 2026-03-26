import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext.jsx'

const base = 'http://localhost:8080/api'

export default function Settings() {
  const { token, role, logout } = useAuth()
  const [profile, setProfile] = useState({ name: '', email: '', phoneNumber: '', address: '' })
  const [passwords, setPasswords] = useState({ old: '', new: '', confirm: '' })
  const [status, setStatus] = useState('')

  const headers = { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }

  useEffect(() => {
    // Fetch current profile info based on role
    // For now, we'll just mock this or assume an endpoint exists
  }, [])

  const updateProfile = async (e) => {
    e.preventDefault()
    setStatus('Updating profile...')
    // API call to update profile
    setStatus('Profile updated successfully!')
  }

  const changePassword = async (e) => {
    e.preventDefault()
    if (passwords.new !== passwords.confirm) {
      setStatus('Passwords do not match')
      return
    }
    setStatus('Changing password...')
    // API call to change password
    setStatus('Password changed successfully!')
    setPasswords({ old: '', new: '', confirm: '' })
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h2>Account Settings</h2>
        <p>Manage your profile and security settings for your {role} account</p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
        <div className="auth-card">
          <h3>Update Profile</h3>
          <form className="form" onSubmit={updateProfile}>
            <div className="form-group">
              <label>Full Name</label>
              <input value={profile.name} onChange={e => setProfile({...profile, name: e.target.value})} />
            </div>
            <div className="form-group">
              <label>Phone Number</label>
              <input value={profile.phoneNumber} onChange={e => setProfile({...profile, phoneNumber: e.target.value})} />
            </div>
            <div className="form-group">
              <label>Delivery/Business Address</label>
              <textarea value={profile.address} onChange={e => setProfile({...profile, address: e.target.value})} />
            </div>
            <button className="auth-submit" type="submit">Save Changes</button>
          </form>
        </div>

        <div className="auth-card">
          <h3>Change Password</h3>
          <form className="form" onSubmit={changePassword}>
            <div className="form-group">
              <label>Old Password</label>
              <input type="password" value={passwords.old} onChange={e => setPasswords({...passwords, old: e.target.value})} />
            </div>
            <div className="form-group">
              <label>New Password</label>
              <input type="password" value={passwords.new} onChange={e => setPasswords({...passwords, new: e.target.value})} />
            </div>
            <div className="form-group">
              <label>Confirm New Password</label>
              <input type="password" value={passwords.confirm} onChange={e => setPasswords({...passwords, confirm: e.target.value})} />
            </div>
            <button className="auth-submit" type="submit">Update Password</button>
          </form>
        </div>
      </div>

      <div style={{ textAlign: 'center', marginTop: '3rem' }}>
        <button className="nav-button" onClick={logout} style={{ background: '#ef4444', color: 'white', padding: '0.8rem 2rem' }}>
          Logout from all devices
        </button>
        {status && <p style={{ marginTop: '1rem', color: status.includes('success') ? 'green' : 'red' }}>{status}</p>}
      </div>
    </div>
  )
}
