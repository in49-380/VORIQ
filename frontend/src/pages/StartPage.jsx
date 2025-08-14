// RegistrationPage.jsx
import React from 'react';
import GoogleAuthButton from '../components/oauth/GoogleAuthButton';
// import usePageUrl from '../hooks/usePageUrl';
import {useTranslation} from 'react-i18next'

const RegistrationPage = () => {
  // usePageUrl('/login')
  const {t}=useTranslation()
  return (
    <div className="min-h-screen flex flex-col items-center justify-around bg-pink-100">
      <h1 className='text-5xl font-semibold text-red-700'>
          {t('welcome')}
      </h1>
     <GoogleAuthButton/>
    

    </div>
  );
};

export default RegistrationPage;
