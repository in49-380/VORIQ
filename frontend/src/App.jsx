import './App.css'
import GoogleAuthButton from './oauth/GoogleAuthButton'
import LogoutButton from './oauth/LogoutButton'
import AuthContext from "./oauth/AuthContext";
import { useContext } from "react";
import RegistrationPage from './Pages/StartPAge';
import CarPage from './Pages/CarPage';


function App() {
  const { token } = useContext(AuthContext);
  return !token?
    <>
    <RegistrationPage/>
    </>:
    <>
    <CarPage/>
    <LogoutButton/>
    </>
  
}

export default App
