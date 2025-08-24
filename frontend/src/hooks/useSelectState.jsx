import { useState } from "react";
import Select from "react-select/base";

const useSelectState=(isDisabled)=>{

  const [options, setOptions]=useState([])
  const [value, setValue]=useState(null)
  const [disabled, setDisabled]=useState(isDisabled)

  const clear=()=>{
    setOptions([])
    setValue(null)
    setDisabled(true)    
  }

  const softClear=()=>{
    setValue(null)
  }
  
  return(
    {options, setOptions,
    value,setValue,
    disabled,setDisabled,
    clear, softClear}
  )
}
export default useSelectState