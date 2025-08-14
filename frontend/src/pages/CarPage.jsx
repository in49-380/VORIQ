
import React from 'react';
// import usePageUrl from '../hooks/usePageUrl';
import {useTranslation} from 'react-i18next'

const CarPage = () => {
  // usePageUrl('/cars')
  const {t}=useTranslation()
  return (
    
    <div className="min-h-screen flex items-center justify-center bg-blue-100">
      <h1 className="text-4xl font-semibold text-blue-700">
        {t('carPage')}
      </h1>
    </div>
  );
};

export default CarPage;
