import './App.css'
import LogoutButton from './oauth/LogoutButton'
import AuthContext from "./oauth/AuthContext";
import { useContext } from "react";
import RegistrationPage from './Pages/StartPage';
import CarPage from './Pages/CarPage';
import KlaroConsentModal from './components/CookieConsent/KlaroConsentModal';
import KlaroConsentButton from './components/CookieConsent/KlaroConsentButton';
import IconButton from './components/IconButton';




function App() {
  const { token } = useContext(AuthContext);
  
  return <>
    {/* hier kommt header */}
    <IconButton onClick={()=>window.open('https://github.com/in49-380/VORIQ', '_blank')}
                title='GitHub'
                className="fixed top-4 right-4"  
                />
    {/* main */}
    <KlaroConsentModal/>
      {!token?
        <RegistrationPage/>
        :
        <>
        <CarPage/>
        <LogoutButton/>
        </>}
    {/* hier kommt Futer */}
    <KlaroConsentButton/>
 </> 
}

export default App