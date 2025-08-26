
import { useEffect} from 'react';
import {useTranslation} from 'react-i18next'
import Select from 'react-select';
// import { motion, AnimatePresence } from "framer-motion";


import { useLoader } from '../../hooks/useLoader.jsx';
import useSelect from '../../hooks/useSelect.jsx';

import {requestFromVehicleSelectors} from '../../api/dbRequest.jsx'
import brandAnalyse from '../../../public/fakeDB/fakeAnalys.jsx';
import { modelAnalyse, yearAnalyse, engineAnalyse } from '../../../public/fakeDB/fakeAnalys.jsx';


const SelectorBlock=({brand,model,year,engine})=>{
    const {t}=useTranslation()
    const {runApi}=useLoader()
    const {setAnalysButtonIsDisabled}=useSelect()
      
    useEffect(()=>{
        const getBrands=async()=>{
        const data= await runApi((opt)=>requestFromVehicleSelectors('brands.json',opt))
        const result=brandAnalyse(data)
        brand.setOptions(result)
        }
    getBrands()
    },[])

    useEffect(()=>{
        const getModels=async()=>{
        if (!brand.value) {
        return;
        }
        const data=await runApi((opt)=>requestFromVehicleSelectors('models.json', opt))
        const result=modelAnalyse(data, brand.value)
        model.setOptions(result)
        }
    getModels()
    },[brand.value])

    useEffect(()=>{
        const getYears=async()=>{
        if (!model.value) {
        return
        }
        const data=await runApi((opt)=>requestFromVehicleSelectors('years.json',opt))
        const result=yearAnalyse(data,model.value)
        year.setOptions(result)
        }
    getYears()
    },[model.value])

    useEffect(()=>{
        const getEngine=async()=>{
        if (!year.value) {
        return
        }
        const data=await runApi((opt)=>requestFromVehicleSelectors('engines.json',opt))
        const result=engineAnalyse(data, year.value)
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
        setAnalysButtonIsDisabled(false)
    }

    const className= "px-3 py-1.5 w-50 bg-transparent text-sm text-black cursor-pointer outline-none hover:bg-black/5 focus:bg-black/10 appearance-none";
    const optionClassName= "mt-1 w-full bg-transparent shadow-none border-none outline-none";

  return(
      <div className="h-[30vh] w-[90vw] flex flex-row items-center justify-around bg-blue-100">
            
            <Select
              placeholder={t('selectorBlock.brand')}
              value={brand.value}
              options={brand.options}
              onChange={onBrandChange}
              isClearable
              isDisabled={brand.disabled}
              className={className}
              optionClassName={optionClassName}
            />
    
            <Select
              placeholder={t('selectorBlock.model')}
              value={model.value}
              options={model.options}
              onChange={onModelChange}
              isClearable
              isDisabled={model.disabled}
              className={className}
              optionClassName={optionClassName}
            />
    
            <Select
              placeholder={t('selectorBlock.year')}
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
              placeholder={t('selectorBlock.engine')}
              value={engine.value}
              options={engine.options}
              onChange={onEngineChange}
              isClearable
              isDisabled={engine.disabled}
              className={className}
              optionClassName={optionClassName}
            />
          
          </div>
  )



}
export default SelectorBlock







  