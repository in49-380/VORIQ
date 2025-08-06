import  useGoogleAuth from './useGoogleAuth';

import GoogleIcon from '@mui/icons-material/Google';
import { useContext } from 'react';
import AuthContext from './AuthContext';


const GoogleAuthButton=()=>{
    const {token}=useContext(AuthContext)
    const {login}=useGoogleAuth();
    return !token?(
        <button onClick={login}>
            <GoogleIcon />
        </button>):(null)
    
}
export default GoogleAuthButton