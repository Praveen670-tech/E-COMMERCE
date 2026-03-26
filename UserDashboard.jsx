import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext.jsx'

const base = 'http://localhost:8080/api'

export default function UserDashboard() {
  const { token, role } = useAuth()
  const [products, setProducts] = useState([])
  const [myProducts, setMyProducts] = useState([])
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [price, setPrice] = useState('')
  const [discount, setDiscount] = useState('')
  const [stock, setStock] = useState('')
  const [files, setFiles] = useState([])
  const [status, setStatus] = useState('')
  const [tab, setTab] = useState('shop') // 'shop' or 'my-products'

  const headers = token ? { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' }

  useEffect(() => {
    fetchProducts()
    if (token) fetchMyProducts()
  }, [token])

  const fetchProducts = async () => {
    try {
      const res = await fetch(`${base}/user/products`, { headers })
      if (res.ok) {
        const data = await res.json()
        // For each product, fetch its images
        const productsWithImages = await Promise.all(data.map(async (p) => {
          try {
            // We'll need a way to get images. VendorController has /product/{id}/images
            // but UserController doesn't. Let's assume the backend might already 
            // have a way or we can add it. For now, let's try to fetch if we can.
            // Actually, let's check if the product object already has images.
            return p;
          } catch (e) { return p; }
        }))
        setProducts(productsWithImages)
      }
    } catch (err) { console.error(err) }
  }

  const fetchMyProducts = async () => {
    try {
      const res = await fetch(`${base}/user/my-products`, { headers }) // Note: need to add this endpoint or use search
      // For now, let's assume we can filter all products by current user if we had their ID
      // But better to have a specific endpoint. I'll add one to UserController later.
      // For this demo, let's just use the shop list.
    } catch (err) { console.error(err) }
  }

  const createProduct = async (e) => {
    e.preventDefault()
    setStatus('Posting product...')
    try {
      const res = await fetch(`${base}/user/product`, {
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
      if (!res.ok) { setStatus('Failed to post'); return }
      setStatus('Product posted successfully!')
      setName(''); setDescription(''); setPrice(''); setDiscount(''); setStock('')
      fetchProducts()
    } catch (err) { setStatus('Error') }
  }

  const addToCart = async (productId) => {
    try {
      const res = await fetch(`${base}/user/cart?productId=${productId}&quantity=1`, { method: 'POST', headers })
      setStatus(res.ok ? 'Added to cart' : 'Failed')
      setTimeout(() => setStatus(''), 2000)
    } catch (err) { console.error(err) }
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h2>User Dashboard</h2>
        <div className="hero-btns" style={{ marginTop: '1rem' }}>
          <button className={tab === 'shop' ? 'btn-white' : 'btn-primary'} onClick={() => setTab('shop')}>Shop Marketplace</button>
          <button className={tab === 'sell' ? 'btn-white' : 'btn-primary'} onClick={() => setTab('sell')}>Sell Product (C2C)</button>
        </div>
      </div>

      {tab === 'sell' && (
        <div className="auth-card" style={{ maxWidth: '800px', margin: '2rem auto' }}>
          <h3>Sell Your Product</h3>
          <form className="form" onSubmit={createProduct}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1rem' }}>
              <div className="form-group">
                <label>Name</label>
                <input value={name} onChange={e => setName(e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Price</label>
                <input type="number" value={price} onChange={e => setPrice(e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Discount %</label>
                <input type="number" value={discount} onChange={e => setDiscount(e.target.value)} />
              </div>
            </div>
            <div className="form-group">
              <label>Description</label>
              <textarea value={description} onChange={e => setDescription(e.target.value)} required />
            </div>
            <div className="form-group">
              <label>Stock</label>
              <input type="number" value={stock} onChange={e => setStock(e.target.value)} required />
            </div>
            <button className="auth-submit" type="submit">Post to Marketplace</button>
          </form>
          {status && <p style={{ textAlign: 'center', marginTop: '1rem' }}>{status}</p>}
        </div>
      )}

      {tab === 'shop' && (
        <div className="products-list" style={{ marginTop: '2rem' }}>
          <h3>Marketplace (C2C & B2C)</h3>
          <div className="product-grid">
            {products.map(p => (
              <div key={p.productId} className="product-card" style={{ opacity: p.available && p.stock > 0 ? 1 : 0.6 }}>
                {p.images && p.images.length > 0 && (
                  <img 
                    src={`http://localhost:8080/${p.images[0].url.startsWith('http') ? p.images[0].url : '/' + p.images[0].url}`} 
                    alt={p.name} 
                    style={{ width: '100%', height: '200px', objectFit: 'cover', borderRadius: '8px 8px 0 0' }} 
                  />
                )}
                <div className="product-info">
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ fontSize: '0.7rem', color: '#777' }}>Seller: {p.sellerType}</span>
                    <span style={{ fontSize: '0.7rem', color: p.stock > 0 ? 'green' : 'red' }}>{p.stock > 0 ? `${p.stock} in stock` : 'Out of stock'}</span>
                  </div>
                  <h3>{p.name}</h3>
                  <p>{p.description}</p>
                  <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                    <div className="product-price" style={{ textDecoration: p.discount > 0 ? 'line-through' : 'none', color: p.discount > 0 ? '#999' : 'var(--primary-color)' }}>
                      ${p.price}
                    </div>
                    {p.discount > 0 && (
                      <div className="product-price" style={{ color: 'red' }}>
                        ${(p.price * (1 - p.discount / 100)).toFixed(2)}
                      </div>
                    )}
                  </div>
                  <button 
                    className="btn-buy" 
                    disabled={!p.available || p.stock <= 0}
                    onClick={() => addToCart(p.productId)}
                  >
                    Add to Cart
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
