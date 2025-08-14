import IconButton from "../IconButton";
import I18nDropDown from "../i18n/I18nDropDown";

const Header = () => {
   return (
    <header>
      <IconButton onClick={()=>window.open('https://github.com/in49-380/VORIQ', '_blank')}
         title='GitHub'
         className="fixed top-4 right-4"  
      />
      <I18nDropDown />       
      <button onClick={()=>{
        localStorage.removeItem('acceptedCookies')
      }}>
        delete Cookies-consent for Test
      </button>
    </header>
   );
}

export default Header