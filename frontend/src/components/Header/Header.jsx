
import GitHubButton from "../GitHubButton";
import I18nDropDown from "../i18n/I18nDropDown";
import ThemeButton from "../ThemeSwitch/ThemeButton";
import {useTranslation} from 'react-i18next';
import AuthContext from "../oauth/AuthContext";
import { useContext } from "react";

const Header = () => {
  const {t}=useTranslation();
    const {token}=useContext(AuthContext);

  return (
    <header id="header" >

      <img className="logo"
       src="/images/logo.png"
       alt="logo"
      />

{  token && <div className="links">
          <a href="#chooseBlock">{t('headerLinks.services')}</a>
          <a href="#aboutUsBlock">{t('headerLinks.aboutUs')}</a>
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
      </div>}
      <div className="header_buttons">
        <I18nDropDown />
        <ThemeButton/>
      </div>

     
    </header>
  );
};

export default Header;
