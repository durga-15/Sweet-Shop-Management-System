import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import { useNavigate, Navigate } from 'react-router-dom'
import { sweetApi, Sweet } from '../services/api'
import './AdminPanel.css'

/**
 * ADMIN PANEL COMPONENT - Shop manager interface
 * 
 * This is the exclusive interface for admins (shop managers).
 * Only users with ADMIN role can access this page.
 * 
 * Features:
 * - VIEW TAB: See all sweets in inventory
 * - ADD TAB: Create new sweet products
 * - SEARCH TAB: Find sweets by criteria
 * - Edit existing sweets
 * - Delete sweets from the shop
 * - Restock items (increase inventory)
 * 
 * Non-admins are automatically redirected to dashboard.
 */
const AdminPanel = () => {
  // Get current user and logout function
  const { user, logout } = useAuth()
  // Get navigation function for page redirects
  const navigate = useNavigate()

  // ========== STATE VARIABLES ==========
  // Main data
  const [sweets, setSweets] = useState<Sweet[]>([])      // All sweets in inventory
  
  // UI states
  const [loading, setLoading] = useState(true)           // Show loading spinner
  const [error, setError] = useState('')                 // Error messages
  const [success, setSuccess] = useState('')             // Success messages
  const [activeTab, setActiveTab] = useState<'view' | 'add' | 'search'>('view')  // Currently active tab
  
  // Edit modal states
  const [editingSweet, setEditingSweet] = useState<Sweet | null>(null)  // Sweet being edited
  const [showEditModal, setShowEditModal] = useState(false)             // Show/hide edit dialog

  // Form for adding new sweet
  const [formData, setFormData] = useState({
    name: '',
    category: '',
    price: '',
    quantity: '',
  })

  // Form for searching sweets
  const [searchData, setSearchData] = useState({
    name: '',
    category: '',
    minPrice: '',
    maxPrice: '',
  })

  /**
   * VERIFY ADMIN & LOAD DATA - Runs when component loads
   */
  useEffect(() => {
    // Check if user is admin - if not, redirect to dashboard
    if (user?.role !== 'ADMIN') {
      navigate('/dashboard')  // Redirect non-admins
    }
    loadSweets()  // Load sweets data
  }, [user, navigate])

  /**
   * LOAD SWEETS - Get all sweets from backend
   */
  const loadSweets = async () => {
    try {
      setLoading(true)      // Show loading state
      const data = await sweetApi.getAll()  // Fetch all sweets
      setSweets(data)       // Store in state
      setError('')          // Clear errors
    } catch (err: any) {
      setError('Failed to load sweets')  // Show error message
    } finally {
      setLoading(false)     // Hide loading state
    }
  }

  /**
   * HANDLE ADD SWEET - Create a new sweet product
   */
  const handleAddSweet = async (e: React.FormEvent) => {
    e.preventDefault()  // Prevent form default submission
    
    // Validate all fields are filled
    if (!formData.name || !formData.category || !formData.price || formData.quantity === '') {
      setError('All fields are required')
      return
    }

    try {
      await sweetApi.create({
        name: formData.name,
        category: formData.category,
        price: Number(formData.price),
        quantity: Number(formData.quantity),
      })
      setSuccess('Sweet added successfully!')
      setFormData({ name: '', category: '', price: '', quantity: '' })
      setActiveTab('view')
      await loadSweets()
      setTimeout(() => setSuccess(''), 3000)
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to add sweet')
    }
  }

  const handleEditSweet = (sweet: Sweet) => {
    setEditingSweet(sweet)
    setFormData({
      name: sweet.name,
      category: sweet.category,
      price: sweet.price.toString(),
      quantity: sweet.quantity.toString(),
    })
    setShowEditModal(true)
  }

  const handleUpdateSweet = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!editingSweet?.id) return

    try {
      await sweetApi.update(editingSweet.id, {
        name: formData.name,
        category: formData.category,
        price: Number(formData.price),
        quantity: Number(formData.quantity),
      })
      setSuccess('Sweet updated successfully!')
      setShowEditModal(false)
      setEditingSweet(null)
      setFormData({ name: '', category: '', price: '', quantity: '' })
      await loadSweets()
      setTimeout(() => setSuccess(''), 3000)
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to update sweet')
    }
  }

  const handleDeleteSweet = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this sweet?')) return

    try {
      await sweetApi.delete(id)
      setSuccess('Sweet deleted successfully!')
      await loadSweets()
      setTimeout(() => setSuccess(''), 3000)
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to delete sweet')
    }
  }

  const handleRestock = async (id: number) => {
    const quantity = prompt('Enter quantity to restock:')
    if (!quantity || isNaN(Number(quantity)) || Number(quantity) <= 0) {
      setError('Please enter a valid quantity')
      return
    }

    try {
      await sweetApi.restock(id, Number(quantity))
      setSuccess('Sweet restocked successfully!')
      await loadSweets()
      setTimeout(() => setSuccess(''), 3000)
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to restock sweet')
    }
  }

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      setLoading(true)
      const data = await sweetApi.search({
        name: searchData.name || undefined,
        category: searchData.category || undefined,
        minPrice: searchData.minPrice ? Number(searchData.minPrice) : undefined,
        maxPrice: searchData.maxPrice ? Number(searchData.maxPrice) : undefined,
      })
      setSweets(data)
      setError('')
    } catch (err: any) {
      setError('Search failed')
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  if (user?.role !== 'ADMIN') {
    return <Navigate to="/dashboard" />
  }

  return (
    <div className="admin-panel">
      <header className="admin-header">
        <h1>👨‍💼 Admin Panel</h1>
        <div className="admin-header-actions">
          <span className="admin-info">Welcome, {user?.username}</span>
          <button className="btn-logout" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <nav className="admin-nav">
        <button
          className={`nav-btn ${activeTab === 'view' ? 'active' : ''}`}
          onClick={() => {
            setActiveTab('view')
            loadSweets()
          }}
        >
          View Sweets
        </button>
        <button
          className={`nav-btn ${activeTab === 'add' ? 'active' : ''}`}
          onClick={() => setActiveTab('add')}
        >
          Add Sweet
        </button>
        <button
          className={`nav-btn ${activeTab === 'search' ? 'active' : ''}`}
          onClick={() => setActiveTab('search')}
        >
          Search Sweets
        </button>
      </nav>

      <div className="admin-content">
        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        {/* View Sweets Tab */}
        {activeTab === 'view' && (
          <div className="tab-content">
            <h2>All Sweets</h2>
            {loading ? (
              <div className="loading">Loading sweets...</div>
            ) : sweets.length === 0 ? (
              <div className="no-data">No sweets available. Add some sweets to get started!</div>
            ) : (
              <div className="sweets-table-container">
                <table className="sweets-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Name</th>
                      <th>Category</th>
                      <th>Price (₹)</th>
                      <th>Quantity</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {sweets.map((sweet) => (
                      <tr key={sweet.id}>
                        <td>{sweet.id}</td>
                        <td>{sweet.name}</td>
                        <td>{sweet.category}</td>
                        <td>{sweet.price.toFixed(2)}</td>
                        <td>
                          <span className={`qty-badge ${sweet.quantity === 0 ? 'out-of-stock' : ''}`}>
                            {sweet.quantity}
                          </span>
                        </td>
                        <td className="action-buttons">
                          <button
                            className="btn-edit"
                            onClick={() => handleEditSweet(sweet)}
                          >
                            Edit
                          </button>
                          <button
                            className="btn-restock"
                            onClick={() => handleRestock(sweet.id!)}
                          >
                            Restock
                          </button>
                          <button
                            className="btn-delete"
                            onClick={() => handleDeleteSweet(sweet.id!)}
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {/* Add Sweet Tab */}
        {activeTab === 'add' && (
          <div className="tab-content">
            <h2>Add New Sweet</h2>
            <form onSubmit={handleAddSweet} className="admin-form">
              <div className="form-group">
                <label>Sweet Name *</label>
                <input
                  type="text"
                  placeholder="e.g., Gulab Jamun"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label>Category *</label>
                <input
                  type="text"
                  placeholder="e.g., Indian, Western, Cake"
                  value={formData.category}
                  onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                  required
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Price (₹) *</label>
                  <input
                    type="number"
                    placeholder="0.00"
                    step="0.01"
                    value={formData.price}
                    onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Quantity *</label>
                  <input
                    type="number"
                    placeholder="0"
                    value={formData.quantity}
                    onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                    required
                  />
                </div>
              </div>

              <button type="submit" className="btn-submit">
                Add Sweet
              </button>
            </form>
          </div>
        )}

        {/* Search Tab */}
        {activeTab === 'search' && (
          <div className="tab-content">
            <h2>Search Sweets</h2>
            <form onSubmit={handleSearch} className="admin-form">
              <div className="form-group">
                <label>Sweet Name</label>
                <input
                  type="text"
                  placeholder="Search by name..."
                  value={searchData.name}
                  onChange={(e) => setSearchData({ ...searchData, name: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label>Category</label>
                <input
                  type="text"
                  placeholder="Search by category..."
                  value={searchData.category}
                  onChange={(e) => setSearchData({ ...searchData, category: e.target.value })}
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Min Price (₹)</label>
                  <input
                    type="number"
                    placeholder="0"
                    step="0.01"
                    value={searchData.minPrice}
                    onChange={(e) => setSearchData({ ...searchData, minPrice: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>Max Price (₹)</label>
                  <input
                    type="number"
                    placeholder="9999"
                    step="0.01"
                    value={searchData.maxPrice}
                    onChange={(e) => setSearchData({ ...searchData, maxPrice: e.target.value })}
                  />
                </div>
              </div>

              <button type="submit" className="btn-submit">
                Search
              </button>
            </form>
          </div>
        )}
      </div>

      {/* Edit Modal */}
      {showEditModal && editingSweet && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>Edit Sweet</h2>
              <button
                className="modal-close"
                onClick={() => {
                  setShowEditModal(false)
                  setEditingSweet(null)
                }}
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleUpdateSweet} className="admin-form">
              <div className="form-group">
                <label>Sweet Name *</label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label>Category *</label>
                <input
                  type="text"
                  value={formData.category}
                  onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                  required
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Price (₹) *</label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.price}
                    onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Quantity *</label>
                  <input
                    type="number"
                    value={formData.quantity}
                    onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="modal-actions">
                <button type="submit" className="btn-submit">
                  Update Sweet
                </button>
                <button
                  type="button"
                  className="btn-cancel"
                  onClick={() => {
                    setShowEditModal(false)
                    setEditingSweet(null)
                  }}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default AdminPanel
