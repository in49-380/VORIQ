import { useContext } from 'react';

import './App.css'
import LogoutButton from './components/oauth/LogoutButton'
import AuthContext from "./components/oauth/AuthContext";
import RegistrationPage from './pages/StartPage';
import CarPage from './pages/CarPage';
import KlaroConsentModal from './components/CookieConsent/KlaroConsentModal';
import KlaroConsentButton from './components/CookieConsent/KlaroConsentButton';
import IconButton from './components/IconButton';
import Header from './components/Header/Header';
import Footer from './components/Footer/Footer';
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

  // console.log('===currentScreen', currentScreen); 

  return (
    <>
      {/* hier kommt header */}
      
      <Header/>
      {/* main */}
      <KlaroConsentModal/>
      <main>
        {screenPage[currentScreen]}
      </main>
      {/* hier kommt Footer */}
      <Footer/>
      <KlaroConsentButton/>
    </> 
  )
}

export default App



