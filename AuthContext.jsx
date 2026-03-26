import { createContext, useContext, useEffect, useState } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem('token') || '')
  const [role, setRole] = useState(localStorage.getItem('role') || '')
  useEffect(() => {
    if (token) localStorage.setItem('token', token); else localStorage.removeItem('token')
    if (role) localStorage.setItem('role', role); else localStorage.removeItem('role')
  }, [token, role])
  const logout = () => { setToken(''); setRole('') }

  const login = async (email, password) => {
    const res = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    })
    if (!res.ok) throw new Error('Login failed')
    const data = await res.json()
    setToken(data.token)
    
    try {
      const payload = JSON.parse(atob(data.token.split('.')[1]))
      setRole(payload.role || 'USER')
    } catch (e) {
      setRole('USER')
    }
    
    return data
  }

  return <AuthContext.Provider value={{ token, setToken, role, setRole, logout, login }}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}
