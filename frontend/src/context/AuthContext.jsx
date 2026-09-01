import { createContext, useContext, useState } from 'react'
import api from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user')
    return stored ? JSON.parse(stored) : null
  })

  function persistSession(authResponse) {
    localStorage.setItem('token', authResponse.token)
    const sessionUser = {
      email: authResponse.email,
      fullName: authResponse.fullName,
      role: authResponse.role
    }
    localStorage.setItem('user', JSON.stringify(sessionUser))
    setUser(sessionUser)
  }

  async function login(email, password) {
    const { data } = await api.post('/auth/login', { email, password })
    persistSession(data)
    return data
  }

  async function register(email, password, fullName, role) {
    const { data } = await api.post('/auth/register', { email, password, fullName, role })
    persistSession(data)
    return data
  }

  function logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
