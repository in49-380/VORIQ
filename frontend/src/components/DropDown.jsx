import React from 'react';

const DropDown=({selectValue, options, onOptionChange, className, optionClassName})=>{
  const handleChange = (e) => {
  const newValue = e.target.value;
  onOptionChange(newValue);
};
    

    return (
        <select 
          value={selectValue} 
          onChange={handleChange} 
          className={className} 
          >
          {options.map(({ value: optionValue, label, icon }) => 
          (
            <option 
              key={optionValue} 
              value={optionValue} 
              optionClassName={optionClassName}
            >
              <span aria-hidden="true">{icon}</span>
              <span>{label}</span>
            </option>
          ))}
        </select>
      );
}

export default DropDown