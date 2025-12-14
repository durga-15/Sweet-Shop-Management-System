import axios from 'axios'

/**
 * API SERVICE - Communication layer between frontend and backend
 * 
 * This file handles all HTTP requests to the backend server.
 * It includes:
 * - Authentication APIs (login, register)
 * - Sweet/product APIs (create, read, update, delete, search, purchase, restock)
 * 
 * The backend runs at: http://localhost:8081/api
 */

// Backend server URL
const API_BASE_URL = 'http://localhost:8081/api'

/**
 * Create axios instance with base configuration
 * axios = library for making HTTP requests
 */
const api = axios.create({
  baseURL: API_BASE_URL,  // All requests will be made to this URL
  headers: {
    'Content-Type': 'application/json',  // We're sending JSON data
  },
})

/**
 * INTERCEPTOR - Automatically add authentication token to every request
 * 
 * This runs before every HTTP request is sent to the backend.
 * It checks if the user is logged in (has a token in browser storage).
 * If they do, it includes the token with the request.
 * 
 * Without the token, the backend won't know who's making the request.
 */
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')  // Get stored token
  if (token) {
    // Add token to request header for authentication
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

/**
 * SWEET INTERFACE - Defines the structure of a sweet/product
 * 
 * This is what we send and receive for product data
 */
export interface Sweet {
  id?: number              // Product ID (optional when creating)
  name: string            // Product name (e.g., "Chocolate Cake")
  category: string        // Product category (e.g., "Cake")
  price: number           // Product price
  quantity: number        // How many in stock
}

/**
 * LOGIN REQUEST INTERFACE - What we send when logging in
 */
export interface LoginRequest {
  username: string
  password: string
}

/**
 * REGISTER REQUEST INTERFACE - What we send when creating an account
 */
export interface RegisterRequest {
  username: string
  email: string
  password: string
}

/**
 * AUTH RESPONSE INTERFACE - What we get back from login/register
 */
export interface AuthResponse {
  token: string          // Authentication ticket to use for future requests
  username: string       // User's login name
  email: string         // User's email
  role: 'USER' | 'ADMIN'  // Whether user is customer or shop manager
}

/**
 * AUTHENTICATION APIS - Handle login and registration
 */
export const authApi = {
  /**
   * LOGIN - Send credentials and get authentication token
   * Endpoint: POST /auth/login
   */
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', data)
    return response.data
  },

  /**
   * REGISTER - Create new customer account
   * Endpoint: POST /auth/register
   */
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/register', data)
    return response.data
  },

  /**
   * REGISTER ADMIN - Create new admin account
   * Endpoint: POST /auth/register-admin
   */
  registerAdmin: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/register-admin', data)
    return response.data
  },
}

/**
 * SWEET APIS - Handle all product operations
 */
export const sweetApi = {
  /**
   * GET ALL - Retrieve all sweets from the shop
   * Endpoint: GET /sweets
   */
  getAll: async (): Promise<Sweet[]> => {
    const response = await api.get<Sweet[]>('/sweets')
    return response.data
  },

  /**
   * GET BY ID - Retrieve a specific sweet by its ID
   * Endpoint: GET /sweets/{id}
   */
  getById: async (id: number): Promise<Sweet> => {
    const response = await api.get<Sweet>(`/sweets/${id}`)
    return response.data
  },

  /**
   * SEARCH - Find sweets by name, category, or price range
   * Endpoint: GET /sweets/search
   * Optional parameters: name, category, minPrice, maxPrice
   */
  search: async (params: {
    name?: string
    category?: string
    minPrice?: number
    maxPrice?: number
  }): Promise<Sweet[]> => {
    const response = await api.get<Sweet[]>('/sweets/search', { params })
    return response.data
  },

  /**
   * CREATE - Add a new sweet (admin only)
   * Endpoint: POST /sweets
   */
  create: async (sweet: Omit<Sweet, 'id'>): Promise<Sweet> => {
    const response = await api.post<Sweet>('/sweets', sweet)
    return response.data
  },

  /**
   * UPDATE - Modify a sweet's information (admin only)
   * Endpoint: PUT /sweets/{id}
   */
  update: async (id: number, sweet: Omit<Sweet, 'id'>): Promise<Sweet> => {
    const response = await api.put<Sweet>(`/sweets/${id}`, sweet)
    return response.data
  },

  /**
   * DELETE - Remove a sweet from the shop (admin only)
   * Endpoint: DELETE /sweets/{id}
   */
  delete: async (id: number): Promise<void> => {
    await api.delete(`/sweets/${id}`)
  },

  /**
   * PURCHASE - Buy a sweet (reduces stock)
   * Endpoint: POST /sweets/{id}/purchase
   * Sends: quantity (how many to buy)
   */
  purchase: async (id: number, quantity: number): Promise<Sweet> => {
    const response = await api.post<Sweet>(`/sweets/${id}/purchase`, { quantity })
    return response.data
  },

  /**
   * RESTOCK - Add more inventory (admin only)
   * Endpoint: POST /sweets/{id}/restock
   * Sends: quantity (how many to add)
   */
  restock: async (id: number, quantity: number): Promise<Sweet> => {
    const response = await api.post<Sweet>(`/sweets/${id}/restock`, { quantity })
    return response.data
  },
}

