import LogoutButton from "../oauth/LogoutButton";
import KlaroConsentButton from "../CookieConsent/KlaroConsentButton";
import GitHubButton from "../GitHubButton";

const Footer = () => {
  return (
    <footer className="fixed bottom-0 left-0 w-full bg-gray-100 p-4 text-center shadow">
      {/* Left part: buttons */}
      <div className="flex flex-wrap items-center gap-2">
        "{/* LogoutButton */}
        <LogoutButton />
        {/* Cookies */}
        <KlaroConsentButton />
        {/* GitHub */}
        <GitHubButton />
      </div>

      {/* Right part: Links (open in new tab) */}
      <nav className="flex flex-wrap items-center gap-4 text-sm text-gray-600">
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
