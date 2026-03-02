import { createContext, useContext, useState } from 'react'
import translations from '../i18n/translations'

const LocaleContext = createContext()

export function LocaleProvider({ children }) {
  const [locale, setLocale] = useState(
    () => localStorage.getItem('locale') ?? 'uz'
  )

  function changeLocale(lang) {
    setLocale(lang)
    localStorage.setItem('locale', lang)
  }

  const t = translations[locale] ?? translations.uz

  return (
    <LocaleContext.Provider value={{ locale, changeLocale, t }}>
      {children}
    </LocaleContext.Provider>
  )
}

export function useLocale() {
  return useContext(LocaleContext)
}
