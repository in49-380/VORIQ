import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { GoogleOAuthProvider } from '@react-oauth/google';

import App from './App.jsx'
import { AuthProvider } from './oauth/AuthContext.jsx';
import { ScreenProvider } from './components/ScreenProvider';

import './index.css'

const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
console.log(import.meta.env.VITE_GOOGLE_CLIENT_ID);

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <GoogleOAuthProvider clientId={clientId}>
      <ScreenProvider>
        <AuthProvider>
          <App />
        </AuthProvider>
      </ScreenProvider>
    </GoogleOAuthProvider>
  </StrictMode>,
);
