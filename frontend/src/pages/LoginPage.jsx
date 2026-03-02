import { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useLocale } from '../context/LocaleContext'
import { authApi } from '../api/auth'
import { getErrorMessage } from '../utils/errorMessage'

export default function LoginPage() {
  const { t } = useLocale()
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
      setError(getErrorMessage(err, t.errors))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page" style={styles.wrapper}>
      <div className="card" style={styles.card}>
        <h1 style={styles.title}>{t.login.title}</h1>

        {error && <p className="error-message" style={{ marginBottom: '1rem' }}>{error}</p>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">{t.login.email}</label>
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
            <label htmlFor="password">{t.login.password}</label>
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
            {loading ? t.login.submitting : t.login.submit}
          </button>
        </form>

        <p style={styles.footer}>
          {t.login.noAccount}{' '}
          <Link to="/register" style={{ color: '#1976d2' }}>{t.login.registerLink}</Link>
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
