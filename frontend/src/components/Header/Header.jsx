
import GitHubButton from "../GitHubButton";
import I18nDropDown from "../i18n/I18nDropDown";
import ThemeButton from "../ThemeSwitch/ThemeButton"
const Header = () => {
  return (
    <header className="fixed top-0 left-0 right-0 z-50 bg-gray-100 border-b border-gray-200 shadow">
      <GitHubButton id='b12'
        onClick={() =>
          window.open("https://github.com/in49-380/VORIQ", "_blank")
        }
        title="GitHub"
        className="fixed top-4 right-4"
      />
      <I18nDropDown />
      <button id='b13'
        onClick={() => {
          localStorage.removeItem("acceptedCookies");
        }}
      >
        delete Cookies-consent for Test
      </button>

      <button id='b20'
        onClick={()=>{
        localStorage.removeItem('hintIsViewed')
      }}>
        delete Hint-mark for Test
      </button>

      <ThemeButton/>
    </header>
  );
};

export default Header;
