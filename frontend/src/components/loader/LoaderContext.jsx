import React, { createContext, useState, useRef} from 'react';

const LoaderContext=createContext()

export const LoaderProvider=({children})=>{
const [isLoading, setIsLoading]=useState(false)
const [isTooLongLoading,setIsTooLongLoading]=useState(false)

 const longLoadingTimer = useRef(null);
const delayStartTimer = useRef(null);

const waitLonger=()=>{
    setIsLoading(true)
    setIsTooLongLoading(false)
    clearTimeout(longLoadingTimer.current);
    longLoadingTimer.current = setTimeout(() => setIsTooLongLoading(true), 20000);
  }

const runWithLoader=async(asyncFunction)=>{
    clearTimeout(delayStartTimer.current);
    delayStartTimer.current=setTimeout(()=>setIsLoading(true),200)

    if (longLoadingTimer.current) clearTimeout(longLoadingTimer.current);
    longLoadingTimer.current=setTimeout(()=>
        setIsTooLongLoading(true)
        ,20000)

    try 
       { return await asyncFunction() }
       
    finally {
      clearTimeout(delayStartTimer.current);
      clearTimeout(longLoadingTimer.current);

      setIsTooLongLoading(false);
      setTimeout(() => setIsLoading(false), 500);
    }
}
    return(
        <LoaderContext.Provider
         value={{isLoading, isTooLongLoading, runWithLoader, waitLonger}}>
            {children}
        </LoaderContext.Provider>
    )
}
export default LoaderContext;
