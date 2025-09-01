import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { GoogleOAuthProvider } from '@react-oauth/google';

import App from './App.jsx'
import { AuthProvider } from './components/oauth/AuthContext.jsx';
import { ScreenProvider } from './components/ScreenProvider';
import { LoaderProvider } from './components/error_loaderModal/LoaderProvider.jsx';
import { SelectProvider } from './components/Selectors/SelectProvider.jsx';
import { ThemeProvider } from './components/ThemeSwitch/ThemeProvider.jsx';

import i18nInit from './components/i18n/i18nInit.js';

import './index.css'
import './styles/main.scss'

const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
const URI='https://voriq.info'

const render=()=>{
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ThemeProvider>
      <GoogleOAuthProvider clientId={clientId}>
        <LoaderProvider>
         <SelectProvider>
           <ScreenProvider>
            <AuthProvider>
              <App />
            </AuthProvider>
          </ScreenProvider>
         </SelectProvider>
        </LoaderProvider>
       </GoogleOAuthProvider>
    </ThemeProvider>
  </StrictMode>)
}

i18nInit()
.then(render)
.catch(err=>{
  console.error('i18n init failed', err)
  render()})