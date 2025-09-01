
import { useEffect} from 'react';
import {useTranslation} from 'react-i18next'
import Select, {components as RSComponents} from 'react-select';
// import { motion, AnimatePresence } from "framer-motion";


import { useLoader } from '../../hooks/useLoader.jsx';
import useSelect from '../../hooks/useSelect.jsx';

import {requestFromVehicleSelectors} from '../../api/dbRequest.jsx'
import brandAnalyse from '../../../public/fakeDB/fakeAnalys.jsx';
import { modelAnalyse, yearAnalyse, engineAnalyse } from '../../../public/fakeDB/fakeAnalys.jsx';


const SelectorBlock=({brand, model, year,engine, setCurrentStep})=>{
    const {t}=useTranslation()
    const {runApi}=useLoader()
    const {setAnalysButtonIsDisabled}=useSelect()
    const goToStep=(s)=>{setCurrentStep(s)}
      
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
        setAnalysButtonIsDisabled(true)
        } else {
            model.softClear()
            year.softClear()
            engine.softClear()
            brand.setValue(newValue)
            model.setDisabled(false)
            goToStep(2)
        }
    }

    const onModelChange=(newValue)=>{
    if (newValue===null){
        year.clear()
        engine.clear()
        model.softClear()
        setAnalysButtonIsDisabled(true)
    } else {
            year.softClear()
            model.setValue(newValue)
            year.setDisabled(false)
            goToStep(3)
    }
    }

    const onYearChange=(newValue)=>{
        if (!newValue || newValue.length === 0){
        engine.clear()
        year.softClear()
        setAnalysButtonIsDisabled(true)
        } else {
            engine.softClear()
            year.setValue(newValue)
            engine.setDisabled(false)
            goToStep(4)
           
        }   
    }

    const onEngineChange=(newValue)=>{
        if (!newValue || newValue.length === 0){
        engine.softClear()
        setAnalysButtonIsDisabled(true)
        } else {
        engine.setValue(newValue)
        setAnalysButtonIsDisabled(false)
        goToStep(5)
        }
    }


    // **************select styling******************
    // **********************************************

    const DropdownIndicator=(props)=>{
      return (
      <RSComponents.DropdownIndicator {...props}>
          <svg
            style={{ stroke: props.isDisabled ? 'var(--color-light)' : 'var(--color-primary)', 
            fill:'none',
            strokeWidth:'2px'}}
            height="2rem" width="2rem" viewBox="0 0 20 20"
          >
            <path d="M4 6 L10 13 L16 6"  />
          </svg>
      </RSComponents.DropdownIndicator>
  );
    }

    
    const customStyles={
        container:(provided)=>({
          ...provided,
          width:'20%',
        }),

        control: (provided, state)=>({
            ...provided,
            backgroundColor: 'var(--background-light)',
                      
            '&:hover': { backgroundColor: 'var(--background-secondary)',
                         border:'none'   
             },
            border:'none',
            boxShadow: state.isFocused ? '0 0 0 0 transparent' : 'none', 
        }),

         placeholder : (provided, state) => ({
            ...provided,
            color: state.isDisabled
            ? 'var(--color-light)'
            : 'var(--color-primary)'
          }),

        DropdownIndicator:(provided, state)=>({
           ...provided,
           svg: {
              fill: state.isDisabled
            ? 'var(--color-light)'
            : 'var(--color-primary)'
          }
        }),

        menu: (provided) => ({
            ...provided,
           borderRadius: '0.5rem',

        }),

        menuList: (provided) => ({
            ...provided,
            maxHeight: "30rem",
            "::-webkit-scrollbar": {
              width: "0.8rem",
            },
            "::-webkit-scrollbar-track": {
              background: "transparent",
            },
            "::-webkit-scrollbar-thumb": {
              background: "var(--background-secondary)",
              borderRadius: "4px",
            },
        }),

        singleValue: (provided) => ({
            ...provided,
            color: 'var(--color-title)',
            
        }),

        multiValue: (provided) => ({
          ...provided,
          backgroundColor: "none",
          display: "flex",
          alignItems: "center",
          gap: "4px",
        }),

        option: (provided, state) => ({
            ...provided,
            backgroundColor: state.isFocused
                ? 'var(--background-dark)'
                : 'var(--background-primary)',
            color: state.isFocused ? 'var(--color-light)' : 'null',
            cursor: 'pointer',
            userSelect: "none",
             ":active": {
              ...provided[":active"],
              backgroundColor: "var(--backgound-primary)",
    },
         }),

    }

  return(
      <div className="selector_container">
            
            
            <Select
              id='s1'
              placeholder={t('selectorBlock.brand')}
              value={brand.value}
              options={brand.options}
              onChange={onBrandChange}
              isClearable
              isDisabled={brand.disabled}
              styles={customStyles}
              components={{DropdownIndicator}}
            />
      

            <Select
              id='s2'
              placeholder={t('selectorBlock.model')}
              value={model.value}
              options={model.options}
              onChange={onModelChange}
              isClearable
              isDisabled={model.disabled}
              styles={customStyles}
              components={{DropdownIndicator}}


            />
    
            <Select
              id='s3'
              placeholder={t('selectorBlock.year')}
              value={year.value}
              options={year.options}
              onChange={onYearChange}
              isClearable
              isMulti
              isDisabled={year.disabled}
              styles={customStyles}
              components={{DropdownIndicator}}


            />
    
            <Select
              id='s4'

              placeholder={t('selectorBlock.engine')}
              value={engine.value}
              options={engine.options}
              onChange={onEngineChange}
              isClearable
              isDisabled={engine.disabled}
              styles={customStyles}
              components={{DropdownIndicator}}


            />
          
          </div>
  )



}
export default SelectorBlock







  