import React from "react";
import {useState, useEffect} from 'react'
import useHintBox from '../../hooks/useHintBox'
import Button from "../Button";
import {useTranslation} from "react-i18next";

const HintBox=({currentStep, setCurrentStep})=>{
const {t}=useTranslation()
  const hintTour=[
    { step: 1, value: 's1', content: t('hint.1') },
    { step: 2, value: 's2', content: t('hint.2') },
    { step: 3, value: 's3', content: t('hint.3') },
    { step: 4, value: 's4', content: t('hint.4') },
    { step: 5, value: 's5', content: t('hint.5') },
    { step: 6, value: 's6', content: t('hint.6') },
    { step: 7, value: 'b5', content: t('hint.analyze') },
    { step: 8, value: 'b6', content: t('hint.newSearch') },
  ]

  const {hintBoxData}=useHintBox()
  const [isHintAvaible, setIsHintAvaible] = useState(() => {
    return currentStep < 9 && !localStorage.getItem('hintIsViewed');
  });

  useEffect(() => {
  if (currentStep > 8) {

    setIsHintAvaible(false);
  } else {
    if (!localStorage.getItem('hintIsViewed')) setIsHintAvaible(true);
  }
  }, [currentStep]);

    const currentHint=hintTour
    .find(item=>item.step===currentStep)

    if (!currentHint) return null;
    const currentBoxData=hintBoxData
    .filter(item=>item.id===currentHint.value)
    .map(hint=>({
      coord:{
        top: hint.coord.top,
        left:hint.coord.left
      },
      size:{
        width:hint.size.width,
        height:hint.size.height
      },
      tipOffset:hint.tipOffset
    }))[0]  
    if (!currentBoxData) return null;

    const hintStyle=
          { width: `${currentBoxData?.size?.width}px`, 
            height: `${currentBoxData?.size?.height}px`, 
            top: `${currentBoxData?.coord?.top}px`, 
            left:`${currentBoxData?.coord?.left}px` }

    const tipStyle={
      "--tip-offset":`${currentBoxData?.tipOffset}px`
    }        

    
            
    const onPrevClick=(()=>{
      setCurrentStep(currentStep-1)
    })

    const onNextClick=(()=>{
      setCurrentStep(currentStep+1)
    })

    const onCloseClick=(()=>{
      setIsHintAvaible(false)
      localStorage.setItem('hintIsViewed', 'true')
    })



    if (!isHintAvaible || localStorage.getItem('hintIsViewed')) return null
    else
    return (
      <> 
          <div
            key={currentHint?.stepValue}
            className="hint_container"
            style={hintStyle}
          > 
            {currentHint?.content}
            <div
              className="small_element"
              style={tipStyle}
            />
            <div className="hint_buttons_container"> 
              
                <Button id='b16'
                onClick={onCloseClick}
                children={'close'}/>

                {currentStep>1 && 
                <Button id='b14'
                onClick={onPrevClick}
                children={'prev'}/>}
                
                {currentStep<8 && 

                <Button id='b15'
                onClick={onNextClick}
                children={'next'}/>}

              </div>
          
          </div>
        </>
      );
    }

    export default HintBox

