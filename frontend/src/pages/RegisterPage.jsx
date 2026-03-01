import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authApi } from '../api/auth'

export default function RegisterPage() {
  const [email,    setEmail]    = useState('')
  const [password, setPassword] = useState('')
  const [role,     setRole]     = useState('BUYER')
  const [error,    setError]    = useState('')
  const [loading,  setLoading]  = useState(false)

  const { login } = useAuth()
  const navigate  = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const data = await authApi.register({ email, password, role })
      login(data)
      navigate('/')
    } catch (err) {
      const fieldErrors = err.response?.data?.fieldErrors
      if (fieldErrors) {
        setError(Object.values(fieldErrors).join(', '))
      } else {
        setError(err.response?.data?.message ?? 'Registration failed')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page" style={styles.wrapper}>
      <div className="card" style={styles.card}>
        <h1 style={styles.title}>Create Account</h1>

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
              autoComplete="new-password"
              minLength={8}
              value={password}
              onChange={e => setPassword(e.target.value)}
            />
            <span style={styles.hint}>Min 8 chars, must include upper, lower, digit</span>
          </div>
          <div className="form-group">
            <label htmlFor="role">I want to</label>
            <select id="role" value={role} onChange={e => setRole(e.target.value)}>
              <option value="BUYER">Buy items</option>
              <option value="SELLER">Sell items</option>
            </select>
          </div>
          <button type="submit" className="btn btn-primary" style={styles.submitBtn} disabled={loading}>
            {loading ? 'Creating account...' : 'Register'}
          </button>
        </form>

        <p style={styles.footer}>
          Already have an account? <Link to="/login" style={{ color: '#1976d2' }}>Log in</Link>
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
  hint:      { fontSize: '0.8rem', color: '#9e9e9e' },
  footer:    { marginTop: '1.25rem', textAlign: 'center', fontSize: '0.9rem', color: '#616161' },
}
