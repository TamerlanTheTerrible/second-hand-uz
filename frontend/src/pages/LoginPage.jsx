import { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authApi } from '../api/auth'

export default function LoginPage() {
  const [email,    setEmail]    = useState('')
  const [password, setPassword] = useState('')
  const [error,    setError]    = useState('')
  const [loading,  setLoading]  = useState(false)

  const { login } = useAuth()
  const navigate  = useNavigate()
  const location  = useLocation()
  const from = location.state?.from?.pathname ?? '/'

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const data = await authApi.login({ email, password })
      login(data)
      navigate(from, { replace: true })
    } catch (err) {
      setError(err.response?.data?.message ?? 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page" style={styles.wrapper}>
      <div className="card" style={styles.card}>
        <h1 style={styles.title}>Log In</h1>

        {error && <p className="error-message" style={{ marginBottom: '1rem' }}>{error}</p>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              required
              autoComplete="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              required
              autoComplete="current-password"
              value={password}
              onChange={e => setPassword(e.target.value)}
            />
          </div>
          <button type="submit" className="btn btn-primary" style={styles.submitBtn} disabled={loading}>
            {loading ? 'Logging in...' : 'Log In'}
          </button>
        </form>

        <p style={styles.footer}>
          Don't have an account? <Link to="/register" style={{ color: '#1976d2' }}>Register</Link>
        </p>
      </div>
    </div>
  )
}

const styles = {
  wrapper:   { display: 'flex', justifyContent: 'center', alignItems: 'flex-start', paddingTop: '4rem' },
  card:      { width: '100%', maxWidth: '400px', padding: '2rem' },
  title:     { fontSize: '1.5rem', fontWeight: 700, marginBottom: '1.5rem', textAlign: 'center' },
  submitBtn: { width: '100%', marginTop: '0.5rem' },
  footer:    { marginTop: '1.25rem', textAlign: 'center', fontSize: '0.9rem', color: '#616161' },
}
