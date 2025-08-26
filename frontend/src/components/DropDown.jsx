import React from 'react';

// options=[{value:},{label:}]
const DropDown=({selectValue, options, disabledOption, onOptionChange, disabledSelect=false, className, optionclassname})=>{
  const handleChange = (e) => {
  const newValue = e.target.value;
  onOptionChange(newValue);
};
    

    return (
        <select value={selectValue} onChange={handleChange} className={className} disabled={disabledSelect}>
          {disabledOption && <option value={disabledOption} disabled> {disabledOption} </option>}
          {options.map(({ value: optionValue, label }) => (
            <option key={optionValue} value={optionValue} optionclassname={optionclassname}>
              {label}
            </option>
          ))}
        </select>
      );
}

export default DropDown