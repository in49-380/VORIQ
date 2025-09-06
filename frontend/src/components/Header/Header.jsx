
import GitHubButton from "../GitHubButton";
import I18nDropDown from "../i18n/I18nDropDown";
import ThemeButton from "../ThemeSwitch/ThemeButton"
const Header = () => {
  return (
    <header >
      <GitHubButton id='b12' 
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
