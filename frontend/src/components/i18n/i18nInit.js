import i18next from "i18next";
import Backend from 'i18next-http-backend';
import {initReactI18next} from 'react-i18next'

import en from './locales/en.json'
import uk from './locales/uk.json'
import de from './locales/de.json'


const browserLang = navigator.langukge?.split('-')[0] 
const savedLanguage=localStorage.getItem('Language')||browserLang||'en';
const REMOTE_NS = ['remoteA', 'remoteB', 'remoteC']; //must renamed
const BASE_NS = ['translation']; 

const i18nInit=()=>{
return i18next
   .use(Backend)
   .use(initReactI18next)
    .init({
      debug:true,
      lng: savedLanguage,  // if you're using a language detector, do not define the lng option
      fallbackLng: "en",

      ns:[...BASE_NS,...REMOTE_NS],
      defaultNS:'translation',

      resources: {
      en: { translation: en },
      de: { translation: de },
      uk: {translation: uk},
    },

    backend:{
        // Terms of Use: /api/meta/terms
        // Privacy Policy: /api/meta/privacy
        // Cookie Policy: /api/meta/cookies

        loadPath:'/api/locales/{{lng}}/{{ns}}.json'
    },
  }, function(err){
    if (err) {
    console.error('i18next init failed', err);
  } else {
    console.log('i18next initialized successfully');
  }
  })}
  

export default i18nInit;
export {i18next};
