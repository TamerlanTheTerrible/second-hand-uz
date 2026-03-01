import { useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { listingsApi } from '../api/listings'
import { ordersApi } from '../api/orders'
import { useAuth } from '../context/AuthContext'

export default function ListingDetailPage() {
  const { id }      = useParams()
  const navigate    = useNavigate()
  const { user, isAuthenticated } = useAuth()
  const queryClient = useQueryClient()
  const [activeImg, setActiveImg] = useState(0)
  const [orderMsg,  setOrderMsg]  = useState('')

  const { data: listing, isLoading, isError } = useQuery({
    queryKey: ['listing', id],
    queryFn: () => listingsApi.getById(id),
  })

  const orderMutation = useMutation({
    mutationFn: () => ordersApi.create({ listingId: Number(id) }),
    onSuccess: (order) => {
      setOrderMsg('Order created! Redirecting to payment...')
      ordersApi.createPaymentSession(order.id)
        .then(session => { window.location.href = session.paymentUrl })
        .catch(() => { navigate('/orders') })
    },
    onError: (err) => {
      setOrderMsg(err.response?.data?.message ?? 'Failed to create order')
    },
  })

  const deleteMutation = useMutation({
    mutationFn: () => listingsApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['listings'] })
      navigate('/')
    },
  })

  if (isLoading) return <div className="spinner">Loading...</div>
  if (isError)   return <p style={{ color: '#d32f2f', padding: '2rem' }}>Listing not found.</p>

  const isOwner   = user?.id === listing.sellerId
  const canOrder  = isAuthenticated && !isOwner && listing.status === 'ACTIVE'

  return (
    <div className="page">
      <div className="container">
        <div style={styles.layout}>
          {/* Image gallery */}
          <div style={styles.gallery}>
            <div style={styles.mainImgWrapper}>
              {listing.imageUrls?.length > 0 ? (
                <img
                  src={listing.imageUrls[activeImg]}
                  alt={listing.title}
                  style={styles.mainImg}
                />
              ) : (
                <div style={styles.noImg}>No image</div>
              )}
            </div>
            {listing.imageUrls?.length > 1 && (
              <div style={styles.thumbs}>
                {listing.imageUrls.map((url, i) => (
                  <img
                    key={i}
                    src={url}
                    alt=""
                    style={{ ...styles.thumb, outline: i === activeImg ? '2px solid #1976d2' : 'none' }}
                    onClick={() => setActiveImg(i)}
                  />
                ))}
              </div>
            )}
          </div>

          {/* Details */}
          <div style={styles.details}>
            <span className={`badge badge-${listing.status?.toLowerCase()}`} style={{ marginBottom: '0.5rem' }}>
              {listing.status}
            </span>
            <h1 style={styles.title}>{listing.title}</h1>
            <p style={styles.price}>{Number(listing.price).toLocaleString()} UZS</p>

            <div style={styles.meta}>
              <span><strong>Condition:</strong> {listing.condition?.replace('_', ' ')}</span>
              {listing.brand && <span><strong>Brand:</strong> {listing.brand}</span>}
              {listing.size  && <span><strong>Size:</strong> {listing.size}</span>}
            </div>

            <p style={styles.description}>{listing.description}</p>

            <Link to={`/profile/${listing.sellerId}`} style={styles.sellerLink}>
              View seller profile &rarr;
            </Link>

            {orderMsg && <p style={{ color: '#1976d2', margin: '0.75rem 0' }}>{orderMsg}</p>}

            <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1.25rem', flexWrap: 'wrap' }}>
              {canOrder && (
                <button
                  className="btn btn-primary"
                  onClick={() => orderMutation.mutate()}
                  disabled={orderMutation.isPending}
                >
                  {orderMutation.isPending ? 'Processing...' : 'Buy Now'}
                </button>
              )}
              {isOwner && (
                <>
                  <button
                    className="btn btn-secondary"
                    onClick={() => navigate(`/listings/${id}/edit`)}
                  >
                    Edit
                  </button>
                  <button
                    className="btn btn-danger"
                    onClick={() => { if (confirm('Delete this listing?')) deleteMutation.mutate() }}
                    disabled={deleteMutation.isPending}
                  >
                    Delete
                  </button>
                </>
              )}
              {!isAuthenticated && listing.status === 'ACTIVE' && (
                <Link to="/login" className="btn btn-primary">Log in to Buy</Link>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

const styles = {
  layout:       { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', alignItems: 'start' },
  gallery:      {},
  mainImgWrapper: { borderRadius: '8px', overflow: 'hidden', background: '#f0f0f0', paddingTop: '75%', position: 'relative' },
  mainImg:      { position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', objectFit: 'contain' },
  noImg:        { position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#9e9e9e' },
  thumbs:       { display: 'flex', gap: '0.5rem', marginTop: '0.5rem', flexWrap: 'wrap' },
  thumb:        { width: '70px', height: '70px', objectFit: 'cover', borderRadius: '6px', cursor: 'pointer' },
  details:      { display: 'flex', flexDirection: 'column', gap: '0.75rem' },
  title:        { fontSize: '1.5rem', fontWeight: 700 },
  price:        { fontSize: '1.75rem', fontWeight: 800, color: '#1976d2' },
  meta:         { display: 'flex', flexDirection: 'column', gap: '0.25rem', color: '#424242', fontSize: '0.9rem' },
  description:  { color: '#424242', lineHeight: 1.7 },
  sellerLink:   { color: '#1976d2', fontSize: '0.9rem' },
}
