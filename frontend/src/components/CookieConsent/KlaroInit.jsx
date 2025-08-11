import { klaroConfig } from './klaroConfig'
import  *  as  klaro  from  'klaro/dist/klaro-no-css'
// import 'klaro/dist/cm'
import './klaro.css'


export default function KlaroInit() {
  const currentLang = localStorage.getItem('Language') || 'en'

    window.klaro = klaro;
    window.klaroConfig = {...klaroConfig, lang:currentLang};
    klaro.setup(window.klaroConfig);

    
  return null
}
