import React from "react";
import { useEffect, useState } from "react";

const IDs=['s1', 's2', 's3', 's4', 'b5', 'b6']

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
    const id=elementData.id
    const width=elementData.size.width*2
    const height=elementData.size.height*1.2
    const top=elementData.coord.top-height*3
    const left=elementData.coord.left-width/4
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
