import React, {useState, useRef, useEffect} from 'react';

export const useRunApi=()=>{


const [isLoading, setIsLoading]=useState(false)
const [isTimeOutError, setIsTimeOutError]=useState(false)

const [errorMessage, setErrorMessage]=useState(null)
useEffect(()=>{console.log('error message:',errorMessage)},[errorMessage])

const delayTime=200

const delayStartTimer = useRef(null);
const errorByTimeOutTimer=useRef(null)
const controllerRef=useRef(null)
const lastCallFunction=useRef(null)


const clearAllTimeOut = () => {
  clearTimeout(delayStartTimer.current);
  clearTimeout(errorByTimeOutTimer.current);
};

const startAllTimeOut = () => {
  delayStartTimer.current = setTimeout(() => {
    setIsLoading(true);
  }, delayTime);


  errorByTimeOutTimer.current = setTimeout(() => {
    clearAllTimeOut()
    setIsTimeOutError(true);
    controllerRef.current?.abort()
  }, 3000);
};

const initialAbortController=()=>{
    controllerRef.current=new AbortController()
    const signal=controllerRef.current.signal
    return signal
}

const retry = () => {
  setTimeout(()=>setIsTimeOutError(false),delayTime+100)
   if (lastCallFunction.current) runApi(lastCallFunction.current)
} 

const cancel= ()=>{
  clearAllTimeOut()
  setIsLoading(false)
  setIsTimeOutError(false)
}

const runApi=async(asyncFunction)=>{
    clearAllTimeOut();
    startAllTimeOut();
    
    lastCallFunction.current=asyncFunction
    const signal = initialAbortController();

    try {
    const result = await asyncFunction({ signal});
    clearAllTimeOut()
    setTimeout(()=>setIsLoading(false),500)
    console.log('result', result)
    if (signal.aborted) {
    setErrorMessage('Request canceled or timed out');
  } else if (result.error) {
    setErrorMessage(result.error.code || `Error: ${result.error.status || 'unknown'}`);
    setIsTimeOutError(true)
  } else {
    setErrorMessage(null);
  }
  
    return result;
   } finally {controllerRef.current=null}
  }
    return({runApi, retry, cancel, isLoading, isTimeOutError, errorMessage})
}

