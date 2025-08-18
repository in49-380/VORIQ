import React, { createContext, useState} from 'react';

const LoaderContext=createContext()

export const LoaderProvider=({children})=>{
const [isLoading, setIsLoading]=useState(false)
const [isTooLongLoading,setIsTooLongLoading]=useState(false)
let longLoadingTimer

const waitLonger=()=>{
    setIsLoading(true)
    setIsTooLongLoading(false)
    longLoadingTimer=setTimeout(()=>
        setIsTooLongLoading(true)
        ,20000)
}

const runWithLoader=async(asyncFunction)=>{
    let delayStartTimer=setTimeout(()=>setIsLoading(true),200)
    longLoadingTimer=setTimeout(()=>
        setIsTooLongLoading(true)
        ,20000)

    try 
       { return await asyncFunction() }
       
    finally {
        clearTimeout(delayStartTimer)
        // setIsLoading(false)
        setTimeout(()=>setIsTooLongLoading(false),1000) 
        setTimeout(()=>setIsLoading(false),500) 
        clearTimeout (longLoadingTimer)
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
