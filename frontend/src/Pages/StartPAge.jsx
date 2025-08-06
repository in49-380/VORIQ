// RegistrationPage.jsx
import React from 'react';
import GoogleAuthButton from '../oauth/GoogleAuthButton';

const RegistrationPage = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-pink-100">
      <h1 className="text-4xl font-semibold text-pink-700">
        Registration Page
      </h1>
     <GoogleAuthButton/>

    </div>
  );
};

export default RegistrationPage;
