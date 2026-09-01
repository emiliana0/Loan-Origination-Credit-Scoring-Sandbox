import { NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const ROLE_LABELS = {
  APPLICANT: 'Applicant',
  ANALYST: 'Analyst',
  ADMIN: 'Admin'
}

export default function Sidebar() {
  const { user, logout } = useAuth()

  return (
    <aside className="sidebar">
      <div className="brand">
        Credit Scoring
        <span>Sandbox</span>
      </div>

      <nav>
        {user?.role === 'APPLICANT' && (
          <NavLink to="/applicant" className={({ isActive }) => (isActive ? 'active' : '')}>
            My applications
          </NavLink>
        )}
        {user?.role === 'ANALYST' && (
          <NavLink to="/analyst" className={({ isActive }) => (isActive ? 'active' : '')}>
            Application queue
          </NavLink>
        )}
        {user?.role === 'ADMIN' && (
          <NavLink to="/admin" className={({ isActive }) => (isActive ? 'active' : '')}>
            Scoring rules
          </NavLink>
        )}
      </nav>

      {user && (
        <div className="user-box">
          <div>{user.fullName}</div>
          <div className="role-tag">{ROLE_LABELS[user.role] || user.role}</div>
          <div style={{ marginTop: 12 }}>
            <button className="nav-link" onClick={logout}>Log out</button>
          </div>
        </div>
      )}
    </aside>
  )
}
