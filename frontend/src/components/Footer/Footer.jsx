
import LogoutButton from "../oauth/LogoutButton";
import KlaroConsentButton from "../CookieConsent/KlaroConsentButton";
import GitHubButton from "../GitHubButton";
import {useTranslation} from 'react-i18next'


const Footer = () => {
  const {t}=useTranslation()
  return (
    <footer>
      {/* Left part: buttons */}
      <div className="linksTop">
        <KlaroConsentButton />
        <LogoutButton />
        <GitHubButton />
      </div>

      {/* Right part: Links (open in new tab) */}
      <nav className="footer-nav">
        <a href="/privacy" target="_blank" rel="noopener noreferrer">
          {t('policy.privacy')}
        </a>
        <a href="/terms" target="_blank" rel="noopener noreferrer">
          {t('policy.termsofUse')}
        </a>
        <a href="/cookies" target="_blank" rel="noopener noreferrer">
          {t('policy.cookiePolicy')}
        </a>
      </nav>

      <img className="logo"
       src="/images/logo.png"
       alt="logo"
      />

      <p className="allrights">{t('rights')}</p>
    </footer>
  );
};

export default Footer;
