// RegistrationPage.jsx
import React from 'react';
import GoogleAuthButton from '../oauth/GoogleAuthButton';
// import usePageUrl from '../hooks/usePageUrl';

const RegistrationPage = () => {
  // usePageUrl('/login')

  return (
    <div className="min-h-screen flex items-center justify-around bg-pink-100">
      <h1 className="text-4xl font-semibold text-pink-700">
        Registration Page
      </h1>
     <GoogleAuthButton/>

    </div>
  );
};

export default RegistrationPage;
