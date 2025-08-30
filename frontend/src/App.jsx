import { useContext, useEffect } from 'react';

import './App.css'
import LogoutButton from './components/oauth/LogoutButton'
import AuthContext from "./components/oauth/AuthContext";
import RegistrationPage from './pages/StartPage';
import CarPage from './pages/CarPage';
import KlaroConsentModal from './components/CookieConsent/KlaroConsentModal';
import KlaroConsentButton from './components/CookieConsent/KlaroConsentButton';
import Header from './components/Header/Header';
import Footer from './components/Footer/Footer';
import { useScreen } from './hooks/useScreen';
import LoaderModal from './components/error_loaderModal/LoaderModal';
import ErrorModal from './components/error_loaderModal/ErrorModal';

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
  const { currentScreen, setCurrentScreen } = useScreen();

  // if (!token && currentScreen !== 'login') {
  //   setCurrentScreen('login');
  // }

  useEffect(() => {
  if (!token) {
    setCurrentScreen('login');
  } else if (currentScreen === 'login') {
    setCurrentScreen('cars'); 
  }
}, [token, currentScreen, setCurrentScreen]);


  return (
    <>
      <Header/>
      {/* main */}
      <KlaroConsentModal/>
      <main>
        <LoaderModal/>
        <ErrorModal/>
        {screenPage[currentScreen]}
      </main>
      <Footer/>
      <KlaroConsentButton/>
    </> 
  )
}

export default App



