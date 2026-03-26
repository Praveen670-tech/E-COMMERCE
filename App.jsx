import './App.css'
import { BrowserRouter, Route, Routes, Navigate } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import { AuthProvider, useAuth } from './context/AuthContext.jsx'
import Login from './pages/Login.jsx'
import Signup from './pages/Signup.jsx'
import Home from './pages/Home.jsx'
import UserDashboard from './pages/UserDashboard.jsx'
import VendorDashboard from './pages/VendorDashboard.jsx'
import AdminDashboard from './pages/AdminDashboard.jsx'
import Settings from './pages/Settings.jsx'
import Orders from './pages/Orders.jsx'

function Protected({ children, allow }) {
  const { token, role } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  if (allow && allow.length && !allow.includes(role)) return <Navigate to="/" replace />
  return children
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navbar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/user" element={<Protected allow={['USER']}><UserDashboard /></Protected>} />
          <Route path="/vendor" element={<Protected allow={['VENDOR']}><VendorDashboard /></Protected>} />
          <Route path="/admin" element={<Protected allow={['ADMIN']}><AdminDashboard /></Protected>} />
          <Route path="/settings" element={<Protected><Settings /></Protected>} />
          <Route path="/orders" element={<Protected><Orders /></Protected>} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
