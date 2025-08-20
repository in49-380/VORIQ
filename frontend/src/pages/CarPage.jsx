
import React, { Children, useState } from 'react';
// import usePageUrl from '../hooks/usePageUrl';
import {useTranslation} from 'react-i18next'
import LoaderModal from '../components/loader/LoaderModal';
import {useLoader} from '../hooks/useLoader';
import { asyncFunc } from '../api/asyncFunc.jsx';
import Button from '../components/Button.jsx';

const CarPage = () => {
  // usePageUrl('/cars')
  const [res,setRes]=useState()
  const {isLoading, isTooLongLoading,runWithLoader}=useLoader()
  const {t}=useTranslation()
  
  const handleOnClick=async()=>
   { const result=await  runWithLoader(asyncFunc)
    setRes(result.answer||result.err)
  }


  return (

    <div className="min-h-screen flex flex-col items-center justify-center bg-blue-100">
      <h1 className="text-4xl font-semibold text-blue-700">
        {t('carPage')}
      </h1>
    <LoaderModal isLoading={isLoading} isTooLongLoading={isTooLongLoading}/>

    {/*test***********test***********test****  */}
    <Button 
      onClick={handleOnClick}
      className='bg-red-500 text-white'
      children='Start Loader'
      />

     { res && <h2 className='text-red-500 text-2xl'>The asynchronous function is {res}</h2>}
    {/* /////////////////////////////////////// */}

    </div>
  );
};

export default CarPage;