import React, { useEffect } from "react";
import {useState} from 'react'
import useHintBox from '../../hooks/useHintBox'
import Button from "../Button";
import {changeLanguage } from "i18next";
import { SelectContext } from "../../hooks/useSelect";

const HintBox=({width, height, top, left, currentStep, setCurrentStep})=>{

  const hintTour=[
    { step: 1, value: 's1', content: 'hint1' },
    { step: 2, value: 's2', content: 'hint2' },
    { step: 3, value: 's3', content: 'hint3' },
    { step: 4, value: 's4', content: 'hint4' },
    { step: 5, value: 'b5', content: 'hint5' },
    { step: 6, value: 'b6', content: 'hint6' },
  ]

  const {hintBoxData}=useHintBox()
  // const [currentStep, setCurrentStep]=useState(1)
  const [isHintAvaible, setIsHintAvaible]=useState(currentStep<5)

    const currentHint=hintTour
    .find(item=>item.step===currentStep)

    const currentBoxData=hintBoxData
    .filter(item=>item.id===currentHint.value)
    .map(hint=>({
      coord:{
        top: top||hint.coord.top,
        left:left||hint.coord.left
      },
      size:{
        width:width||hint.size.width,
        height:height||hint.size.height
      }
    }))[0]  

    const hintStyle=
          { width: `${currentBoxData?.size?.width}px`, 
            height: `${currentBoxData?.size?.height}px`, 
            top: `${currentBoxData?.coord?.top}px`, 
            left:`${currentBoxData?.coord?.left}px` }


    
            
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
            className="absolute  bg-gray-400 rounded-lg flex flex-col items-center justify-center text-black font-bold m-12 mx-auto py-4"
            style={hintStyle}
          > 
            {currentHint?.content}
            <div
              className="absolute bottom-[-10px] left-1/2 -translate-x-1/2 w-0 h-0 border-l-[10px] border-l-transparent border-r-[10px] border-r-transparent border-t-[10px] border-t-gray-400"
            />
            <div className="flex flex-row"> 
              
                {currentStep>1 && 
                <Button
                onClick={onPrevClick}
                children={'prev'}/>}
                
                {currentStep<5 && 
                <Button
                onClick={onNextClick}
                children={'next'}/>}

                <Button
                onClick={onCloseClick}
                children={'close'}
                />
              </div>
          
          </div>
        </>
      );
    }

    export default HintBox

