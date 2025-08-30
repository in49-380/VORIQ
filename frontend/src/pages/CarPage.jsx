
import React, {useEffect, useState} from 'react';
// import {useTranslation} from 'react-i18next'
import { useLoader } from '../hooks/useLoader.jsx';
import useSelect from '../hooks/useSelect.jsx';
import useSelectState from '../hooks/useSelectState.jsx';

import Button from '../components/Button.jsx';
import {asyncRandomError} from '../api/asyncFunc.jsx';
import SelectorBlock from '../components/Selectors/SelectorBlock.jsx';
import ButtonBlock from '../components/Selectors/ButtonBlock.jsx';

import HintBox from '../components/HintTour/HintBox.jsx';


const CarPage = () => {

  // usePageUrl('/cars')
  // const {t}=useTranslation()

  const [res,setRes]=useState()
  const {runApi, resultMessage, successResult}=useLoader()
  const {isNewSelectorSetVisible}=useSelect()
  const [currentStep, setCurrentStep]=useState(1)
  

  useEffect(()=>{
    setRes(resultMessage);
  },[resultMessage])

  useEffect(()=>{
    console.log('visible', isNewSelectorSetVisible);
  },[isNewSelectorSetVisible])
  
  // ***************************************************
  const handleOnClickError = async () => {
       await runApi(asyncRandomError);
  }
  // ****************************************************

    const brand=useSelectState(false)
    const model=useSelectState (true)
    const year=useSelectState(true)
    const engine=useSelectState(true)  
  
  return (
    
<div className="relative h-[100vh] flex flex-col items-center justify-center bg-blue-100">

        <Button 
          onClick={handleOnClickError}
          className='bg-red-500 text-white h-auto'
          children={<>Error Occured <br />It's a TEST-button</>}
          />

        <Button
        className='bg-blue-500 text-white h-auto'
        children={'Swagger'}
        />

    { res && <h2 className='text-red-500 text-2xl'>The asynchronous function is {res}</h2>}
    
    {successResult &&
      <div className='bg-white border-2 border-green-500 flex flex-col items-center justify-center p-4 w-64 h-32 m-4'>
            <h2 className='text-green-500 text-xl'>Result of Analyse:</h2>
          <div>
            <strong>Brand:</strong> {successResult.brandId?.label} (ID: {successResult.brandId?.value})
          </div>
          <div>
            <strong>Model:</strong> {successResult.modelId?.label} (ID: {successResult.modelId?.value})
          </div>
          <div>
            <strong>Years:</strong> {successResult.yearIds?.join(', ')}
          </div>
          <div>
            <strong>Engine:</strong> {successResult.engineIds?.label} (ID: {successResult.engineIds?.value})
          </div>

      </div>}

      
      <HintBox
        currentStep={currentStep}
        setCurrentStep={setCurrentStep}
      />

      <SelectorBlock
        brand={brand}
        model={model}
        year={year}
        engine={engine}
        setCurrentStep={setCurrentStep}
      />

      <ButtonBlock
        brand={brand}
        model={model}
        year={year}
        engine={engine}
        setCurrentStep={setCurrentStep}
      />

</div>

    );
  };
  export default CarPage;