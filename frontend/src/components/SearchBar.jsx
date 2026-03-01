import { useState } from 'react'

export default function SearchBar({ onSearch, initialValues = {} }) {
  const [query,    setQuery]    = useState(initialValues.query    ?? '')
  const [brand,    setBrand]    = useState(initialValues.brand    ?? '')
  const [minPrice, setMinPrice] = useState(initialValues.minPrice ?? '')
  const [maxPrice, setMaxPrice] = useState(initialValues.maxPrice ?? '')

  function handleSubmit(e) {
    e.preventDefault()
    onSearch({ query, brand, minPrice, maxPrice })
  }

  function handleClear() {
    setQuery(''); setBrand(''); setMinPrice(''); setMaxPrice('')
    onSearch({})
  }

  return (
    <form onSubmit={handleSubmit} style={styles.form}>
      <input
        style={styles.input}
        placeholder="Search listings..."
        value={query}
        onChange={e => setQuery(e.target.value)}
      />
      <input
        style={{ ...styles.input, width: '130px' }}
        placeholder="Brand"
        value={brand}
        onChange={e => setBrand(e.target.value)}
      />
      <input
        style={{ ...styles.input, width: '120px' }}
        placeholder="Min price"
        type="number"
        min="0"
        value={minPrice}
        onChange={e => setMinPrice(e.target.value)}
      />
      <input
        style={{ ...styles.input, width: '120px' }}
        placeholder="Max price"
        type="number"
        min="0"
        value={maxPrice}
        onChange={e => setMaxPrice(e.target.value)}
      />
      <button type="submit" className="btn btn-primary">Search</button>
      <button type="button" className="btn btn-secondary" onClick={handleClear}>Clear</button>
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
    minWidth: '150px',
    padding: '0.5rem 0.75rem',
    border: '1px solid #bdbdbd',
    borderRadius: '6px',
    fontSize: '0.95rem',
    outline: 'none',
  },
}
