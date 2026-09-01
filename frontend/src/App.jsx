import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import Sidebar from './components/Sidebar'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ApplicantDashboard from './pages/ApplicantDashboard'
import AnalystDashboard from './pages/AnalystDashboard'
import AdminDashboard from './pages/AdminDashboard'

const ROLE_HOME = {
  APPLICANT: '/applicant',
  ANALYST: '/analyst',
  ADMIN: '/admin'
}

function HomeRedirect() {
  const { user } = useAuth()
  if (!user) return <Navigate to="/login" replace />
  return <Navigate to={ROLE_HOME[user.role] || '/login'} replace />
}

function AppLayout({ children }) {
  return (
    <div className="app-shell">
      <Sidebar />
      <main className="main-content">{children}</main>
    </div>
  )
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route
        path="/applicant"
        element={
          <ProtectedRoute allowedRoles={['APPLICANT']}>
            <AppLayout><ApplicantDashboard /></AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/analyst"
        element={
          <ProtectedRoute allowedRoles={['ANALYST']}>
            <AppLayout><AnalystDashboard /></AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin"
        element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <AppLayout><AdminDashboard /></AppLayout>
          </ProtectedRoute>
        }
      />

      <Route path="/" element={<HomeRedirect />} />
      <Route path="*" element={<HomeRedirect />} />
    </Routes>
  )
}
