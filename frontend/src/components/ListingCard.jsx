import { Link } from 'react-router-dom'
import { useLocale } from '../context/LocaleContext'

export default function ListingCard({ listing }) {
  const { t } = useLocale()
  const { id, title, price, condition, gender, category, status, imageUrls } = listing
  const thumbnail = imageUrls && imageUrls.length > 0 ? imageUrls[0] : null

  return (
    <Link to={`/listings/${id}`} className="card" style={styles.card}>
      <div style={styles.imageWrapper}>
        {thumbnail ? (
          <img src={thumbnail} alt={title} style={styles.image} />
        ) : (
          <div style={styles.placeholder}>{t.listing.noImage}</div>
        )}
        <span className={`badge badge-${status?.toLowerCase()}`} style={styles.badge}>
          {status}
        </span>
      </div>
      <div style={styles.body}>
        <p style={styles.title}>{title}</p>
        <p style={styles.price}>{Number(price).toLocaleString()} UZS</p>
        <div style={styles.meta}>
          {category  && <span style={styles.categoryTag}>{t.categoryLabels[category] ?? category.replace(/_/g, ' ')}</span>}
          {condition && <span>{t.conditionLabels[condition] ?? condition.replace('_', ' ')}</span>}
          {gender    && <span style={styles.genderTag}>{t.genderLabels[gender] ?? gender}</span>}
        </div>
      </div>
    </Link>
  )
}

const styles = {
  card: {
    display: 'block',
    transition: 'transform 0.15s, box-shadow 0.15s',
    cursor: 'pointer',
  },
  imageWrapper: {
    position: 'relative',
    paddingTop: '75%',
    background: '#f0f0f0',
    overflow: 'hidden',
  },
  image: {
    position: 'absolute',
    top: 0, left: 0,
    width: '100%',
    height: '100%',
    objectFit: 'cover',
  },
  placeholder: {
    position: 'absolute',
    top: 0, left: 0,
    width: '100%',
    height: '100%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    color: '#9e9e9e',
    fontSize: '0.85rem',
  },
  badge: {
    position: 'absolute',
    top: '0.5rem',
    right: '0.5rem',
  },
  body: {
    padding: '0.75rem',
  },
  title: {
    fontWeight: 600,
    fontSize: '0.95rem',
    marginBottom: '0.25rem',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
  },
  price: {
    color: '#1976d2',
    fontWeight: 700,
    fontSize: '1rem',
    marginBottom: '0.2rem',
  },
  meta: {
    display: 'flex',
    gap: '0.4rem',
    flexWrap: 'wrap',
    color: '#757575',
    fontSize: '0.8rem',
  },
  categoryTag: {
    background: '#f3e5f5',
    color: '#6a1b9a',
    borderRadius: '4px',
    padding: '0 4px',
  },
  genderTag: {
    background: '#e3f2fd',
    color: '#1565c0',
    borderRadius: '4px',
    padding: '0 4px',
  },
}
