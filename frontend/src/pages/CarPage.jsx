
import React, {useState} from 'react';
import {useTranslation} from 'react-i18next'
import { useLoader } from '../hooks/useLoader.jsx';
import useSelectState from '../hooks/useSelectState.jsx';
import useSelect from '../hooks/useSelect.jsx';

import SelectorBlock from '../components/Selectors/SelectorBlock.jsx';
import ButtonBlock from '../components/Selectors/ButtonBlock.jsx';

import HintBox from '../components/HintTour/HintBox.jsx';
import Speed from '../components/Speed.jsx';

const CarPage = () => {

  const {t}=useTranslation()
  // const [res,setRes]=useState()
  // const {runApi, resultMessage, successResult}=useLoader()
  const {successResult}=useLoader()
  const [currentStep, setCurrentStep]=useState(1)
  const {isNewSearch}=useSelect()

 // const handleOnClickError = async () => {
  //      await runApi(asyncRandomError);
  // }
  // ****************************************************

    const brand=useSelectState(false)
    const model=useSelectState (true)
    const year=useSelectState(true)
    const engine=useSelectState(true)  
    const transmission=useSelectState(true)  
    const wheel=useSelectState(true)  

    const hasAllValues = brand.value 
    && model.value 
    && year.value 
    && year.value.length > 0 
    && engine.value
    && transmission.value
    && wheel.value

  
  return (
    
<div className="main-container ">
      {/* <div className='topCarSite'>
       { currentStep>6 && !isNewSearch && hasAllValues &&
        <div className='carData'>
            <h2>You have selected this car:</h2>
            <div>
            <strong>Brand:</strong> {brand.value.value} 
          </div>
          <div>
            <strong>Model:</strong> {model.value.value} 
          </div>
          <div>
            <strong>Years:</strong> {year.value.map(y=>y.value).join(', ')}
          </div>
          <div>
            <strong>Engine:</strong> {engine.value.value} 
          </div>
          <div>
            <strong>Transmission:</strong> {transmission.value.value} 
          </div>
          <div>
            <strong>Wheel drive:</strong> {wheel.value.value} 

          </div>
          <h2>Click <strong>Analyze</strong> and we will collect the data for you.</h2>
        </div>}

       { successResult && !isNewSearch &&
        <div className='resultData'>
            <h1>Here will be the results of the selected car’s analysis.</h1>
            <h2>* Not necessarily here — it will depend on the design — but they will be shown.</h2>
        </div>}
      </div>
      <Speed/> */}


   <div className='car-container'>
       <div className='experience-block'>
  
         <div className='textt'>
           <div className='first'>
             <p> {t('experience.discover')}</p>
             <p>{t('experience.decide')}</p>
             <p>{t('experience.enjoy')}</p>
           </div>
           <p className='second'>{t('experience.title')}</p>
           <p className='third'>{t('experience.bigText1')}
            </p>
           <p className='third'>{t('experience.bigText2')}</p>
         </div>
  
         <div className='topCarSite'>
       { currentStep>6 && !isNewSearch && hasAllValues &&
        <div className='carData'>
            <h2>You have selected this car:</h2>
            <div>
            <strong>Brand:</strong> {brand.value.value} 
          </div>
          <div>
            <strong>Model:</strong> {model.value.value} 
          </div>
          <div>
            <strong>Years:</strong> {year.value.map(y=>y.value).join(', ')}
          </div>
          <div>
            <strong>Engine:</strong> {engine.value.value} 
          </div>
          <div>
            <strong>Transmission:</strong> {transmission.value.value} 
          </div>
          <div>
            <strong>Wheel drive:</strong> {wheel.value.value} 

          </div>
          <h2>Click <strong>Analyze</strong> and we will collect the data for you.</h2>
        </div>}

       { successResult && !isNewSearch &&
        <div className='resultData'>
            <h1>Here will be the results of the selected car’s analysis.</h1>
            <h2>* Not necessarily here — it will depend on the design — but they will be shown.</h2>
        </div>}
      </div>
  
          <div className='selector-button-wrap'>
            <SelectorBlock
              brand={brand}
              model={model}
              year={year}
              engine={engine}
              transmission={transmission}
              wheel={wheel}
              setCurrentStep={setCurrentStep}
            />
      
            <ButtonBlock
              brand={brand}
              model={model}
              year={year}
              engine={engine}
              transmission={transmission}
              wheel={wheel}
              setCurrentStep={setCurrentStep}
            />
          </div>
       </div>   
  
        <HintBox
              currentStep={currentStep}
              setCurrentStep={setCurrentStep}
        />
     
  
  
  
        <div className='chooseBlock' id='chooseBlock'>
          <div className='choose-us-title'>
              {t('choose.title')}
          </div>
          <div className='choose-us-1'>
             <div className='chooseImage'></div>
             <div className='chooseText'>
                 <p>{t('choose.text1')}</p>
             </div>
          </div>
            
          <div className='choose-us-2'>
            <div className='chooseImage'></div>
             <div className='chooseText'>
               <p>{t('choose.text2')}</p>
             </div>
          </div>
          <div className='choose-us-3'>
            <div className='chooseImage'></div>
             <div className='chooseText'>
               <p>{t('choose.text3')}</p>
             </div>
          </div>
        </div>
  
          <div className='aboutUsBlock' id='aboutUsBlock'>
            <div className='about about-1'>
              <div className="icon">
                <img src="/images/car.png" alt="car icon" />
              </div>
              <p>{t('about.title1')}</p>
              <p>{t('about.text1')}</p>
            </div>
            <div className='about about-2'>
              <div className="icon">
                  <img src="/images/people.png" alt="people icon" />
              </div>
              <p>{t('about.title2')}</p>
              <p>{t('about.text2')}</p>
            </div>
            <div className='about about-3'>
             <div className="icon">
                <img src="/images/info.png" alt="info icon" />
              </div>
              <p>{t('about.title3')}</p>
              <p>{t('about.text3')}</p>
            </div>
        </div>
   </div>



</div>

    );
  };
  export default CarPage;




