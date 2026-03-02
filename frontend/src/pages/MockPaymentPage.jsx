import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery, useMutation } from '@tanstack/react-query'
import { ordersApi } from '../api/orders'
import { useLocale } from '../context/LocaleContext'

export default function MockPaymentPage() {
  const { t } = useLocale()
  const { orderId } = useParams()
  const navigate = useNavigate()
  const [done, setDone] = useState(false)

  const { data: order, isLoading } = useQuery({
    queryKey: ['mock-order', orderId],
    queryFn: () => ordersApi.getForMockPayment(orderId),
  })

  const confirmMutation = useMutation({
    mutationFn: () => ordersApi.mockConfirm(orderId),
    onSuccess: () => {
      setDone(true)
      setTimeout(() => navigate('/orders'), 1500)
    },
  })

  if (isLoading) return <div className="spinner">Loading...</div>

  return (
    <div className="page" style={styles.wrapper}>
      <div className="card" style={styles.card}>
        <div style={styles.mockBadge}>🔧 {t.mockPayment.title}</div>

        <h1 style={styles.title}>{t.mockPayment.title}</h1>

        <div style={styles.detail}>
          <span style={styles.label}>{t.mockPayment.orderLabel} #</span>
          <strong>{order?.id}</strong>
        </div>
        <div style={styles.detail}>
          <span style={styles.label}>{t.mockPayment.amountLabel}</span>
          <strong style={{ color: '#1976d2', fontSize: '1.25rem' }}>
            {Number(order?.totalPrice ?? 0).toLocaleString()} UZS
          </strong>
        </div>

        {done ? (
          <p style={{ color: '#388e3c', fontWeight: 600, marginTop: '1.5rem', textAlign: 'center' }}>
            ✓ {t.mockPayment.successMsg}
          </p>
        ) : (
          <button
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '1.5rem' }}
            onClick={() => confirmMutation.mutate()}
            disabled={confirmMutation.isPending}
          >
            {confirmMutation.isPending ? t.mockPayment.confirming : t.mockPayment.confirm}
          </button>
        )}
      </div>
    </div>
  )
}

const styles = {
  wrapper: { display: 'flex', justifyContent: 'center', alignItems: 'flex-start', paddingTop: '4rem' },
  card:    { width: '100%', maxWidth: '420px', padding: '2rem' },
  mockBadge: {
    display: 'inline-block',
    background: '#fff3e0',
    color: '#e65100',
    borderRadius: '4px',
    padding: '0.2rem 0.75rem',
    fontSize: '0.8rem',
    fontWeight: 700,
    marginBottom: '1rem',
  },
  title:   { fontSize: '1.4rem', fontWeight: 700, marginBottom: '1.5rem' },
  detail:  { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.5rem 0', borderBottom: '1px solid #f0f0f0' },
  label:   { color: '#757575', fontSize: '0.9rem' },
}
