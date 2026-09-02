import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const ROLE_HOME = {
  APPLICANT: '/applicant',
  ANALYST: '/analyst',
  ADMIN: '/admin'
}

// Same intent as the backend's validation: a plausible email shape, and a
// name made up of letters (any language) and spaces only - no digits or
// symbols. Client-side checks give instant feedback; the backend still
// re-validates everything, so this is a UX nicety, not the source of truth.
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const NAME_PATTERN = /^[\p{L} ]+$/u

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [fullName, setFullName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [role, setRole] = useState('APPLICANT')
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
  const [loading, setLoading] = useState(false)

  function validate() {
    const errors = {}
    if (!NAME_PATTERN.test(fullName.trim())) {
      errors.fullName = 'Full name may only contain letters and spaces'
    }
    if (!EMAIL_PATTERN.test(email.trim())) {
      errors.email = 'Enter a valid email address'
    }
    return errors
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    const errors = validate()
    setFieldErrors(errors)
    if (Object.keys(errors).length > 0) {
      return
    }

    setLoading(true)
    try {
      const data = await register(email, password, fullName, role)
      navigate(ROLE_HOME[data.role] || '/')
    } catch (err) {
      const message = err.response?.data?.error || err.response?.data?.password || 'Registration failed'
      setError(message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="brand">Credit Scoring</div>
        <div className="brand-sub">Sandbox · Sign up</div>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div className="field">
            <label htmlFor="fullName">Full name</label>
            <input
              id="fullName"
              type="text"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              required
            />
            {fieldErrors.fullName && <div className="error-text">{fieldErrors.fullName}</div>}
          </div>
          <div className="field">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            {fieldErrors.email && <div className="error-text">{fieldErrors.email}</div>}
          </div>
          <div className="field">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              minLength={6}
              required
            />
          </div>
          <div className="field">
            <label htmlFor="role">Role (for demo purposes)</label>
            <select id="role" value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="APPLICANT">Applicant</option>
              <option value="ANALYST">Analyst</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>
          <button className="btn btn-primary btn-block" type="submit" disabled={loading}>
            {loading ? 'Signing up…' : 'Sign up'}
          </button>
        </form>

        <p style={{ marginTop: 16, fontSize: 13 }}>
          Already have an account? <Link to="/login">Log in</Link>
        </p>
      </div>
    </div>
  )
}
