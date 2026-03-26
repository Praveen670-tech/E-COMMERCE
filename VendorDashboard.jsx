import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext.jsx'

const base = 'http://localhost:8080/api'

export default function VendorDashboard() {
  const { token } = useAuth()
  const [products, setProducts] = useState([])
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [price, setPrice] = useState('')
  const [discount, setDiscount] = useState('')
  const [stock, setStock] = useState('')
  const [files, setFiles] = useState([])
  const [status, setStatus] = useState('')

  const headers = token ? { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' }

  const fetchProducts = async () => {
    try {
      const res = await fetch(`${base}/vendor/products`, { headers })
      if (res.ok) {
        const data = await res.json()
        const productsWithImages = await Promise.all(data.map(async (p) => {
          const imgRes = await fetch(`${base}/user/products`, { headers }) // Re-use image logic or just wait for backend change
          return p;
        }))
        setProducts(data)
      }
    } catch (err) { console.error(err) }
  }

  const [sales, setSales] = useState([])
  const fetchSales = async () => {
    try {
      const res = await fetch(`${base}/vendor/orders`, { headers })
      if (res.ok) setSales(await res.json())
    } catch (err) { console.error(err) }
  }

  useEffect(() => {
    fetchProducts()
    fetchSales()
  }, [])

  const createProduct = async (e) => {
    e.preventDefault()
    setStatus('Creating product...')
    try {
      const res = await fetch(`${base}/vendor/product`, {
        method: 'POST',
        headers,
        body: JSON.stringify({ 
          name, 
          description, 
          price: parseFloat(price || 0), 
          discount: parseFloat(discount || 0),
          stock: parseInt(stock || 0, 10), 
          available: true 
        })
      })
      if (!res.ok) { setStatus(await res.text() || 'Failed'); return }
      const product = await res.json()
      
      if (files.length) {
        const form = new FormData()
        for (const f of files) form.append('files', f)
        const up = await fetch(`${base}/vendor/product/${product.productId}/images`, {
          method: 'POST',
          headers: token ? { Authorization: `Bearer ${token}` } : {},
          body: form
        })
        if (!up.ok) setStatus('Product created, image upload failed')
      }
      setStatus('Product created successfully')
      setName(''); setDescription(''); setPrice(''); setDiscount(''); setStock(''); setFiles([])
      fetchProducts()
    } catch (err) { setStatus('Error creating product') }
  }

  const toggleAvailability = async (product) => {
    try {
      const res = await fetch(`${base}/vendor/product/${product.productId}`, {
        method: 'PUT',
        headers,
        body: JSON.stringify({ ...product, available: !product.available })
      })
      if (res.ok) fetchProducts()
    } catch (err) { console.error(err) }
  }

  const updateStock = async (product, newStock) => {
    try {
      const res = await fetch(`${base}/vendor/product/${product.productId}`, {
        method: 'PUT',
        headers,
        body: JSON.stringify({ ...product, stock: parseInt(newStock, 10) })
      })
      if (res.ok) fetchProducts()
    } catch (err) { console.error(err) }
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h2>Vendor Dashboard</h2>
        <p>Manage your products, prices, and stock levels</p>
      </div>

      <div className="auth-card" style={{ maxWidth: '800px', margin: '0 auto 3rem' }}>
        <h3>Post New Product</h3>
        <form className="form" onSubmit={createProduct}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label>Product Name</label>
              <input value={name} onChange={e => setName(e.target.value)} required />
            </div>
            <div className="form-group">
              <label>Price ($)</label>
              <input type="number" step="0.01" value={price} onChange={e => setPrice(e.target.value)} required />
            </div>
            <div className="form-group">
              <label>Discount (%)</label>
              <input type="number" step="0.1" value={discount} onChange={e => setDiscount(e.target.value)} />
            </div>
          </div>
          <div className="form-group">
            <label>Description</label>
            <textarea 
              style={{ width: '100%', padding: '0.8rem', borderRadius: '8px', border: '1px solid #d1d5db' }}
              value={description} onChange={e => setDescription(e.target.value)} required 
            />
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label>Initial Stock Count</label>
              <input type="number" value={stock} onChange={e => setStock(e.target.value)} required />
            </div>
            <div className="form-group">
              <label>Product Images (Multiple)</label>
              <input type="file" multiple onChange={e => setFiles([...e.target.files])} />
            </div>
          </div>
          <button className="auth-submit" type="submit">Post Product</button>
        </form>
        {status && <div className="status" style={{ marginTop: '1rem', textAlign: 'center', color: status.includes('success') ? 'green' : 'red' }}>{status}</div>}
      </div>

      <div className="products-list" style={{ marginTop: '3rem' }}>
        <h3>Sales Performance</h3>
        <div className="auth-card" style={{ maxWidth: '1200px', margin: '1rem auto' }}>
          {sales.length === 0 ? (
            <p style={{ textAlign: 'center', padding: '2rem' }}>No sales recorded yet.</p>
          ) : (
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid #eee', textAlign: 'left' }}>
                  <th style={{ padding: '1rem' }}>Product</th>
                  <th style={{ padding: '1rem' }}>Buyer</th>
                  <th style={{ padding: '1rem' }}>Quantity</th>
                  <th style={{ padding: '1rem' }}>Total Amount</th>
                  <th style={{ padding: '1rem' }}>Date</th>
                  <th style={{ padding: '1rem' }}>Status</th>
                </tr>
              </thead>
              <tbody>
                {sales.map(s => (
                  <tr key={s.orderId} style={{ borderBottom: '1px solid #eee' }}>
                    <td style={{ padding: '1rem' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        {s.productImage && <img src={`http://localhost:8080/${s.productImage}`} style={{ width: '30px', height: '30px', borderRadius: '4px' }} />}
                        <span>{s.productName}</span>
                      </div>
                    </td>
                    <td style={{ padding: '1rem' }}>
                      <div>{s.buyerName}</div>
                      <div style={{ fontSize: '0.7rem', color: '#666' }}>{s.buyerPhone}</div>
                    </td>
                    <td style={{ padding: '1rem' }}>{s.quantity}</td>
                    <td style={{ padding: '1rem' }}>${s.totalAmount.toFixed(2)}</td>
                    <td style={{ padding: '1rem' }}>{new Date(s.orderTime).toLocaleDateString()}</td>
                    <td style={{ padding: '1rem' }}>
                      <span style={{ 
                        padding: '0.2rem 0.5rem', 
                        borderRadius: '4px', 
                        fontSize: '0.8rem',
                        background: s.status === 'PAID' ? '#dcfce7' : '#fef9c3',
                        color: s.status === 'PAID' ? '#166534' : '#854d0e'
                      }}>
                        {s.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      <div className="products-list" style={{ marginTop: '3rem' }}>
        <h3>Your Products</h3>
        <div className="product-grid">
          {products.map(p => (
            <div key={p.productId} className="product-card" style={{ opacity: p.available ? 1 : 0.6 }}>
              <div className="product-info">
                <h3>{p.name}</h3>
                <p>{p.description}</p>
                <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                  <div className="product-price" style={{ textDecoration: p.discount > 0 ? 'line-through' : 'none', fontSize: p.discount > 0 ? '0.9rem' : '1.2rem', color: p.discount > 0 ? '#999' : 'var(--primary-color)' }}>
                    ${p.price}
                  </div>
                  {p.discount > 0 && (
                    <div className="product-price" style={{ color: 'red' }}>
                      ${(p.price * (1 - p.discount / 100)).toFixed(2)}
                      <span style={{ fontSize: '0.7rem', marginLeft: '4px' }}>({p.discount}% OFF)</span>
                    </div>
                  )}
                </div>
                <div className="form-group">
                  <label>Stock: {p.stock}</label>
                  <input 
                    type="number" 
                    placeholder="Update stock"
                    onBlur={(e) => updateStock(p, e.target.value)}
                    style={{ padding: '0.4rem', width: '100px', marginLeft: '10px' }}
                  />
                </div>
                <div style={{ marginTop: '1rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ color: p.available ? 'green' : 'red', fontWeight: 'bold' }}>
                    {p.available ? '● Available' : '○ Unavailable'}
                  </span>
                  <button 
                    onClick={() => toggleAvailability(p)}
                    className="nav-button"
                    style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem' }}
                  >
                    Toggle Availability
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
