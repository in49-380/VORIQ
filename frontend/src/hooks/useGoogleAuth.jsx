import { useGoogleLogin } from '@react-oauth/google';
// import { useEffect } from 'react';
import { sendTokenToBackend } from '../api/googleAuth';
import { useContext } from 'react';
import AuthContext from '../components/oauth/AuthContext';



export default function useGoogleAuth() {
    const {token, setToken } = useContext(AuthContext);
      // *****************Update: fixed so that only the JWT is now stored in localStorage,
      // **************** the Google access token is no longer saved
      // *********************************************************************************
  // useEffect(() => {
  //   const savedToken = localStorage.getItem('jwt_token');
  //   if (savedToken) {
  //     setToken(savedToken);
  //   }
  // }, []);

  const login = useGoogleLogin({
    onSuccess: async tokenResponse =>{
      // *****************Update: fixed so that only the JWT is now stored in localStorage,
      // **************** the Google access token is no longer saved
      // *********************************************************************************
      // setToken(tokenResponse.access_token);
      // localStorage.setItem('google_access_token', tokenResponse.access_token);
      const backendResponse = await sendTokenToBackend(tokenResponse.access_token);
      setToken(backendResponse.token)
      localStorage.setItem('jwt_token', backendResponse.token);
    },
    onError: errorResponse => {
      console.error('Login Failed:', errorResponse);
    },
  });

  const logout = () => {
    setToken(null);
    localStorage.removeItem('jwt_token'); 
  };
  
  return { login, logout, token };
}
