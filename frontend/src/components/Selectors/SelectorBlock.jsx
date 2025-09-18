
import { useEffect} from 'react';
import {useTranslation} from 'react-i18next'
import Select, {components as RSComponents} from 'react-select';
import { useLoader } from '../../hooks/useLoader.jsx';
import useSelect from '../../hooks/useSelect.jsx';

import {get} from '../../api/api.js'

const SelectorBlock=({brand, model, year,engine, transmission, wheel, setCurrentStep})=>{
    const {t}=useTranslation()
    const {runApi, setSuccessResult}=useLoader()
    const {setAnalysButtonIsDisabled, isNewSearch, setIsNewSearch}=useSelect()
    const goToStep=(s)=>{setCurrentStep(s)}
      
    const clearForNewSearching=()=>{
       setSuccessResult(null)
       setIsNewSearch(false)
    } 
// *****************Brand init******************
    useEffect(()=>{
        const getBrands=async()=>{
        const result= await runApi((signal)=>get('http://voriq.info:8084/api/v1/catalog/brands',signal))
        brand.setOptions(result)
        }
    getBrands()
    },[])

// *****************Model init******************
    useEffect(()=>{
        const getModels=async()=>{
        console.log('brand.value', brand.value)
        if (!brand.value) {
        return;
        }
        const result=await runApi((signal)=>
        get(`http://voriq.info:8084/api/v1/catalog/brands/${brand.value.id}/models`,signal))
        model.setOptions(result)
        }
    getModels()
    },[brand.value])

// *****************Year init******************
    useEffect(()=>{
        const getYears=async()=>{
        if (!model.value) {
        return
        }
        const result=await runApi((signal)=>
        get(`http://voriq.info:8084/api/v1/catalog/brands/${brand.value.id}/models/${model.value.id}/years`,signal))
        year.setOptions(result)
        }
    getYears()
    },[model.value])

// *****************Engine init******************
    useEffect(()=>{
        const getEngine=async()=>{
        if (!year.value) {
        return
        }
       const result=await runApi((signal)=>
        get(`http://voriq.info:8084/api/v1/catalog/brands/${brand.value.id}/models/${model.value.id}/years/${year.value[0].id}/engines`,signal))
        engine.setOptions(result)
    }
    getEngine()
    },[year.value])

// *****************Transmission init******************
    useEffect(()=>{
        const getTransmission=async()=>{
        if (!engine.value) {
        return
        }
       const result=await runApi((signal)=>
        get(`http://voriq.info:8084/api/v1/catalog/brands/${brand.value.id}/models/${model.value.id}/years/${year.value[0].id}/engines/${engine.value.id}/transmissions`,signal))
        transmission.setOptions(result)
    }
    getTransmission()
    },[engine.value])

// *****************WheelDrive init******************
    useEffect(()=>{
        const getWheel=async()=>{
        if (!transmission.value) {
        return
        }
       const result=await runApi((signal)=>
        get(`http://voriq.info:8084/api/v1/catalog/brands/${brand.value.id}/models/${model.value.id}/years/${year.value[0].id}/engines/${engine.value.id}/transmissions/${transmission.value.id}/wheel_drive`,signal))
        wheel.setOptions(result)
    }
    getWheel()
    },[transmission.value])

// **************Brand Change****************************
    const onBrandChange=(newValue)=>{
       clearForNewSearching()

        if (newValue===null){
          model.clear()
          year.clear()
          engine.clear()
          transmission.clear()
          wheel.clear()
          brand.softClear()
          setAnalysButtonIsDisabled(true)

        } else {
            model.softClear()
            year.softClear()
            engine.softClear()
            transmission.softClear()
            wheel.softClear()
            brand.setValue(newValue)
            model.setDisabled(false)
            goToStep(2)
        }
    }
// **************Model Change****************************
    const onModelChange=(newValue)=>{
       clearForNewSearching()

    if (newValue===null){
        year.clear()
        engine.clear()
        transmission.clear()
        wheel.clear()
        model.softClear()
        setAnalysButtonIsDisabled(true)
    } else {
            year.softClear()
            engine.softClear()
            transmission.softClear()
            wheel.softClear()
            model.setValue(newValue)
            year.setDisabled(false)
            goToStep(3)
    }
    }
// **************Year Change****************************
    const onYearChange=(newValue)=>{
       clearForNewSearching()

        if (!newValue || newValue.length === 0){
          engine.clear()
          transmission.clear()
          wheel.clear()
          year.softClear()
          setAnalysButtonIsDisabled(true)
        } else {
            engine.softClear()
            transmission.softClear()
            wheel.softClear()
            year.setValue(newValue)
            engine.setDisabled(false)
            goToStep(4)
           
        }   
    }
// **************Engine Change****************************
    const onEngineChange=(newValue)=>{
      clearForNewSearching()

        if (!newValue || newValue.length === 0){
          transmission.clear()
          wheel.clear()  
          engine.softClear()
        setAnalysButtonIsDisabled(true)
        } else {
            transmission.softClear()
            wheel.softClear()
            engine.setValue(newValue)
            transmission.setDisabled(false)
            setAnalysButtonIsDisabled(true)
            goToStep(5)
        }
    }
    // **************Transmission Change****************************
    const onTransmissionChange=(newValue)=>{
      clearForNewSearching()

        if (!newValue || newValue.length === 0){
          wheel.clear()
          transmission.softClear()
          setAnalysButtonIsDisabled(true)
        } else {
          wheel.softClear()
          transmission.setValue(newValue)
          wheel.setDisabled(false)
          setAnalysButtonIsDisabled(true)
          goToStep(6)
        }
    }
    // **************WheelDrive Change****************************
    const onWheelChange=(newValue)=>{
      clearForNewSearching()

        if (!newValue || newValue.length === 0){
          wheel.softClear()
          setAnalysButtonIsDisabled(true)
        } else {
          wheel.setValue(newValue)
          setAnalysButtonIsDisabled(false)
          goToStep(7)
        }
    }

     useEffect(()=>{
      if(isNewSearch){
        brand.softClear()
        model.clear()
        year.clear()
        engine.clear()
      }
     },[isNewSearch]) 
    // **************select styling******************
    // **********************************************

     useEffect(()=>{
      if(isNewSearch){
        brand.softClear()
        model.clear()
        year.clear()
        engine.clear()
        transmission.clear()
        wheel.clear()
      }
     },[isNewSearch]) 
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
          width:'30%',

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
              getOptionLabel={(option) => option.value}
              getOptionValue={(option) => option.id}
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
              getOptionLabel={(option) => option.value}
              getOptionValue={(option) => option.id}
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
              getOptionLabel={(option) => option.value}
              getOptionValue={(option) => option.id}
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
              getOptionLabel={(option) => option.value}
              getOptionValue={(option) => option.id}
              placeholder={t('selectorBlock.engine')}
              value={engine.value}
              options={engine.options}
              onChange={onEngineChange}
              isClearable
              isDisabled={engine.disabled}
              styles={customStyles}
              components={{DropdownIndicator}}
            />

            <Select
              id='s5'
              getOptionLabel={(option) => option.value}
              getOptionValue={(option) => option.id}
              placeholder={t('selectorBlock.transmission')}
              value={transmission.value}
              options={transmission.options}
              onChange={onTransmissionChange}
              isClearable
              isDisabled={transmission.disabled}
              styles={customStyles}
              components={{DropdownIndicator}}
            />

            <Select
              id='s6'
              getOptionLabel={(option) => option.value}
              getOptionValue={(option) => option.id}
              placeholder={t('selectorBlock.wheel')}
              value={wheel.value}
              options={wheel.options}
              onChange={onWheelChange}
              isClearable
              isDisabled={wheel.disabled}
              styles={customStyles}
              components={{DropdownIndicator}}
            />
          
          </div>
  )



}
export default SelectorBlock







  