import  useGoogleAuth from '../../hooks/useGoogleAuth';

import GoogleIcon from '@mui/icons-material/Google';
import { useContext } from 'react';
import AuthContext from './AuthContext';
import Button from '../Button';


const GoogleAuthButton=()=>{
    const {token}=useContext(AuthContext)
    const {login}=useGoogleAuth();
    return !token?(
        <Button onClick={login} 
                className='bg-gray-400'>
            <GoogleIcon />
        </Button>
        ):(null)
    
}
export default GoogleAuthButton