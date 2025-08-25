
import React, {useEffect, useState } from 'react';
// import {useTranslation} from 'react-i18next'
import { useLoader } from '../hooks/useLoader.jsx';
import useSelectState from '../hooks/useSelectState.jsx';

import Button from '../components/Button.jsx';
import {asyncRandomError} from '../api/asyncFunc.jsx';
import SelectorBlock from '../components/SelectorBlock.jsx';
import ButtonBlock from '../components/ButtonBlock.jsx';

const CarPage = () => {
  // usePageUrl('/cars')
  // const {t}=useTranslation()
  
  const [res,setRes]=useState()
  const {runApi, resultMessage}=useLoader()
  useEffect(()=>{
    setRes(resultMessage);
  },[resultMessage])
  
  // ***************************************************
  const handleOnClickError = async () => {
       await runApi(asyncRandomError);
  }
  // ****************************************************

  const [analysResponse, setAnalysResponse]=useState()
  const [analysButtonIsDisabled, setAnalysButtonIsDisabled]=useState(true)

    const brand=useSelectState(false)
    const model=useSelectState (true)
    const year=useSelectState(true)
    const engine=useSelectState(true)  
    
  return (
    
    <div className="min-h-[80vh] flex flex-col items-center justify-center bg-blue-100">
        <Button 
          onClick={handleOnClickError}
          className='bg-red-500 text-white h-auto'
          children={<>Error Occured <br />It's a TEST-button</>}
          />

    { res && <h2 className='text-red-500 text-2xl'>The asynchronous function is {res}</h2>}
    {analysResponse && 
      <div className='bg-white border-2 border-green-500 flex flex-col items-center justify-center p-4 w-64 h-32 m-4'>
        <h2 className='text-green-500 text-xl'>Result of Analyse:</h2>
      <div>
        <strong>Brand:</strong> {analysResponse.brandId?.label} (ID: {analysResponse.brandId?.value})
      </div>
      <div>
        <strong>Model:</strong> {analysResponse.modelId?.label} (ID: {analysResponse.modelId?.value})
      </div>
      <div>
        <strong>Years:</strong> {analysResponse.yearIds?.join(', ')}
      </div>
      <div>
        <strong>Engine:</strong> {analysResponse.engineIds?.label} (ID: {analysResponse.engineIds?.value})
      </div>

      </div>}


    <SelectorBlock
    brand={brand}
    model={model}
    year={year}
    engine={engine}
    setButtonIsDisabled={setAnalysButtonIsDisabled}
  />

  <ButtonBlock
    brand={brand}
    model={model}
    year={year}
    engine={engine}
    onStartAnalysis={setAnalysResponse}
    buttonIsDisabled={analysButtonIsDisabled}
  />

      </div>
    );
  };
  export default CarPage;