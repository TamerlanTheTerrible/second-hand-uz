import client from './client'

export const ordersApi = {
  create:    (data)   => client.post('/orders', data).then(r => r.data),
  getMyOrders: ()     => client.get('/orders').then(r => r.data.content),
  getById:   (id)     => client.get(`/orders/${id}`).then(r => r.data),
  cancel:    (id)     => client.post(`/orders/${id}/cancel`).then(r => r.data),
  createPaymentSession: (orderId) =>
    client.post(`/payments/session/${orderId}`).then(r => r.data),
  mockConfirm: (orderId) =>
    client.post(`/payments/mock-confirm/${orderId}`).then(r => r.data),
  getForMockPayment: (orderId) =>
    client.get(`/payments/mock-info/${orderId}`).then(r => r.data),
}
