import GitHubButton from "../GitHubButton";
import I18nDropDown from "../i18n/I18nDropDown";

const Header = () => {
  return (
    <header className="fixed top-0 left-0 right-0 z-50 bg-gray-100 border-b border-gray-200 shadow">
      <GitHubButton
        onClick={() =>
          window.open("https://github.com/in49-380/VORIQ", "_blank")
        }
        title="GitHub"
        className="fixed top-4 right-4"
      />
      <I18nDropDown />
      <button
        onClick={() => {
          localStorage.removeItem("acceptedCookies");
        }}
      >
        delete Cookies-consent for Test
      </button>
    </header>
  );
};

export default Header;
