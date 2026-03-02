import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useLocale } from '../context/LocaleContext'
import { authApi } from '../api/auth'
import { getErrorMessage } from '../utils/errorMessage'

export default function RegisterPage() {
  const { t } = useLocale()
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
      if (fieldErrors?.length) {
        setError(fieldErrors.map(fe => `${fe.field}: ${fe.message}`).join(', '))
      } else {
        setError(getErrorMessage(err, t.errors))
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page" style={styles.wrapper}>
      <div className="card" style={styles.card}>
        <h1 style={styles.title}>{t.register.title}</h1>

        {error && <p className="error-message" style={{ marginBottom: '1rem' }}>{error}</p>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">{t.register.email}</label>
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
            <label htmlFor="password">{t.register.password}</label>
            <input
              id="password"
              type="password"
              required
              autoComplete="new-password"
              minLength={8}
              value={password}
              onChange={e => setPassword(e.target.value)}
            />
            <span style={styles.hint}>{t.register.passwordHint}</span>
          </div>
          <div className="form-group">
            <label htmlFor="role">{t.register.role}</label>
            <select id="role" value={role} onChange={e => setRole(e.target.value)}>
              <option value="BUYER">{t.register.buyItems}</option>
              <option value="SELLER">{t.register.sellItems}</option>
            </select>
          </div>
          <button type="submit" className="btn btn-primary" style={styles.submitBtn} disabled={loading}>
            {loading ? t.register.submitting : t.register.submit}
          </button>
        </form>

        <p style={styles.footer}>
          {t.register.hasAccount}{' '}
          <Link to="/login" style={{ color: '#1976d2' }}>{t.register.loginLink}</Link>
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
