import client from './client'

export const authApi = {
  register: (data) => client.post('/auth/register', data).then(r => r.data),
  login:    (data) => client.post('/auth/login', data).then(r => r.data),
  getMe:    ()     => client.get('/users/me').then(r => r.data),
  getUser:  (id)   => client.get(`/users/${id}`).then(r => r.data),
}
