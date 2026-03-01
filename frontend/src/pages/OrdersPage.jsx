import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { ordersApi } from '../api/orders'
import { reviewsApi } from '../api/reviews'

const STATUS_LABELS = {
  CREATED:   { label: 'Created',   color: '#1976d2' },
  PAID:      { label: 'Paid',      color: '#388e3c' },
  SHIPPED:   { label: 'Shipped',   color: '#f57c00' },
  COMPLETED: { label: 'Completed', color: '#7b1fa2' },
  CANCELED:  { label: 'Canceled',  color: '#9e9e9e' },
}

export default function OrdersPage() {
  const queryClient = useQueryClient()
  const [reviewForm, setReviewForm] = useState({ orderId: null, rating: 5, comment: '' })

  const { data: orders, isLoading } = useQuery({
    queryKey: ['orders'],
    queryFn: ordersApi.getMyOrders,
  })

  const cancelMutation = useMutation({
    mutationFn: (id) => ordersApi.cancel(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['orders'] }),
  })

  const reviewMutation = useMutation({
    mutationFn: ({ orderId, rating, comment }) => reviewsApi.create({ orderId, rating, comment }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
      setReviewForm({ orderId: null, rating: 5, comment: '' })
    },
  })

  if (isLoading) return <div className="spinner">Loading...</div>

  return (
    <div className="page">
      <div className="container" style={{ maxWidth: '760px' }}>
        <h1 style={{ fontSize: '1.75rem', fontWeight: 700, marginBottom: '1.5rem' }}>My Orders</h1>

        {!orders?.length && (
          <p style={{ color: '#757575', textAlign: 'center', padding: '3rem' }}>No orders yet.</p>
        )}

        {orders?.map(order => {
          const statusInfo = STATUS_LABELS[order.status] ?? { label: order.status, color: '#757575' }
          return (
            <div key={order.id} className="card" style={styles.orderCard}>
              <div style={styles.orderHeader}>
                <div>
                  <Link to={`/listings/${order.listingId}`} style={{ fontWeight: 600, color: '#1976d2' }}>
                    Order #{order.id}
                  </Link>
                  <p style={{ fontSize: '0.85rem', color: '#757575', marginTop: '0.2rem' }}>
                    {new Date(order.createdAt).toLocaleDateString()}
                  </p>
                </div>
                <span style={{ ...styles.statusBadge, color: statusInfo.color }}>
                  {statusInfo.label}
                </span>
              </div>

              <p style={styles.price}>{Number(order.totalAmount).toLocaleString()} UZS</p>

              <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap', marginTop: '0.75rem' }}>
                {order.status === 'CREATED' && (
                  <>
                    <button
                      className="btn btn-primary"
                      onClick={() => ordersApi.createPaymentSession(order.id)
                        .then(s => window.location.href = s.paymentUrl)
                      }
                    >
                      Pay Now
                    </button>
                    <button
                      className="btn btn-danger"
                      onClick={() => cancelMutation.mutate(order.id)}
                      disabled={cancelMutation.isPending}
                    >
                      Cancel
                    </button>
                  </>
                )}
                {order.status === 'COMPLETED' && (
                  <button
                    className="btn btn-secondary"
                    onClick={() => setReviewForm(f => ({ ...f, orderId: order.id }))}
                  >
                    Leave Review
                  </button>
                )}
              </div>

              {reviewForm.orderId === order.id && (
                <div style={styles.reviewForm}>
                  <div className="form-group">
                    <label>Rating (1-5)</label>
                    <input
                      type="number" min="1" max="5"
                      value={reviewForm.rating}
                      onChange={e => setReviewForm(f => ({ ...f, rating: Number(e.target.value) }))}
                    />
                  </div>
                  <div className="form-group">
                    <label>Comment</label>
                    <textarea
                      rows={3}
                      value={reviewForm.comment}
                      onChange={e => setReviewForm(f => ({ ...f, comment: e.target.value }))}
                    />
                  </div>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button
                      className="btn btn-primary"
                      onClick={() => reviewMutation.mutate(reviewForm)}
                      disabled={reviewMutation.isPending}
                    >
                      Submit
                    </button>
                    <button
                      className="btn btn-secondary"
                      onClick={() => setReviewForm({ orderId: null, rating: 5, comment: '' })}
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              )}
            </div>
          )
        })}
      </div>
    </div>
  )
}

const styles = {
  orderCard:   { padding: '1.25rem', marginBottom: '1rem' },
  orderHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' },
  statusBadge: { fontWeight: 700, fontSize: '0.9rem' },
  price:       { fontSize: '1.1rem', fontWeight: 700, color: '#1976d2', marginTop: '0.5rem' },
  reviewForm:  { marginTop: '1rem', padding: '1rem', background: '#f9f9f9', borderRadius: '6px' },
}
