import useGoogleAuth from "../../hooks/useGoogleAuth";
import AuthContext from "./AuthContext";
import { useContext } from "react";
import Button from '../Button';
import {useTranslation} from 'react-i18next'

const LogoutButton=()=>{
    const {token}=useContext(AuthContext);
    const {logout}=useGoogleAuth() || {}
    const {t}=useTranslation()
    const handleOnClick=()=>{logout()}
    
    
    return token?(
        <Button id='b18'
                onClick={handleOnClick} 
                children={t('logout')} 
                className="logout-button"
        />
    ):(null)
}
export default LogoutButton