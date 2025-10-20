import React from "react";
import { useEffect, useState } from "react";

const IDs=['s1', 's2', 's3', 's4', 's5', 's6', 'b5', 'b6']

const HintBox=()=>{

const [hintBoxData, setHintBoxData]=useState([])




 const getElementData=()=>{
    const getDataByElementId=IDs.map((id)=>{
            const currentElement = document.getElementById(id);
            if (!currentElement) return null
            const data=currentElement.getBoundingClientRect()

            return{
              id: id,
              coord:{
                top: data.top,
                left:data.left
              },
              size:{
                width:data.width,
                height:data.height
              }
            }
            
          })
     return getDataByElementId     
  }
  
  const  makeHintBoxData=(elementData)=>{

    const size=elementData.size
    const coord=elementData.coord

    const xcenter=coord.left+size.width/2
    const ycenter=coord.top
    
    const id=elementData.id

    const width = Math.max(250, size.width);
    const height=size.height*1.5
    const top=ycenter-height-10
    const newLeft=xcenter-width/2
    const left = Math.min(
         Math.max(10, newLeft),           
         window.innerWidth - width - 10  
          );
    const tipOffset=xcenter-left      
  
    return{
          id: id,
          coord:{
            top: top,
            left:left
          },
          size:{
            width:width,
            height:height
          },
          tipOffset:tipOffset
      }
  }
       
  const updateData = () => {
    const data = getElementData()
    const hintData = data.filter(Boolean).map(d => makeHintBoxData(d))
    setHintBoxData(hintData)
  }

  useEffect(()=>{
     updateData()
       window.addEventListener("resize", updateData)
    return ()=>{
      window.removeEventListener("resize", updateData)
    }
  },[])
 
  return {hintBoxData}
   
}

export default HintBox
