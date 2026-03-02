import { Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import Navbar from './components/Navbar'
import ProtectedRoute from './components/ProtectedRoute'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ListingDetailPage from './pages/ListingDetailPage'
import CreateListingPage from './pages/CreateListingPage'
import OrdersPage from './pages/OrdersPage'
import SellerProfilePage from './pages/SellerProfilePage'
import MockPaymentPage from './pages/MockPaymentPage'

export default function App() {
  return (
    <AuthProvider>
      <Navbar />
      <Routes>
        <Route path="/"                   element={<HomePage />} />
        <Route path="/login"              element={<LoginPage />} />
        <Route path="/register"           element={<RegisterPage />} />
        <Route path="/listings/:id"       element={<ListingDetailPage />} />
        <Route path="/profile/:id"        element={<SellerProfilePage />} />
        <Route path="/listings/new"       element={
          <ProtectedRoute><CreateListingPage /></ProtectedRoute>
        } />
        <Route path="/orders"             element={
          <ProtectedRoute><OrdersPage /></ProtectedRoute>
        } />
        <Route path="/mock-payment/:orderId" element={
          <ProtectedRoute><MockPaymentPage /></ProtectedRoute>
        } />
      </Routes>
    </AuthProvider>
  )
}
