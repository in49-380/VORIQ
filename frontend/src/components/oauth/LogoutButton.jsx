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
        <Button onClick={handleOnClick} 
                children={t('logout')} 
                style='fixed top-4 right-4'/>
    ):(null)
}
export default LogoutButton