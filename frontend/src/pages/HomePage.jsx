import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { listingsApi } from '../api/listings'
import ListingCard from '../components/ListingCard'
import SearchBar from '../components/SearchBar'

export default function HomePage() {
  const [params, setParams] = useState({ status: 'ACTIVE' })

  const { data: listings, isLoading, isError } = useQuery({
    queryKey: ['listings', params],
    queryFn: () => listingsApi.getAll(params),
  })

  function handleSearch(values) {
    setParams({ ...values, status: 'ACTIVE' })
  }

  return (
    <div className="page">
      <div className="container">
        <h1 style={styles.heading}>Browse Listings</h1>
        <SearchBar onSearch={handleSearch} />

        {isLoading && <div className="spinner">Loading...</div>}
        {isError   && <p style={styles.err}>Failed to load listings.</p>}

        {listings && (
          listings.length === 0
            ? <p style={styles.empty}>No listings found.</p>
            : <div className="grid">
                {listings.map(l => <ListingCard key={l.id} listing={l} />)}
              </div>
        )}
      </div>
    </div>
  )
}

const styles = {
  heading: { fontSize: '1.75rem', marginBottom: '1.25rem', fontWeight: 700 },
  err:     { color: '#d32f2f' },
  empty:   { color: '#757575', textAlign: 'center', padding: '3rem' },
}
