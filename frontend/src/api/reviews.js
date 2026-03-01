import client from './client'

export const reviewsApi = {
  getSellerReviews: (sellerId) => client.get(`/reviews/seller/${sellerId}`).then(r => r.data),
  create: (data) => client.post('/reviews', data).then(r => r.data),
}
