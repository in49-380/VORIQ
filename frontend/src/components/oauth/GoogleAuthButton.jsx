import  useGoogleAuth from '../../hooks/useGoogleAuth';

import GoogleIcon from '@mui/icons-material/Google';
// import { useContext } from 'react';
// import AuthContext from './AuthContext';
import Button from '../Button';


const GoogleAuthButton=()=>{
    const {login}=useGoogleAuth();
    // *****************Update: fixed so that only the JWT is now stored in localStorage,
    // **************** the Google access token is no longer saved
    // *********************************************************************************
    // const {token}=useContext(AuthContext)
    // return !token?(
    //     <Button onClick={login} 
    //             className='bg-gray-400'>
    //         <GoogleIcon />
    //     </Button>
    //     ):(null)
    return (
        <Button onClick={login} 
                className='bg-gray-400'>
            <GoogleIcon />
        </Button>
        )
    
}
export default GoogleAuthButton