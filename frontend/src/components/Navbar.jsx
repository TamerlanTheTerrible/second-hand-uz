import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useLocale } from '../context/LocaleContext'

const LANGS = [
  { code: 'uz', label: "O'Z" },
  { code: 'ru', label: 'РУ' },
  { code: 'en', label: 'EN' },
]

export default function Navbar() {
  const { user, logout, isAuthenticated } = useAuth()
  const { locale, changeLocale, t } = useLocale()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/')
  }

  return (
    <nav style={styles.nav}>
      <div className="container" style={styles.inner}>
        <Link to="/" style={styles.brand}>Second Hand UZ</Link>

        <div style={styles.links}>
          <Link to="/" style={styles.link}>{t.nav.browse}</Link>

          {isAuthenticated ? (
            <>
              <Link to="/listings/new" style={styles.link}>{t.nav.sell}</Link>
              <Link to="/orders" style={styles.link}>{t.nav.orders}</Link>
              <Link to={`/profile/${user.id}`} style={styles.link}>
                {user.email}
              </Link>
              <button onClick={handleLogout} style={styles.logoutBtn}>
                {t.nav.logout}
              </button>
            </>
          ) : (
            <>
              <Link to="/login" style={styles.link}>{t.nav.login}</Link>
              <Link to="/register" className="btn btn-primary" style={{ padding: '0.4rem 1rem' }}>
                {t.nav.register}
              </Link>
            </>
          )}

          <div style={styles.langSwitcher}>
            {LANGS.map(l => (
              <button
                key={l.code}
                onClick={() => changeLocale(l.code)}
                style={{ ...styles.langBtn, ...(locale === l.code ? styles.langBtnActive : {}) }}
              >
                {l.label}
              </button>
            ))}
          </div>
        </div>
      </div>
    </nav>
  )
}

const styles = {
  nav: {
    background: '#1976d2',
    color: '#fff',
    height: '64px',
    position: 'sticky',
    top: 0,
    zIndex: 100,
    boxShadow: '0 2px 4px rgba(0,0,0,0.2)',
  },
  inner: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    height: '100%',
  },
  brand: {
    fontWeight: 700,
    fontSize: '1.25rem',
    color: '#fff',
  },
  links: {
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
  },
  link: {
    color: 'rgba(255,255,255,0.9)',
    fontSize: '0.95rem',
  },
  logoutBtn: {
    background: 'rgba(255,255,255,0.15)',
    border: '1px solid rgba(255,255,255,0.4)',
    color: '#fff',
    borderRadius: '6px',
    padding: '0.35rem 0.85rem',
    fontSize: '0.9rem',
  },
  langSwitcher: {
    display: 'flex',
    gap: '2px',
    marginLeft: '0.5rem',
    background: 'rgba(255,255,255,0.15)',
    borderRadius: '6px',
    padding: '2px',
  },
  langBtn: {
    background: 'transparent',
    border: 'none',
    color: 'rgba(255,255,255,0.75)',
    borderRadius: '4px',
    padding: '0.2rem 0.5rem',
    fontSize: '0.8rem',
    fontWeight: 600,
    cursor: 'pointer',
    letterSpacing: '0.5px',
  },
  langBtnActive: {
    background: 'rgba(255,255,255,0.9)',
    color: '#1976d2',
  },
}
