import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { listingsApi } from '../api/listings'

const CONDITIONS = ['NEW', 'LIKE_NEW', 'GOOD', 'FAIR', 'POOR']

export default function CreateListingPage() {
  const navigate    = useNavigate()
  const queryClient = useQueryClient()

  const [form, setForm] = useState({
    title: '', description: '', price: '', condition: 'GOOD',
    brand: '', size: '',
  })
  const [images, setImages]   = useState([])
  const [error,  setError]    = useState('')

  function update(field, value) {
    setForm(f => ({ ...f, [field]: value }))
  }

  const createMutation = useMutation({
    mutationFn: async () => {
      const listing = await listingsApi.create({
        ...form,
        price: Number(form.price),
      })
      for (const file of images) {
        const fd = new FormData()
        fd.append('image', file)
        await listingsApi.uploadImage(listing.id, fd)
      }
      return listing
    },
    onSuccess: (listing) => {
      queryClient.invalidateQueries({ queryKey: ['listings'] })
      navigate(`/listings/${listing.id}`)
    },
    onError: (err) => {
      const fieldErrors = err.response?.data?.fieldErrors
      if (fieldErrors) {
        setError(Object.values(fieldErrors).join(', '))
      } else {
        setError(err.response?.data?.message ?? 'Failed to create listing')
      }
    },
  })

  function handleSubmit(e) {
    e.preventDefault()
    setError('')
    createMutation.mutate()
  }

  return (
    <div className="page">
      <div className="container" style={{ maxWidth: '640px' }}>
        <h1 style={styles.heading}>Create Listing</h1>
        <div className="card" style={{ padding: '2rem' }}>
          {error && <p className="error-message" style={{ marginBottom: '1rem' }}>{error}</p>}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Title *</label>
              <input required value={form.title} onChange={e => update('title', e.target.value)} maxLength={200} />
            </div>
            <div className="form-group">
              <label>Description *</label>
              <textarea
                required
                rows={5}
                value={form.description}
                onChange={e => update('description', e.target.value)}
                maxLength={5000}
              />
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div className="form-group">
                <label>Price (UZS) *</label>
                <input
                  type="number" required min="0" step="100"
                  value={form.price}
                  onChange={e => update('price', e.target.value)}
                />
              </div>
              <div className="form-group">
                <label>Condition *</label>
                <select value={form.condition} onChange={e => update('condition', e.target.value)}>
                  {CONDITIONS.map(c => (
                    <option key={c} value={c}>{c.replace('_', ' ')}</option>
                  ))}
                </select>
              </div>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div className="form-group">
                <label>Brand</label>
                <input value={form.brand} onChange={e => update('brand', e.target.value)} maxLength={100} />
              </div>
              <div className="form-group">
                <label>Size</label>
                <input value={form.size} onChange={e => update('size', e.target.value)} maxLength={50} />
              </div>
            </div>
            <div className="form-group">
              <label>Images (optional, up to 10)</label>
              <input
                type="file"
                accept="image/*"
                multiple
                onChange={e => setImages(Array.from(e.target.files).slice(0, 10))}
              />
              {images.length > 0 && (
                <span style={{ fontSize: '0.85rem', color: '#616161' }}>
                  {images.length} file(s) selected
                </span>
              )}
            </div>
            <button
              type="submit"
              className="btn btn-primary"
              style={{ width: '100%', marginTop: '0.5rem' }}
              disabled={createMutation.isPending}
            >
              {createMutation.isPending ? 'Creating...' : 'Create Listing'}
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}

const styles = {
  heading: { fontSize: '1.75rem', fontWeight: 700, marginBottom: '1.5rem' },
}
