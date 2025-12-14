import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authApi } from '../services/api'
import './Auth.css'

/**
 * LOGIN COMPONENT - The login page for users
 * 
 * This page allows users to sign in with their username and password.
 * After successful login, they go to the dashboard.
 * 
 * Flow:
 * 1. User enters username and password
 * 2. Submit to backend
 * 3. Backend returns authentication token
 * 4. Store token and go to dashboard
 */
const Login = () => {
  // State for form inputs
  const [username, setUsername] = useState('')  // What user types for username
  const [password, setPassword] = useState('')  // What user types for password
  const [error, setError] = useState('')        // Error messages to show user
  const [loading, setLoading] = useState(false) // Show loading while submitting

  // Get the login function from authentication context
  const { login } = useAuth()
  // Get navigate function to redirect after login
  const navigate = useNavigate()

  /**
   * HANDLE SUBMIT - Process the login form
   * 
   * Steps:
   * 1. Prevent default form submission
   * 2. Clear any previous errors
   * 3. Send username/password to backend
   * 4. If successful, save token and go to dashboard
   * 5. If failed, show error message
   */
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()  // Prevent page reload
    setError('')        // Clear previous errors
    setLoading(true)    // Show loading state

    try {
      // Send login request to backend API
      const response = await authApi.login({ username, password })
      
      // If successful, save the token and user info
      login(response.token, {
        username: response.username,
        email: response.email,
        role: response.role,
      })
      
      // Redirect to dashboard (main page)
      navigate('/dashboard')
    } catch (err: any) {
      // Show error message from backend or generic message
      setError(err.response?.data?.error || 'Login failed. Please try again.')
    } finally {
      setLoading(false)  // Hide loading state
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Sweet Shop Login</h2>
        
        {/* Show error message if login failed */}
        {error && <div className="error-message">{error}</div>}
        
        {/* Login form */}
        <form onSubmit={handleSubmit}>
          {/* Username input */}
          <div className="form-group">
            <label>Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}  // Update state as user types
              required
              placeholder="Enter your username"
            />
          </div>

          {/* Password input */}
          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}  // Update state as user types
              required
              placeholder="Enter your password"
            />
          </div>

          {/* Submit button */}
          <button type="submit" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        {/* Link to registration page for new users */}
        <p>
          Don't have an account? <Link to="/register">Register here</Link>
        </p>
      </div>
    </div>
  )
}

export default Login

