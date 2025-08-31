import KlaroConsentButton from "../CookieConsent/KlaroConsentButton";
import LogoutButton from '../oauth/LogoutButton'
const Footer = () => {
   return (
    <footer>
           <KlaroConsentButton/>
           <LogoutButton/>
    </footer>
   );
}

export default Footer