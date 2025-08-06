import { useGoogleLogin } from '@react-oauth/google';
import { useEffect } from 'react';
import { sendTokenToBackend } from '../api/googleAuth';
import { useContext } from 'react';
import AuthContext from './AuthContext';



export default function useGoogleAuth() {
    const {token, setToken } = useContext(AuthContext);

  useEffect(() => {
    const savedToken = localStorage.getItem('google_access_token');
    if (savedToken) {
      setToken(savedToken);
    }
  }, []);

  const login = useGoogleLogin({
    onSuccess: async tokenResponse =>{
      console.log('Access Token:', tokenResponse.access_token);
      setToken(tokenResponse.access_token);
      localStorage.setItem('google_access_token', tokenResponse.access_token);
      const backendResponse = await sendTokenToBackend(tokenResponse.access_token);
      setToken(backendResponse.token)
      console.log('Answer From Server:', backendResponse);
    },
    onError: errorResponse => {
      console.error('Login Failed:', errorResponse);
    },
  });

  const logout = () => {
    setToken(null);
    localStorage.removeItem('google_access_token'); 
  };
  
  return { login, logout, token };
}
