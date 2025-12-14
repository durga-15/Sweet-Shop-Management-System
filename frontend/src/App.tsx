import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Login from './components/Login'
import Register from './components/Register'
import Dashboard from './components/Dashboard'
import AdminPanel from './components/AdminPanel'
import './App.css'

/**
 * PROTECTED ROUTE - Allows access only to logged-in users
 * 
 * If user is NOT logged in (no token):
 *   - Redirect to login page
 * 
 * If user IS logged in:
 *   - Show the requested page
 */
function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { token } = useAuth()  // Check if user has authentication token
  // If has token, show page; otherwise redirect to login
  return token ? <>{children}</> : <Navigate to="/login" />
}

/**
 * ADMIN ROUTE - Allows access only to admin users
 * 
 * If user is NOT logged in:
 *   - Redirect to dashboard
 * 
 * If user IS logged in but NOT admin:
 *   - Redirect to dashboard
 * 
 * If user IS admin:
 *   - Show the admin page
 */
function AdminRoute({ children }: { children: React.ReactNode }) {
  const { token, user } = useAuth()  // Check authentication and role
  // If has token AND is admin, show page; otherwise redirect to dashboard
  return token && user?.role === 'ADMIN' ? <>{children}</> : <Navigate to="/dashboard" />
}

/**
 * MAIN APP COMPONENT - Application routing and setup
 * 
 * This component:
 * 1. Wraps the app with AuthProvider (makes authentication available everywhere)
 * 2. Sets up all routes (URL paths)
 * 3. Protects pages so only appropriate users can access them
 * 
 * ROUTES:
 * - /login: Login page (public)
 * - /register: Registration page (public)
 * - /dashboard: Main shopping page (logged-in users only)
 * - /admin: Admin shop manager page (admins only)
 * - /: Default route (redirects to /dashboard)
 */
function App() {
  return (
    <AuthProvider>  {/* Make authentication available to all components */}
      <Router>  {/* Enable routing and URL changes */}
        <div className="App">
          <Routes>  {/* Define all application routes */}
            
            {/* PUBLIC ROUTES - Anyone can access */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            {/* PROTECTED ROUTE - Only logged-in users */}
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <Dashboard />
                </ProtectedRoute>
              }
            />

            {/* ADMIN ROUTE - Only admins */}
            <Route
              path="/admin"
              element={
                <AdminRoute>
                  <AdminPanel />
                </AdminRoute>
              }
            />

            {/* DEFAULT ROUTE - Redirect to dashboard */}
            <Route path="/" element={<Navigate to="/dashboard" />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  )
}

export default App

