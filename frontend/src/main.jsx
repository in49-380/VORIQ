import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

import { GoogleOAuthProvider } from '@react-oauth/google';
import {AuthProvider} from './components/oauth/AuthContext.jsx';

import i18nInit from './components/i18n/i18nInit.js';



const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
console.log(import.meta.env.VITE_GOOGLE_CLIENT_ID);

const render=()=>{
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <GoogleOAuthProvider clientId={clientId}>
    <AuthProvider>
      <App />
    </AuthProvider>
  </GoogleOAuthProvider>
  </StrictMode>)
}

i18nInit()
.then(render)
.catch(err=>{
  console.error('i18n init failed', err)
  render()})

