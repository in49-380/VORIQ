import React, {useState, useRef, useEffect} from 'react';

export const useRunApi=()=>{


const [isLoading, setIsLoading]=useState(false)
const [isTimeOutError, setIsTimeOutError]=useState(false)

const [resultMessage, setResultMessage]=useState(null)
const [successResult, setSuccessResult]=useState(null)

useEffect(()=>{console.log('result message:',resultMessage)},[resultMessage])
useEffect(()=>{console.log('success result:',successResult)},[successResult])


const delayTime=1000

const delayStartTimer = useRef(null);
const errorByTimeOutTimer=useRef(null)
const controllerRef=useRef(null)
const lastCallFunction=useRef(null)
const requestFinished=useRef(null)


const clearAllTimeOut = () => {
  clearTimeout(delayStartTimer.current);
  clearTimeout(errorByTimeOutTimer.current);
};

useEffect(()=>{console.log('finish', requestFinished.current)},[requestFinished.current])

const startAllTimeOut = () => {
  delayStartTimer.current = setTimeout(() => {
    setIsLoading(true);
  }, delayTime);

  errorByTimeOutTimer.current = setTimeout(() => {
    if (requestFinished.current) return;
    clearAllTimeOut()
    setIsTimeOutError(true);
    controllerRef.current?.abort()
  },5000);
};

const initialAbortController=()=>{
    controllerRef.current=new AbortController()
    const signal=controllerRef.current.signal
    return signal
}

const retry = () => {
  setTimeout(()=>setIsTimeOutError(false),delayTime+100)
   if (lastCallFunction.current) 
    {runApi(lastCallFunction.current)}
} 

const cancel= ()=>{
  clearAllTimeOut()
  setIsLoading(false)
  setIsTimeOutError(false)
  controllerRef.current?.abort()

}

const runApi=async(asyncFunction)=>{
  clearAllTimeOut();
  requestFinished.current = false;
  startAllTimeOut();

  
  lastCallFunction.current=asyncFunction
  const signal = initialAbortController();
  
  try {
    const result = await asyncFunction(signal);
    requestFinished.current = true;
    clearAllTimeOut()
    setTimeout(()=>setIsLoading(false),1000)
    console.log('result in Api before if', result.response)
    if (result.success) {
      console.log('result.succes',result.success)
      setResultMessage (result.success)
      result.save
      ? setSuccessResult (result.response)
      :null
    } else if (signal.aborted) {
       setResultMessage('Request canceled or timed out');
    } else if (result.error) {
      setResultMessage(result.error.code || `Error: ${result.error.status || 'unknown'}`);
      setIsTimeOutError(true)
    } else {
      setResultMessage(null);
    }
    return result.response;
   } finally {controllerRef.current=null}
  }
    return({runApi, retry, cancel, isLoading, isTimeOutError, 
      resultMessage, successResult, setSuccessResult})
}
