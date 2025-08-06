import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

import { GoogleOAuthProvider } from '@react-oauth/google';
import { useContext } from 'react';
import {AuthProvider} from './oauth/AuthContext.jsx';


const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
console.log(import.meta.env.VITE_GOOGLE_CLIENT_ID);


createRoot(document.getElementById('root')).render(
  <StrictMode>
     <GoogleOAuthProvider clientId={clientId}>

     <AuthProvider>
      <App />
    </AuthProvider>
  </GoogleOAuthProvider>
  </StrictMode>,
)
