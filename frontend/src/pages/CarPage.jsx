
import React from 'react';
// import usePageUrl from '../hooks/usePageUrl';
import {useTranslation} from 'react-i18next'
import { useState } from 'react';
import LoaderModal from '../components/loader/LoaderModal';

const CarPage = () => {
  // usePageUrl('/cars')
  const [isLoading, setIsLoading]=useState(false)
  const {t}=useTranslation()
  const handleOnClick=()=>{
     setIsLoading(true)

     // test
        setTimeout(() => {
         setIsLoading(false);
       }, 3000); 

  }


  return (
    
    <div className="min-h-screen flex flex-col items-center justify-center bg-blue-100">
      <h1 className="text-4xl font-semibold text-blue-700">
        {t('carPage')}
      </h1>
    <button
    className='px-4 py-2 bg-blue-500 text-white rounded-md' 
    onClick={handleOnClick}>
    Start loader
    </button>
    <LoaderModal isLoading={isLoading}/>
    </div>
  );
};

export default CarPage;
