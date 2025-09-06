
import React, {useEffect, useState} from 'react';
// import {useTranslation} from 'react-i18next'
import { useLoader } from '../hooks/useLoader.jsx';
import useSelectState from '../hooks/useSelectState.jsx';
import useSelect from '../hooks/useSelect.jsx';

import {asyncRandomError} from '../api/asyncFunc.jsx';
import SelectorBlock from '../components/Selectors/SelectorBlock.jsx';
import ButtonBlock from '../components/Selectors/ButtonBlock.jsx';

import HintBox from '../components/HintTour/HintBox.jsx';
import Speed from '../components/Speed.jsx';

const CarPage = () => {

  // usePageUrl('/cars')
  // const {t}=useTranslation()

  // const [res,setRes]=useState()
  const {runApi, resultMessage, successResult}=useLoader()
  const [currentStep, setCurrentStep]=useState(1)
  const {isNewSearch}=useSelect()

  useEffect(()=>{
   console.log("kdjfskjf", successResult);
  },[successResult])

  // useEffect(()=>{
  //   console.log('visible', isNewSelectorSetVisible);
  // },[isNewSelectorSetVisible])
  
  // ***************************************************
  // const handleOnClickError = async () => {
  //      await runApi(asyncRandomError);
  // }
  // ****************************************************

    const brand=useSelectState(false)
    const model=useSelectState (true)
    const year=useSelectState(true)
    const engine=useSelectState(true)  

    const hasAllValues = brand.value 
    && model.value 
    && year.value 
    && year.value.length > 0 
    && engine.value;
  
  return (
    
<div className="main-container car-container">

        {/* <Button 
          onClick={handleOnClickError}
          className='bg-red-500 text-white h-auto'
          children={<>Error Occured <br />It's a TEST-button</>}
          />

        <Button
        className='bg-blue-500 text-white h-auto'
        children={'Swagger'}
        />

    { res && <h2 className='text-red-500 text-2xl'>The asynchronous function is {res}</h2>}
     */}
    {/* {successResult &&
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

      </div>} */}

      <div className='topCarSite'>
       { currentStep>4 && !isNewSearch && hasAllValues &&
        <div className='carData'>
            <h2>You have selected this car:</h2>
            <div>
            <strong>Brand:</strong> {brand.value.label} 
          </div>
          <div>
            <strong>Model:</strong> {model.value.label} 
          </div>
          <div>
            <strong>Years:</strong> {year.value.map(y=>y.label).join(', ')}
          </div>
          <div>
            <strong>Engine:</strong> {engine.value.label} 
          </div>
          <h2>Click <strong>Analyze</strong> and we will collect the data for you.</h2>
        </div>}

       { successResult && !isNewSearch &&
        <div className='resultData'>
            <h1>Here will be the results of the selected car’s analysis.</h1>
            <h2>* Not necessarily here — it will depend on the design — but they will be shown.</h2>
        </div>}
      </div>
      {/* <Speed/> */}
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

      <HintBox
        currentStep={currentStep}
        setCurrentStep={setCurrentStep}
      />
</div>

    );
  };
  export default CarPage;