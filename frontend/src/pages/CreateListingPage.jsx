import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { listingsApi } from '../api/listings'
import { useLocale } from '../context/LocaleContext'
import { getErrorMessage } from '../utils/errorMessage'

const CONDITIONS  = ['NEW', 'LIKE_NEW', 'GOOD', 'FAIR', 'POOR']
const CATEGORIES  = [
  'CLOTHING', 'SHOES', 'KIDS_CLOTHING', 'KIDS_SHOES',
  'ACCESSORIES', 'BAGS', 'SPORTSWEAR', 'OUTERWEAR', 'OTHER',
]

export default function CreateListingPage() {
  const { t } = useLocale()
  const navigate    = useNavigate()
  const queryClient = useQueryClient()

  const [form, setForm] = useState({
    title: '', description: '', price: '', condition: 'GOOD',
    category: 'CLOTHING', brand: '', size: '', gender: '',
  })
  const [images, setImages] = useState([])
  const [error,  setError]  = useState('')

  function update(field, value) {
    setForm(f => ({ ...f, [field]: value }))
  }

  const createMutation = useMutation({
    mutationFn: async () => {
      const listing = await listingsApi.create({
        ...form,
        price: Number(form.price),
        gender: form.gender || null,
      })
      for (const file of images) {
        const fd = new FormData()
        fd.append('file', file)
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
      if (fieldErrors?.length) {
        setError(fieldErrors.map(fe => `${fe.field}: ${fe.message}`).join(', '))
      } else {
        setError(getErrorMessage(err, t.errors))
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
        <h1 style={styles.heading}>{t.createListing.title}</h1>
        <div className="card" style={{ padding: '2rem' }}>
          {error && <p className="error-message" style={{ marginBottom: '1rem' }}>{error}</p>}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>{t.createListing.titleField}</label>
              <input required value={form.title} onChange={e => update('title', e.target.value)} maxLength={200} />
            </div>
            <div className="form-group">
              <label>{t.createListing.description}</label>
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
                <label>{t.createListing.price}</label>
                <input
                  type="number" required min="0" step="100"
                  value={form.price}
                  onChange={e => update('price', e.target.value)}
                />
              </div>
              <div className="form-group">
                <label>{t.createListing.category}</label>
                <select value={form.category} onChange={e => update('category', e.target.value)}>
                  {CATEGORIES.map(c => (
                    <option key={c} value={c}>{t.categoryLabels[c] ?? c.replace(/_/g, ' ')}</option>
                  ))}
                </select>
              </div>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div className="form-group">
                <label>{t.createListing.condition}</label>
                <select value={form.condition} onChange={e => update('condition', e.target.value)}>
                  {CONDITIONS.map(c => (
                    <option key={c} value={c}>{t.conditionLabels[c] ?? c.replace(/_/g, ' ')}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>{t.createListing.gender}</label>
                <select value={form.gender} onChange={e => update('gender', e.target.value)}>
                  <option value="">{t.createListing.genderAny}</option>
                  <option value="MALE">{t.createListing.genderMale}</option>
                  <option value="FEMALE">{t.createListing.genderFemale}</option>
                  <option value="UNISEX">{t.createListing.genderUnisex}</option>
                </select>
              </div>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div className="form-group">
                <label>{t.createListing.brand}</label>
                <input value={form.brand} onChange={e => update('brand', e.target.value)} maxLength={100} />
              </div>
              <div className="form-group">
                <label>{t.createListing.size}</label>
                <input value={form.size} onChange={e => update('size', e.target.value)} maxLength={50} />
              </div>
            </div>
            <div className="form-group">
              <label>{t.createListing.images}</label>
              <input
                type="file"
                accept="image/*"
                multiple
                onChange={e => setImages(Array.from(e.target.files).slice(0, 10))}
              />
              {images.length > 0 && (
                <span style={{ fontSize: '0.85rem', color: '#616161' }}>
                  {images.length} {t.createListing.filesSelected}
                </span>
              )}
            </div>
            <button
              type="submit"
              className="btn btn-primary"
              style={{ width: '100%', marginTop: '0.5rem' }}
              disabled={createMutation.isPending}
            >
              {createMutation.isPending ? t.createListing.submitting : t.createListing.submit}
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
