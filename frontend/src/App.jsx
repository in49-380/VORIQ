import { useContext } from 'react';

import './App.css'
import LogoutButton from './oauth/LogoutButton'
import AuthContext from "./oauth/AuthContext";
import RegistrationPage from './pages/StartPage';
import CarPage from './pages/CarPage';
import KlaroConsentModal from './components/CookieConsent/KlaroConsentModal';
import KlaroConsentButton from './components/CookieConsent/KlaroConsentButton';
import IconButton from './components/IconButton';
import Header from './components/Header/Header.jsx';
import Footer from './components/Footer/Footer.jsx';
import { useScreen } from './hooks/useScreen';

const screenPage = {
  login: <RegistrationPage />,
  cars: (
    <>
      <CarPage />
      <LogoutButton/>
    </>
  ),
}

function App() {
  const { token } = useContext(AuthContext);
  const { currentScreen, setCurrentScreen } = useScreen('cars');

  if (!token && currentScreen !== 'login') {
    setCurrentScreen('login');
  }

  console.log('===currentScreen', currentScreen); 

  return <>
    {/* hier kommt header */}
    <Header/>
    <IconButton 
      onClick={()=>window.open('https://github.com/in49-380/VORIQ', '_blank')}
      title='GitHub'
      className="fixed top-4 right-4"  
    />
    {/* main */}
    <main>
      {screenPage[currentScreen]}
    </main>
    <KlaroConsentModal/>
    {/* hier kommt Footer */}
    <Footer/>
    <KlaroConsentButton/>
 </> 
}

export default App