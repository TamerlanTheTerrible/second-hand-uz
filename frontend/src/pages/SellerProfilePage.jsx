import { useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { authApi } from '../api/auth'
import { listingsApi } from '../api/listings'
import { reviewsApi } from '../api/reviews'
import ListingCard from '../components/ListingCard'

export default function SellerProfilePage() {
  const { id } = useParams()

  const { data: seller,   isLoading: sellerLoading }   = useQuery({
    queryKey: ['user', id],
    queryFn: () => authApi.getUser(id),
  })
  const { data: listings, isLoading: listingsLoading } = useQuery({
    queryKey: ['listings', 'seller', id],
    queryFn: () => listingsApi.getBySeller(id),
  })
  const { data: reviews,  isLoading: reviewsLoading }  = useQuery({
    queryKey: ['reviews', 'seller', id],
    queryFn: () => reviewsApi.getSellerReviews(id),
  })

  if (sellerLoading) return <div className="spinner">Loading...</div>

  return (
    <div className="page">
      <div className="container">
        {/* Profile header */}
        <div className="card" style={styles.header}>
          <div style={styles.avatar}>{seller?.email?.[0]?.toUpperCase() ?? '?'}</div>
          <div>
            <h1 style={styles.email}>{seller?.email}</h1>
            <p style={styles.role}>{seller?.role}</p>
            <p style={styles.rating}>
              Rating: <strong>{seller?.averageRating?.toFixed(1) ?? 'N/A'}</strong>
              {seller?.totalReviews != null && ` (${seller.totalReviews} reviews)`}
            </p>
          </div>
        </div>

        {/* Listings */}
        <h2 style={styles.sectionTitle}>Listings</h2>
        {listingsLoading ? (
          <div className="spinner">Loading...</div>
        ) : listings?.length ? (
          <div className="grid" style={{ marginBottom: '2rem' }}>
            {listings.filter(l => l.status === 'ACTIVE').map(l => (
              <ListingCard key={l.id} listing={l} />
            ))}
          </div>
        ) : (
          <p style={styles.empty}>No active listings.</p>
        )}

        {/* Reviews */}
        <h2 style={styles.sectionTitle}>Reviews</h2>
        {reviewsLoading ? (
          <div className="spinner">Loading...</div>
        ) : reviews?.length ? (
          <div style={styles.reviewList}>
            {reviews.map(r => (
              <div key={r.id} className="card" style={styles.reviewCard}>
                <div style={styles.reviewHeader}>
                  <span style={styles.stars}>{'★'.repeat(r.rating)}{'☆'.repeat(5 - r.rating)}</span>
                  <span style={styles.reviewDate}>{new Date(r.createdAt).toLocaleDateString()}</span>
                </div>
                {r.comment && <p style={styles.reviewComment}>{r.comment}</p>}
              </div>
            ))}
          </div>
        ) : (
          <p style={styles.empty}>No reviews yet.</p>
        )}
      </div>
    </div>
  )
}

const styles = {
  header:        { display: 'flex', alignItems: 'center', gap: '1.5rem', padding: '1.5rem', marginBottom: '2rem' },
  avatar:        { width: '72px', height: '72px', borderRadius: '50%', background: '#1976d2', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '2rem', fontWeight: 700, flexShrink: 0 },
  email:         { fontSize: '1.25rem', fontWeight: 700 },
  role:          { color: '#757575', fontSize: '0.875rem', marginTop: '0.2rem' },
  rating:        { fontSize: '0.95rem', marginTop: '0.25rem' },
  sectionTitle:  { fontSize: '1.25rem', fontWeight: 700, marginBottom: '1rem' },
  empty:         { color: '#9e9e9e', marginBottom: '2rem' },
  reviewList:    { display: 'flex', flexDirection: 'column', gap: '0.75rem' },
  reviewCard:    { padding: '1rem' },
  reviewHeader:  { display: 'flex', justifyContent: 'space-between', marginBottom: '0.4rem' },
  stars:         { color: '#fbc02d', fontSize: '1.1rem', letterSpacing: '2px' },
  reviewDate:    { color: '#9e9e9e', fontSize: '0.8rem' },
  reviewComment: { color: '#424242', lineHeight: 1.6 },
}
