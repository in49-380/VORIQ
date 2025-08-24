
import React, {useEffect, useState } from 'react';
import Select from 'react-select';
// import usePageUrl from '../hooks/usePageUrl';
import {useTranslation} from 'react-i18next'
import { useLoader } from '../hooks/useLoader.jsx';
import useSelectState from '../hooks/useSelectState.jsx';
import Button from '../components/Button.jsx';
import DropDown from '../components/DropDown.jsx'

import {requestFromVehicleSelectors} from '../api/dbRequest.jsx'
import {asyncRandomError} from '../api/asyncFunc.jsx';
// import { abortTest} from '../api/asyncFunc.jsx';
// import {asyncHttpErrorTest} from '../api/asyncFunc.jsx';

const CarPage = () => {
  // usePageUrl('/cars')
  const [res,setRes]=useState()
  const {t}=useTranslation()
  
  // const {runApi}=useLoader()
  const {runApi, resultMessage}=useLoader()

  
  //   const handleOnClickError = async () => {
    //   const result = await runApi(abortTest);
//   setRes(result.answer || result.err);
// }
//   const handleOnClickError = async () => {
  //   const result = await runApi(asyncHttpErrorTest);
  //   setRes(result.answer || result.err);
  // }
  const handleOnClickError = async () => {
  await runApi(asyncRandomError);
}

useEffect(()=>{
setRes(resultMessage);
},[resultMessage])
// *******************************************************

const brand=useSelectState(false)
const model=useSelectState (true)
const year=useSelectState(true)
const engine=useSelectState(true)

useEffect(()=>{
  const getBrands=async()=>{
      const data= await runApi(()=>requestFromVehicleSelectors('brands.json'))
      const result=data.map(item=>({
        value:item.id,
        label:item.name
      }))
      brand.setOptions(result)
  }
  getBrands()
},[])

useEffect(()=>{
  const getModels=async()=>{
    if (!brand.value) {
       return;
    }
    const data=await runApi(()=>requestFromVehicleSelectors('models.json'))
    
    const result=data
    .filter(item=>item.brandId===Number(brand.value.value))
    .map(item=>({
      value:item.id,
      label:item.name
    }))

    model.setOptions(result)
  }

  getModels()
},[brand.value])

useEffect(()=>{
  const getYears=async()=>{
    if (!model.value) {
      return
    }
    const data=await runApi(()=>requestFromVehicleSelectors('years.json'))
    const result=data
    .filter(item=>item.modelId===Number(model.value.value))
    .map (item=>({
      value:item.id,
      label:item.year
    }))
    year.setOptions(result)
  }
  getYears()
},[model.value])

useEffect(()=>{
  const getEngine=async()=>{
    if (!year.value) {
      return
    }
    const data=await runApi(()=>requestFromVehicleSelectors('engines.json'))
    console.log('data', data)
    console.log('yearValue', year.value)
    const result=data
    .filter(item=>
            year.value
            .some(y=>item.yearId===Number(y.value))
            ) 
    .map(item=>({
      value:item.id,
      label:item.engine
    }))
    
    console.log('result', result)
    console.log('yearvalue', year.value[0].value)
    engine.setOptions(result)
  }
  getEngine()
},[year.value])

const onBrandChange=(newValue)=>{
    if (newValue===null){
      model.clear()
      year.clear()
      engine.clear()
      brand.softClear()
      } else {
        model.softClear()
        year.softClear()
        engine.softClear()
        brand.setValue(newValue)
        model.setDisabled(false)
    }
}

const onModelChange=(newValue)=>{
   if (newValue===null){
      year.clear()
      engine.clear()
      model.softClear()
   } else {
        year.softClear()
        model.setValue(newValue)
        year.setDisabled(false)
   }
}

const onYearChange=(newValue)=>{
    if (!newValue || newValue.length === 0){
      engine.clear()
      year.softClear()
    } else {
        engine.softClear()
        year.setValue(newValue)
        engine.setDisabled(false)
    }   
}

const onEngineChange=(newValue)=>{
    engine.setValue(newValue)
}


  const className= "px-3 py-1.5 w-50 bg-transparent text-sm text-black cursor-pointer outline-none hover:bg-black/5 focus:bg-black/10 appearance-none";
  const optionClassName= "mt-1 w-full bg-transparent shadow-none border-none outline-none";
  // ***********************************************************
  
return (
  
  <div className="min-h-screen flex flex-col items-center justify-center bg-blue-100">
       <Button 
        onClick={handleOnClickError}
        className='bg-red-500 text-white h-auto'
        children={<>Error Occured <br />It's a TEST-button</>}
        />
  { res && <h2 className='text-red-500 text-2xl'>The asynchronous function is {res}</h2>}
    <div className="h-[60vh] w-[90vw] flex flex-row items-center justify-around bg-blue-100">
        
        <Select
          placeholder="Vehicle Brand"
          value={brand.value}
          options={brand.options}
          onChange={onBrandChange}
          isClearable
          isDisabled={brand.disabled}
          // isMulti
          className={className}
          optionClassName={optionClassName}
        />

        <Select
          placeholder={'Vehicle Model'}
          value={model.value}
          options={model.options}
          onChange={onModelChange}
          isClearable
          isDisabled={model.disabled}
          className={className}
          optionClassName={optionClassName}
        />

        <Select
          placeholder="Year"
          value={year.value}
          options={year.options}
          onChange={onYearChange}
          isClearable
          isMulti
          isDisabled={year.disabled}
          className={className}
          optionClassName={optionClassName}
        />

        <Select
          placeholder="Engine"
          value={engine.value}
          options={engine.options}
          onChange={onEngineChange}
          isClearable
          isDisabled={engine.disabled}
          className={className}
          optionClassName={optionClassName}
        />
      
      </div>

       <div className='flex flex-row w-[90vw] items-center justify-around'>
         <Button 
          // onClick={handleOnClickError}
          className='bg-green-500 text-white h-auto'
          children={'Start Analysis'}
          />
         <Button 
          // onClick={handleOnClickError}
          className='bg-green-500 text-white h-auto'
          children={'New search'}
          />
         <Button 
          // onClick={handleOnClickError}
          className='bg-green-500 text-white h-auto'
          children={'Select another car'}
          />
        </div>
  
    </div>
  );
};

export default CarPage;