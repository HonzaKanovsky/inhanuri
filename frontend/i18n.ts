import { createI18n } from 'vue-i18n';

import en from './locales/en.json';
import ko from './locales/ko.json';
import ru from './locales/ru.json';
import fr from './locales/fr.json';

const messages = {
  en,
  ko,
  ru,
  fr,
};

const i18n = createI18n({
  locale: 'en',
  fallbackLocale: 'en',
  messages,
});

export type SupportedLocales = keyof typeof messages;

export default i18n;
