import './App.css'
import LogoutButton from './components/oauth/LogoutButton'
import AuthContext from "./components/oauth/AuthContext";
import { useContext } from "react";


import RegistrationPage from './pages/StartPage';
import CarPage from './pages/CarPage';
import KlaroConsentModal from './components/CookieConsent/KlaroConsentModal';
import KlaroConsentButton from './components/CookieConsent/KlaroConsentButton';
import IconButton from './components/IconButton';
import I18nDropDown from './components/i18n/I18nDropDown';




function App() {
  const { token } = useContext(AuthContext);
  
  return <>
    {/* hier kommt header */}
    <IconButton onClick={()=>window.open('https://github.com/in49-380/VORIQ', '_blank')}
                title='GitHub'
                className="fixed top-4 right-4"  
                />
    <I18nDropDown />       
    <button onClick={()=>{
  localStorage.removeItem('acceptedCookies')
  }

     }>
    delete Cookies-consent for Test
    </button>
    
    {/* main */}
    <KlaroConsentModal/>
      {!token?
        <RegistrationPage/>
        :
        <>
        <CarPage/>
        <LogoutButton/>
        </>}

        
    {/* hier kommt Footer */}
    <KlaroConsentButton/>
 </> 
}

export default App