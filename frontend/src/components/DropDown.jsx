import React, { useState } from 'react';

const DropDown=({options, value, onOptionChange, className})=>{
    const [loading, setLoading]=useState(false)
    const handleChange=async(e)=>{
        const newValue=e.target.value
        setLoading(true)
        try {
            onOptionChange(newValue)
            console.log(`loading=${loading}`)
        } finally{

            setLoading(false)
            
        }
    }
    

    return (
        <select value={value} onChange={handleChange} className={className}>
          {options.map(({ value: optionValue, label }) => (
            <option key={optionValue} value={optionValue}>
              {label}
            </option>
          ))}
        </select>
      );
}

export default DropDown