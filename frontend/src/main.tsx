import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import './index.css'

/**
 * APPLICATION ENTRY POINT - Where the React app starts
 * 
 * This file:
 * 1. Imports the main App component
 * 2. Finds the HTML element with id="root" (in index.html)
 * 3. Renders the entire React application into that element
 * 4. Wraps app in StrictMode for development checks
 * 
 * When you run the app, this is the starting point.
 */

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    {/* Main App component with all routing and auth logic */}
    <App />
  </React.StrictMode>,
)

