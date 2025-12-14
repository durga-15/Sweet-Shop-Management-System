import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authApi } from '../services/api'
import './Auth.css'

/**
 * REGISTER COMPONENT - The sign-up page for new users and admins
 * 
 * This page allows users to create new accounts.
 * They can choose to register as:
 * - Regular customer (USER) - browses and purchases sweets
 * - Shop manager (ADMIN) - manages inventory and product list
 * 
 * Flow:
 * 1. User enters username, email, password
 * 2. Check if they want to be admin or customer
 * 3. Submit to backend
 * 4. Backend creates account and returns token
 * 5. Store token and go to dashboard or admin panel
 */
const Register = () => {
  // State for form inputs
  const [username, setUsername] = useState('')      // What user types for username
  const [email, setEmail] = useState('')            // What user types for email
  const [password, setPassword] = useState('')      // What user types for password
  const [isAdmin, setIsAdmin] = useState(false)     // Whether to register as admin
  const [error, setError] = useState('')            // Error messages to show user
  const [loading, setLoading] = useState(false)     // Show loading while submitting

  // Get the login function from authentication context
  const { login } = useAuth()
  // Get navigate function to redirect after registration
  const navigate = useNavigate()

  /**
   * HANDLE SUBMIT - Process the registration form
   * 
   * Steps:
   * 1. Prevent default form submission
   * 2. Clear any previous errors
   * 3. Determine if registering as admin or customer
   * 4. Send registration request to backend
   * 5. If successful, save token and go to appropriate page
   * 6. If failed, show error message
   */
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()  // Prevent page reload
    setError('')        // Clear previous errors
    setLoading(true)    // Show loading state

    try {
      let response
      
      // Check if registering as admin or customer
      if (isAdmin) {
        // Register as ADMIN (shop manager)
        response = await authApi.registerAdmin({ username, email, password })
        // Go to admin panel after registration
        navigate('/admin')
      } else {
        // Register as USER (customer)
        response = await authApi.register({ username, email, password })
        // Go to dashboard after registration
        navigate('/dashboard')
      }
      
      // Save the token and user info from response
      login(response.token, {
        username: response.username,
        email: response.email,
        role: response.role,
      })
    } catch (err: any) {
      // Show error message from backend or generic message
      setError(err.response?.data?.error || 'Registration failed. Please try again.')
    } finally {
      setLoading(false)  // Hide loading state
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Sweet Shop Registration</h2>
        
        {/* Registration form */}
        <form onSubmit={handleSubmit}>
          {/* Username input */}
          <div className="form-group">
            <label>Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}  // Update state as user types
              required
              minLength={3}
              placeholder="Enter your username (min 3 characters)"
            />
          </div>

          {/* Email input */}
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}  // Update state as user types
              required
              placeholder="Enter your email"
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
              minLength={6}
              placeholder="Enter your password (min 6 characters)"
            />
          </div>

          {/* Checkbox: Register as Admin */}
          <div className="form-group checkbox">
            <label>
              <input
                type="checkbox"
                checked={isAdmin}
                onChange={(e) => setIsAdmin(e.target.checked)}  // Toggle admin registration
              />
              <span>Register as Admin (Shop Manager)</span>
            </label>
            {isAdmin && <p className="admin-note">Admins can add and manage products</p>}
          </div>

          {/* Show error message if registration failed */}
          {error && <div className="error-message">{error}</div>}

          {/* Submit button */}
          <button type="submit" disabled={loading} className="auth-button">
            {loading ? 'Registering...' : 'Register'}
          </button>

          {/* Link to login page for existing users */}
          <p className="auth-link">
            Already have an account? <Link to="/login">Login here</Link>
          </p>
        </form>
      </div>
    </div>
  )
}

export default Register

