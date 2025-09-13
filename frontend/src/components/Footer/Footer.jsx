
import LogoutButton from "../oauth/LogoutButton";
import KlaroConsentButton from "../CookieConsent/KlaroConsentButton";
import GitHubButton from "../GitHubButton";

const Footer = () => {
  return (
    <footer>
      {/* Left part: buttons */}
      <div className="footer-left">
        <LogoutButton />
        <KlaroConsentButton />
        <GitHubButton />
      </div>

      {/* Right part: Links (open in new tab) */}
      <nav className="footer-nav">
        <a href="/privacy" target="_blank" rel="noopener noreferrer">
          Privacy Policy
        </a>
        <a href="/terms" target="_blank" rel="noopener noreferrer">
          Terms of Use
        </a>
        <a href="/cookies" target="_blank" rel="noopener noreferrer">
          Cookie Policy
        </a>
      </nav>
    </footer>
  );
};

export default Footer;
