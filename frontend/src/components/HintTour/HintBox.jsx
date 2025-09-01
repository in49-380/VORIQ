import React from "react";
import {useState} from 'react'
import useHintBox from '../../hooks/useHintBox'
import Button from "../Button";
// import {changeLanguage } from "i18next";

const HintBox=({currentStep, setCurrentStep})=>{

  const hintTour=[
    { step: 1, value: 's1', content: 'hint1' },
    { step: 2, value: 's2', content: 'hint2' },
    { step: 3, value: 's3', content: 'hint3' },
    { step: 4, value: 's4', content: 'hint4' },
    { step: 5, value: 'b5', content: 'hint5' },
    { step: 6, value: 'b6', content: 'hint6' },
  ]

  const {hintBoxData}=useHintBox()
  const [isHintAvaible, setIsHintAvaible]=useState(currentStep<5)

    const currentHint=hintTour
    .find(item=>item.step===currentStep)

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

    const hintStyle=
          { width: `${currentBoxData?.size?.width}px`, 
            height: `${currentBoxData?.size?.height}px`, 
            top: `${currentBoxData?.coord?.top}px`, 
            left:`${currentBoxData?.coord?.left}px` }

    const tipStyle={
      "--tip-Offset":`${currentBoxData?.tipOffset}px`
    }        

    
            
    const onPrevClick=(()=>{
      setCurrentStep(currentStep-1)
    })

    const onNextClick=(()=>{
      setCurrentStep(currentStep+1)
    })

    const onCloseClick=(()=>{
      setIsHintAvaible(false)
      localStorage.setItem('hintIsViewed', true)
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
                
                {currentStep<6 && 
                <Button id='b15'
                onClick={onNextClick}
                children={'next'}/>}

              </div>
          
          </div>
        </>
      );
    }

    export default HintBox

