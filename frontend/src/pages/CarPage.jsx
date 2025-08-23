
import React, {useEffect, useState } from 'react';
// import usePageUrl from '../hooks/usePageUrl';
import {useTranslation} from 'react-i18next'
import Button from '../components/Button.jsx';
import { useLoader } from '../hooks/useLoader.jsx';

import {asyncRandomError} from '../api/asyncFunc.jsx';
// import { abortTest} from '../api/asyncFunc.jsx';
// import {asyncHttpErrorTest} from '../api/asyncFunc.jsx';

const CarPage = () => {
  // usePageUrl('/cars')
  const [res,setRes]=useState()
  const {t}=useTranslation()
  const {runApi, resultMessage}=useLoader()
  

//   const handleOnClickError = async () => {
//   const result = await runApi(abortTest);
//   setRes(result.answer || result.err);
// }
  const handleOnClickError = async () => {
  await runApi(asyncRandomError);
}
 useEffect(()=>{
   setRes(resultMessage);
 },[resultMessage])
//   const handleOnClickError = async () => {
//   const result = await runApi(asyncHttpErrorTest);
//   setRes(result.answer || result.err);
// }


  return (

    <div className="min-h-screen flex flex-col items-center justify-center bg-blue-100">
      <h1 className="text-4xl font-semibold text-blue-700">
        {t('carPage')}
      </h1>

     <Button 
      onClick={handleOnClickError}
      className='bg-red-500 text-white h-auto'
      children={<>Error Occured <br />It's a TEST-button</>}
      />
     { res && <h2 className='text-red-500 text-2xl'>The asynchronous function is {res}</h2>}

    </div>
  );
};

export default CarPage;