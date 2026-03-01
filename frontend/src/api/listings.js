import client from './client'

export const listingsApi = {
  getAll:    (params) => client.get('/listings', { params }).then(r => r.data),
  getById:   (id)     => client.get(`/listings/${id}`).then(r => r.data),
  getBySeller: (sellerId) => client.get(`/listings/seller/${sellerId}`).then(r => r.data),
  create:    (data)   => client.post('/listings', data).then(r => r.data),
  update:    (id, data) => client.put(`/listings/${id}`, data).then(r => r.data),
  remove:    (id)     => client.delete(`/listings/${id}`).then(r => r.data),
  uploadImage: (id, formData) =>
    client.post(`/listings/${id}/images`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }).then(r => r.data),
}
