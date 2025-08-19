import React, { createContext, useState, useRef} from 'react';

const LoaderContext=createContext()

export const LoaderProvider=({children})=>{
const [isLoading, setIsLoading]=useState(false)
const [isTooLongLoading,setIsTooLongLoading]=useState(false)

const delayStartTimer = useRef(null);
const longLoadingTimer = useRef(null);
const controllerRef=useRef(null)


const waitLonger=()=>{
    setIsLoading(true)
    setIsTooLongLoading(false)
    clearTimeout(longLoadingTimer.current);
    longLoadingTimer.current = setTimeout(() =>
         setIsTooLongLoading(true), 15000);
  }

const abort=()=>{
  controllerRef.current?.abort();
  setIsLoading(false);
  setIsTooLongLoading(false);
}

const runWithLoader=async(asyncFunction)=>{
    clearTimeout(delayStartTimer.current);
    delayStartTimer.current=setTimeout(()=>
        setIsLoading(true),200)

    clearTimeout(longLoadingTimer.current);
    longLoadingTimer.current=setTimeout(()=>
        setIsTooLongLoading(true)
        ,15000)

    controllerRef.current=new AbortController()
    const signal=controllerRef.current.signal    

     signal.addEventListener("abort", () => {
     console.log("Signal aborted → clearing longLoadingTimer");
     clearTimeout(longLoadingTimer.current);
    });
    

    try {
        return await asyncFunction({signal}) 
    } finally {
      clearTimeout(delayStartTimer.current);
      clearTimeout(longLoadingTimer.current);

      setTimeout(()=>setIsTooLongLoading(false),1000);
      setTimeout(() => setIsLoading(false), 500);
      controllerRef.current = null;
    }
}



    return(
        <LoaderContext.Provider
         value={{isLoading, isTooLongLoading, runWithLoader, waitLonger, abort}}>
            {children}
        </LoaderContext.Provider>
    )
}
export default LoaderContext;
