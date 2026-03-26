import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext.jsx'

const base = 'http://localhost:8080/api'

export default function AdminDashboard() {
  const { token } = useAuth()
  const [users, setUsers] = useState([])
  const [vendors, setVendors] = useState([])
  const [purchases, setPurchases] = useState([])
  const [search, setSearch] = useState('')
  const [tab, setTab] = useState('users')

  const headers = token ? { Authorization: `Bearer ${token}` } : {}

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    try {
      const [uRes, vRes, pRes] = await Promise.all([
        fetch(`${base}/admin/users`, { headers }),
        fetch(`${base}/admin/vendors`, { headers }),
        fetch(`${base}/admin/purchases`, { headers })
      ])
      if (uRes.ok) setUsers(await uRes.json())
      if (vRes.ok) setVendors(await vRes.json())
      if (pRes.ok) setPurchases(await pRes.json())
    } catch (err) { console.error(err) }
  }

  const formatTime = (timeStr) => {
    if (!timeStr) return 'Never'
    return new Date(timeStr).toLocaleString()
  }

  const exportToCSV = () => {
    let data = []
    let filename = ''
    if (tab === 'users') {
      data = users.map(u => ({ Name: u.name, Email: u.email, Phone: u.phoneNumber, Address: u.address, LastLogin: u.lastLogin }))
      filename = 'users.csv'
    } else if (tab === 'vendors') {
      data = vendors.map(v => ({ Name: v.name, Email: v.email, License: v.licenseNumber, LastLogin: v.lastLogin }))
      filename = 'vendors.csv'
    } else {
      data = purchases.map(p => ({ Time: p.orderTime, Buyer: p.buyerName, Product: p.productName, Amount: p.totalAmount, Status: p.status }))
      filename = 'transactions.csv'
    }

    const csvContent = "data:text/csv;charset=utf-8," 
      + [Object.keys(data[0]).join(","), ...data.map(row => Object.values(row).join(","))].join("\n")
    
    const encodedUri = encodeURI(csvContent)
    const link = document.createElement("a")
    link.setAttribute("href", encodedUri)
    link.setAttribute("download", filename)
    document.body.appendChild(link)
    link.click()
  }

  const printTable = () => {
    window.print()
  }

  const filteredUsers = users.filter(u => u.name.toLowerCase().includes(search.toLowerCase()) || u.email.toLowerCase().includes(search.toLowerCase()))
  const filteredVendors = vendors.filter(v => v.name.toLowerCase().includes(search.toLowerCase()) || v.email.toLowerCase().includes(search.toLowerCase()))

  return (
    <div className="dashboard-container">
      <div className="dashboard-header no-print">
        <h2>Admin Dashboard</h2>
        <p>Monitor system activity and manage marketplace records</p>
      </div>

      <div className="hero-btns no-print" style={{ marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <button className={tab === 'users' ? 'btn-white' : 'btn-primary'} onClick={() => setTab('users')}>Users</button>
          <button className={tab === 'vendors' ? 'btn-white' : 'btn-primary'} onClick={() => setTab('vendors')}>Vendors</button>
          <button className={tab === 'purchases' ? 'btn-white' : 'btn-primary'} onClick={() => setTab('purchases')}>Transaction History</button>
        </div>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <button className="nav-button" onClick={exportToCSV} style={{ background: '#10b981', color: 'white' }}>Download CSV</button>
          <button className="nav-button" onClick={printTable} style={{ background: '#6366f1', color: 'white' }}>Print Records</button>
        </div>
      </div>

      <div className="search-bar no-print" style={{ marginBottom: '2rem' }}>
        <input 
          placeholder={`Search ${tab}...`} 
          value={search} 
          onChange={e => setSearch(e.target.value)}
          style={{ width: '100%', padding: '1rem', borderRadius: '12px', border: '1px solid #e5e7eb' }}
        />
      </div>

      <div className="auth-card" style={{ maxWidth: '1200px', margin: '0 auto' }}>
        {tab === 'users' && (
          <div className="printable-content">
            <h3>User List</h3>
            <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '1rem' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid #eee', textAlign: 'left' }}>
                  <th style={{ padding: '1rem' }}>Name</th>
                  <th style={{ padding: '1rem' }}>Email</th>
                  <th style={{ padding: '1rem' }}>Phone</th>
                  <th style={{ padding: '1rem' }}>Last Login</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map(u => (
                  <tr key={u.userId} style={{ borderBottom: '1px solid #eee' }}>
                    <td style={{ padding: '1rem' }}>{u.name}</td>
                    <td style={{ padding: '1rem' }}>{u.email}</td>
                    <td style={{ padding: '1rem' }}>{u.phoneNumber}</td>
                    <td style={{ padding: '1rem' }}>{formatTime(u.lastLogin)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {tab === 'vendors' && (
          <div className="printable-content">
            <h3>Vendor List</h3>
            <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '1rem' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid #eee', textAlign: 'left' }}>
                  <th style={{ padding: '1rem' }}>Business Name</th>
                  <th style={{ padding: '1rem' }}>Email</th>
                  <th style={{ padding: '1rem' }}>License</th>
                  <th style={{ padding: '1rem' }}>Last Login</th>
                </tr>
              </thead>
              <tbody>
                {filteredVendors.map(v => (
                  <tr key={v.vendorId} style={{ borderBottom: '1px solid #eee' }}>
                    <td style={{ padding: '1rem' }}>{v.name}</td>
                    <td style={{ padding: '1rem' }}>{v.email}</td>
                    <td style={{ padding: '1rem' }}>{v.licenseNumber}</td>
                    <td style={{ padding: '1rem' }}>{formatTime(v.lastLogin)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {tab === 'purchases' && (
          <div className="printable-content">
            <h3>Transaction History</h3>
            <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '1rem' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid #eee', textAlign: 'left' }}>
                  <th style={{ padding: '1rem' }}>Time</th>
                  <th style={{ padding: '1rem' }}>Buyer</th>
                  <th style={{ padding: '1rem' }}>Product</th>
                  <th style={{ padding: '1rem' }}>Seller</th>
                  <th style={{ padding: '1rem' }}>Amount</th>
                  <th style={{ padding: '1rem' }}>Status</th>
                </tr>
              </thead>
              <tbody>
                {purchases.map(p => (
                  <tr key={p.orderId} style={{ borderBottom: '1px solid #eee' }}>
                    <td style={{ padding: '1rem' }}>{formatTime(p.orderTime)}</td>
                    <td style={{ padding: '1rem' }}>{p.buyerName} ({p.buyerType})</td>
                    <td style={{ padding: '1rem' }}>{p.productName}</td>
                    <td style={{ padding: '1rem' }}>{p.sellerName} ({p.sellerType})</td>
                    <td style={{ padding: '1rem' }}>${p.totalAmount.toFixed(2)}</td>
                    <td style={{ padding: '1rem' }}>{p.status}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
      <style>{`
        @media print {
          .no-print { display: none !important; }
          .printable-content { display: block !important; }
          body { background: white !important; }
          .auth-card { box-shadow: none !important; border: none !important; }
        }
      `}</style>
    </div>
  )
}
