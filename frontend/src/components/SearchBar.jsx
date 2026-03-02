import { useState } from 'react'
import { useLocale } from '../context/LocaleContext'

const CATEGORIES = [
  'CLOTHING', 'SHOES', 'KIDS_CLOTHING', 'KIDS_SHOES',
  'ACCESSORIES', 'BAGS', 'SPORTSWEAR', 'OUTERWEAR', 'OTHER',
]

export default function SearchBar({ onSearch, initialValues = {} }) {
  const { t } = useLocale()
  const [query,    setQuery]    = useState(initialValues.query    ?? '')
  const [brand,    setBrand]    = useState(initialValues.brand    ?? '')
  const [minPrice, setMinPrice] = useState(initialValues.minPrice ?? '')
  const [maxPrice, setMaxPrice] = useState(initialValues.maxPrice ?? '')
  const [category, setCategory] = useState(initialValues.category ?? '')

  function handleSubmit(e) {
    e.preventDefault()
    onSearch({ query, brand, minPrice, maxPrice, category: category || undefined })
  }

  function handleClear() {
    setQuery(''); setBrand(''); setMinPrice(''); setMaxPrice(''); setCategory('')
    onSearch({})
  }

  return (
    <form onSubmit={handleSubmit} style={styles.form}>
      <input
        style={styles.input}
        placeholder={t.search.placeholder}
        value={query}
        onChange={e => setQuery(e.target.value)}
      />
      <select
        style={{ ...styles.input, width: '160px', flex: 'none' }}
        value={category}
        onChange={e => setCategory(e.target.value)}
      >
        <option value="">{t.search.allCategories}</option>
        {CATEGORIES.map(c => (
          <option key={c} value={c}>{t.categoryLabels[c] ?? c}</option>
        ))}
      </select>
      <input
        style={{ ...styles.input, width: '130px' }}
        placeholder={t.search.brand}
        value={brand}
        onChange={e => setBrand(e.target.value)}
      />
      <input
        style={{ ...styles.input, width: '110px' }}
        placeholder={t.search.minPrice}
        type="number"
        min="0"
        value={minPrice}
        onChange={e => setMinPrice(e.target.value)}
      />
      <input
        style={{ ...styles.input, width: '110px' }}
        placeholder={t.search.maxPrice}
        type="number"
        min="0"
        value={maxPrice}
        onChange={e => setMaxPrice(e.target.value)}
      />
      <button type="submit" className="btn btn-primary">{t.search.search}</button>
      <button type="button" className="btn btn-secondary" onClick={handleClear}>{t.search.clear}</button>
    </form>
  )
}

const styles = {
  form: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: '0.5rem',
    alignItems: 'center',
    padding: '1rem',
    background: '#fff',
    borderRadius: '8px',
    boxShadow: '0 1px 3px rgba(0,0,0,0.12)',
    marginBottom: '1.5rem',
  },
  input: {
    flex: 1,
    minWidth: '140px',
    padding: '0.5rem 0.75rem',
    border: '1px solid #bdbdbd',
    borderRadius: '6px',
    fontSize: '0.95rem',
    outline: 'none',
  },
}
