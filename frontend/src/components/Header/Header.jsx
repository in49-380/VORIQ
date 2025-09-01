import IconButton from "../IconButton";
import ThemeButton from "../ThemeSwitch/ThemeButton";
import I18nDropDown from "../i18n/I18nDropDown";

const Header = () => {
   return (
    <header>
      <IconButton id='b12'
         onClick={()=>window.open('https://github.com/in49-380/VORIQ', '_blank')}
         title='GitHub'
         className="absolute top-4 right-4"  
      />
      <I18nDropDown />    

      <button id='b13'
        onClick={()=>{
        localStorage.removeItem('acceptedCookies')
      }}>
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
}

export default Header