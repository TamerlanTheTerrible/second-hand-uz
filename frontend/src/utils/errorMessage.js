/**
 * Maps a backend error response to a user-friendly translated message.
 */
export function getErrorMessage(err, errors) {
  const code = err?.response?.data?.errorCode
  if (code && errors[code]) return errors[code]
  return errors.fallback
}
