import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext.jsx'

const base = 'http://localhost:8080/api'

export default function Orders() {
  const { token, role } = useAuth()
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)

  const headers = { Authorization: `Bearer ${token}` }

  useEffect(() => {
    fetchOrders()
  }, [])

  const fetchOrders = async () => {
    try {
      const res = await fetch(`${base}/user/orders`, { headers })
      if (res.ok) setOrders(await res.json())
    } catch (err) { console.error(err) }
    setLoading(false)
  }

  const formatTime = (time) => time ? new Date(time).toLocaleString() : 'N/A'

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h2>Past Orders</h2>
        <p>View your order history as a {role}</p>
      </div>

      <div className="auth-card" style={{ maxWidth: '1200px', margin: '0 auto' }}>
        {loading ? <p>Loading orders...</p> : (
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '2px solid #eee', textAlign: 'left' }}>
                <th style={{ padding: '1rem' }}>Product</th>
                <th style={{ padding: '1rem' }}>Role</th>
                <th style={{ padding: '1rem' }}>Seller Info</th>
                <th style={{ padding: '1rem' }}>Order Time</th>
                <th style={{ padding: '1rem' }}>Delivery Time</th>
                <th style={{ padding: '1rem' }}>Address</th>
                <th style={{ padding: '1rem' }}>Status</th>
              </tr>
            </thead>
            <tbody>
              {orders.map(o => (
                <tr key={o.orderId} style={{ borderBottom: '1px solid #eee' }}>
                  <td style={{ padding: '1rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                      {o.productImage && <img src={o.productImage} style={{ width: '40px', height: '40px', objectFit: 'cover' }} />}
                      <span>{o.productName}</span>
                    </div>
                  </td>
                  <td style={{ padding: '1rem' }}>{o.sellerType}</td>
                  <td style={{ padding: '1rem' }}>
                    <div>{o.sellerName}</div>
                    <div style={{ fontSize: '0.8rem', color: '#666' }}>{o.sellerPhone}</div>
                  </td>
                  <td style={{ padding: '1rem' }}>{formatTime(o.orderTime)}</td>
                  <td style={{ padding: '1rem' }}>{formatTime(o.deliveryTime)}</td>
                  <td style={{ padding: '1rem' }}>{o.deliveryAddress}</td>
                  <td style={{ padding: '1rem' }}>
                    <span style={{ 
                      padding: '0.2rem 0.5rem', 
                      borderRadius: '4px', 
                      background: o.status === 'PAID' ? '#dcfce7' : '#eee',
                      color: o.status === 'PAID' ? '#166534' : '#333'
                    }}>
                      {o.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        {!loading && orders.length === 0 && <p style={{ textAlign: 'center', padding: '2rem' }}>No orders found.</p>}
      </div>
    </div>
  )
}
