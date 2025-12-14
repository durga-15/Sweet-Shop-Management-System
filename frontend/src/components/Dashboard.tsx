import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import { sweetApi, Sweet } from '../services/api'
import './Dashboard.css'

/**
 * DASHBOARD COMPONENT - Main shopping/management interface
 * 
 * This is the main page after login. It shows:
 * 
 * FOR CUSTOMERS:
 * - Browse all available sweets
 * - Search by name or category
 * - View price and quantity details
 * - Purchase sweets (buy items)
 * - View their profile
 * - Logout
 * 
 * FOR ADMINS (Shop Managers):
 * - All customer features PLUS:
 * - Add new sweets to the shop
 * - Edit sweet information
 * - Delete sweets
 * - Restock items (increase quantity)
 * 
 * This component handles:
 * - Loading and displaying sweets
 * - Searching and filtering
 * - Creating, editing, deleting sweets (admin only)
 * - Purchasing and restocking sweets
 */
const Dashboard = () => {
  // Get current user and logout function
  const { user, logout } = useAuth()
  // Get navigation function for page redirects
  const navigate = useNavigate()

  // ========== STATE VARIABLES ==========
  // Main data
  const [sweets, setSweets] = useState<Sweet[]>([])        // List of all sweets
  
  // UI states
  const [loading, setLoading] = useState(true)             // Show loading spinner
  const [error, setError] = useState('')                   // Error messages
  const [searchName, setSearchName] = useState('')         // Search by sweet name
  const [searchCategory, setSearchCategory] = useState('')  // Search by category
  const [showAddModal, setShowAddModal] = useState(false)   // Show add sweet dialog
  const [showEditModal, setShowEditModal] = useState(false) // Show edit sweet dialog
  const [editingSweet, setEditingSweet] = useState<Sweet | null>(null)  // Sweet being edited
  const [purchasingId, setPurchasingId] = useState<number | null>(null)  // Sweet being purchased
  const [restockingId, setRestockingId] = useState<number | null>(null)   // Sweet being restocked

  // Form for new/edited sweet
  const [newSweet, setNewSweet] = useState({
    name: '',
    category: '',
    price: '',
    quantity: '',
  })

  // Check if logged-in user is admin
  const isAdmin = user?.role === 'ADMIN'

  /**
   * LOAD INITIAL DATA - Fetch sweets when page loads
   */
  useEffect(() => {
    loadSweets()
  }, [])

  /**
   * LOAD SWEETS - Get all sweets from backend
   */
  const loadSweets = async () => {
    try {
      setLoading(true)      // Show loading state
      setError('')          // Clear previous errors
      const data = await sweetApi.getAll()  // Fetch from API
      setSweets(data)       // Store in state
    } catch (err: any) {
      setError('Failed to load sweets. Please try again.')
    } finally {
      setLoading(false)     // Hide loading state
    }
  }

  /**
   * HANDLE SEARCH - Filter sweets by name and/or category
   */
  const handleSearch = async () => {
    try {
      setLoading(true)
      setError('')
      // Only include non-empty search parameters
      const params: any = {}
      if (searchName.trim() !== '') params.name = searchName.trim()
      if (searchCategory.trim() !== '') params.category = searchCategory.trim()
      console.log('Search params:', params)
      const data = await sweetApi.search(params)
      console.log('Search result:', data)
      setSweets(data)
      setError('')
    } catch (err: any) {
      console.error('Search error:', err)
      console.error('Error response:', err.response?.data)
      const errorMsg = err.response?.data?.error || err.message || 'Search failed. Please try again.'
      setError(`Search failed: ${errorMsg}`)
    } finally {
      setLoading(false)
    }
  }

  const handlePurchase = async (id: number) => {
    try {
      setPurchasingId(id)
      await sweetApi.purchase(id, 1)
      // Silently refresh the item without showing loading state
      const updatedSweet = await sweetApi.getById(id)
      setSweets(prevSweets => 
        prevSweets.map(sweet => sweet.id === id ? updatedSweet : sweet)
      )
    } catch (err: any) {
      alert(err.response?.data?.error || 'Purchase failed')
    } finally {
      setPurchasingId(null)
    }
  }

  const handleRestock = async (id: number) => {
    const quantity = prompt('Enter quantity to restock:')
    if (!quantity || isNaN(Number(quantity)) || Number(quantity) <= 0) {
      alert('Please enter a valid quantity')
      return
    }
    try {
      setRestockingId(id)
      await sweetApi.restock(id, Number(quantity))
      // Silently refresh the item without showing loading state
      const updatedSweet = await sweetApi.getById(id)
      setSweets(prevSweets => 
        prevSweets.map(sweet => sweet.id === id ? updatedSweet : sweet)
      )
    } catch (err: any) {
      alert(err.response?.data?.error || 'Restock failed')
    } finally {
      setRestockingId(null)
    }
  }

  const handleAddSweet = async () => {
    try {
      await sweetApi.create({
        name: newSweet.name,
        category: newSweet.category,
        price: Number(newSweet.price),
        quantity: Number(newSweet.quantity),
      })
      setShowAddModal(false)
      setNewSweet({ name: '', category: '', price: '', quantity: '' })
      await loadSweets()
    } catch (err: any) {
      alert(err.response?.data?.error || 'Failed to add sweet')
    }
  }

  const handleUpdateSweet = async () => {
    if (!editingSweet?.id) return
    try {
      await sweetApi.update(editingSweet.id, {
        name: editingSweet.name,
        category: editingSweet.category,
        price: editingSweet.price,
        quantity: editingSweet.quantity,
      })
      setShowEditModal(false)
      setEditingSweet(null)
      await loadSweets()
    } catch (err: any) {
      alert(err.response?.data?.error || 'Failed to update sweet')
    }
  }

  const handleDeleteSweet = async (id: number) => {
    if (!confirm('Are you sure you want to delete this sweet?')) return
    try {
      await sweetApi.delete(id)
      await loadSweets()
    } catch (err: any) {
      alert(err.response?.data?.error || 'Failed to delete sweet')
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>🍬 Sweet Shop Management System</h1>
        <div className="header-actions">
          <span className="user-info">Welcome, {user?.username} ({user?.role})</span>
          {isAdmin && (
            <button className="btn-admin-panel" onClick={() => navigate('/admin')}>
              Go to Admin Panel
            </button>
          )}
          {isAdmin && (
            <button className="btn-primary" onClick={() => setShowAddModal(true)}>
              Add New Sweet
            </button>
          )}
          <button className="btn-secondary" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <div className="dashboard-content">
        <div className="search-section">
          <input
            type="text"
            placeholder="Search by name..."
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
            className="search-input"
          />
          <input
            type="text"
            placeholder="Search by category..."
            value={searchCategory}
            onChange={(e) => setSearchCategory(e.target.value)}
            className="search-input"
          />
          <button onClick={handleSearch} className="btn-primary">
            Search
          </button>
          <button onClick={loadSweets} className="btn-secondary">
            Reset
          </button>
        </div>

        {error && <div className="error-banner">{error}</div>}

        {loading ? (
          <div className="loading">Loading sweets...</div>
        ) : (
          <div className="sweets-grid">
            {sweets.map((sweet) => (
              <div key={sweet.id} className="sweet-card">
                <h3>{sweet.name}</h3>
                <p className="category">{sweet.category}</p>
                <p className="price">₹{sweet.price.toFixed(2)}</p>
                <p className={`quantity ${sweet.quantity === 0 ? 'out-of-stock' : ''}`}>
                  {sweet.quantity === 0 ? 'Out of Stock' : `Stock: ${sweet.quantity}`}
                </p>
                <div className="sweet-actions">
                  <button
                    onClick={() => handlePurchase(sweet.id!)}
                    disabled={sweet.quantity === 0 || purchasingId === sweet.id}
                    className="btn-purchase"
                  >
                    {purchasingId === sweet.id ? 'Purchasing...' : 'Purchase'}
                  </button>
                  {isAdmin && (
                    <>
                      <button
                        onClick={() => {
                          setEditingSweet(sweet)
                          setShowEditModal(true)
                        }}
                        className="btn-edit"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleRestock(sweet.id!)}
                        disabled={restockingId === sweet.id}
                        className="btn-restock"
                      >
                        {restockingId === sweet.id ? 'Restocking...' : 'Restock'}
                      </button>
                      <button
                        onClick={() => handleDeleteSweet(sweet.id!)}
                        className="btn-delete"
                      >
                        Delete
                      </button>
                    </>
                  )}
                </div>
              </div>
            ))}
            {sweets.length === 0 && (
              <div className="no-sweets">No sweets found. {isAdmin && 'Add some sweets to get started!'}</div>
            )}
          </div>
        )}
      </div>

      {showAddModal && (
        <div className="modal">
          <div className="modal-content">
            <h2>Add New Sweet</h2>
            <input
              type="text"
              placeholder="Name"
              value={newSweet.name}
              onChange={(e) => setNewSweet({ ...newSweet, name: e.target.value })}
            />
            <input
              type="text"
              placeholder="Category"
              value={newSweet.category}
              onChange={(e) => setNewSweet({ ...newSweet, category: e.target.value })}
            />
            <input
              type="number"
              placeholder="Price"
              value={newSweet.price}
              onChange={(e) => setNewSweet({ ...newSweet, price: e.target.value })}
              step="0.01"
            />
            <input
              type="number"
              placeholder="Quantity"
              value={newSweet.quantity}
              onChange={(e) => setNewSweet({ ...newSweet, quantity: e.target.value })}
            />
            <div className="modal-actions">
              <button onClick={handleAddSweet} className="btn-primary">
                Add
              </button>
              <button onClick={() => setShowAddModal(false)} className="btn-secondary">
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {showEditModal && editingSweet && (
        <div className="modal">
          <div className="modal-content">
            <h2>Edit Sweet</h2>
            <input
              type="text"
              placeholder="Name"
              value={editingSweet.name}
              onChange={(e) => setEditingSweet({ ...editingSweet, name: e.target.value })}
            />
            <input
              type="text"
              placeholder="Category"
              value={editingSweet.category}
              onChange={(e) => setEditingSweet({ ...editingSweet, category: e.target.value })}
            />
            <input
              type="number"
              placeholder="Price"
              value={editingSweet.price}
              onChange={(e) => setEditingSweet({ ...editingSweet, price: Number(e.target.value) })}
              step="0.01"
            />
            <input
              type="number"
              placeholder="Quantity"
              value={editingSweet.quantity}
              onChange={(e) => setEditingSweet({ ...editingSweet, quantity: Number(e.target.value) })}
            />
            <div className="modal-actions">
              <button onClick={handleUpdateSweet} className="btn-primary">
                Update
              </button>
              <button
                onClick={() => {
                  setShowEditModal(false)
                  setEditingSweet(null)
                }}
                className="btn-secondary"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Dashboard

