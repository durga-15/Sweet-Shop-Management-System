import React, { createContext, useState, useContext, useEffect } from 'react'
import { authApi } from '../services/api'

/**
 * USER INTERFACE - Defines what data is stored about each logged-in user
 * 
 * username: The user's login name
 * email: The user's email address
 * role: Either 'USER' (customer) or 'ADMIN' (shop manager)
 */
interface User {
  username: string
  email: string
  role: 'USER' | 'ADMIN'
}

/**
 * AUTH CONTEXT TYPE - Defines all the authentication-related data and functions
 * 
 * token: JWT authentication token (like a session ticket)
 * user: The currently logged-in user's information
 * login: Function to log in a user
 * logout: Function to log out a user
 */
interface AuthContextType {
  token: string | null
  user: User | null
  login: (token: string, user: User) => void
  logout: () => void
}

// Create the authentication context
// This context will be used throughout the app to share authentication state
const AuthContext = createContext<AuthContextType | undefined>(undefined)

/**
 * AUTH PROVIDER - Wraps the entire app and provides authentication state
 * 
 * This component:
 * - Manages who is logged in
 * - Stores authentication token and user info in browser storage
 * - Provides login/logout functions to all components
 * 
 * Wrap your app with this: <AuthProvider>{children}</AuthProvider>
 */
export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Get stored token from browser's localStorage, or null if not logged in
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'))
  
  // Get stored user info from browser's localStorage, or null if not logged in
  const [user, setUser] = useState<User | null>(() => {
    const storedUser = localStorage.getItem('user')
    return storedUser ? JSON.parse(storedUser) : null
  })

  // Whenever token changes, save it to browser's localStorage
  // This keeps the user logged in even if they refresh the page
  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token)  // Save token to browser storage
    } else {
      localStorage.removeItem('token')  // Remove token when logged out
    }
  }, [token])

  // Whenever user info changes, save it to browser's localStorage
  // This keeps the user info when they refresh the page
  useEffect(() => {
    if (user) {
      localStorage.setItem('user', JSON.stringify(user))  // Save user to browser storage
    } else {
      localStorage.removeItem('user')  // Remove user info when logged out
    }
  }, [user])

  /**
   * LOGIN - Set the user as logged in
   * 
   * Called after successful authentication
   * Stores token and user info so they stay logged in
   */
  const login = (newToken: string, newUser: User) => {
    setToken(newToken)  // Store the authentication token
    setUser(newUser)    // Store the user's information
  }

  /**
   * LOGOUT - Clear all user data and log them out
   * 
   * Called when user clicks logout
   * Removes token and user info from memory and browser storage
   */
  const logout = () => {
    setToken(null)  // Clear the token
    setUser(null)   // Clear user info
    localStorage.removeItem('token')  // Remove from browser storage
    localStorage.removeItem('user')   // Remove from browser storage
  }

  // Provide authentication state and functions to entire app
  return (
    <AuthContext.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

/**
 * USE AUTH HOOK - Get authentication data in any component
 * 
 * Usage in components:
 *   const { user, token, login, logout } = useAuth()
 * 
 * This lets components access:
 * - user: Who is logged in
 * - token: Authentication ticket
 * - login: Function to log in
 * - logout: Function to log out
 */
export const useAuth = () => {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

