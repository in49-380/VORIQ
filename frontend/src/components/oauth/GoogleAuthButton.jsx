import  useGoogleAuth from '../../hooks/useGoogleAuth';

import { FcGoogle } from 'react-icons/fc';
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
        <Button id='b17'
        onClick={login} 
        className='google-auth-button'>
            <FcGoogle size={240} />
        </Button>
        )
    
}
export default GoogleAuthButton