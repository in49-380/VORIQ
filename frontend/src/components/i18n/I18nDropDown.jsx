import {useState} from "react";
import i18next from "i18next";
import { useTranslation } from "react-i18next";
import Select, {components as RSComponents} from 'react-select';


const I18nDropDown=()=>{
    const {i18n}=useTranslation()
    const Languages=[
        {
            value:'en',
            label:'English',
            icon: '🇬🇧'

        },
        {
            value:'de',
            label:'Deutsch',
            icon: '🇩🇪' 

        },
        {
            value:'uk',
            label:'Українська',
             icon: '🇺🇦'

        }
    ]
   const initialValue=Languages.find(e=>e.value===i18n.language)||Languages[0]
   
    const [value, setValue]=useState(initialValue)

    const onLanguageChange=(newValue)=>{
        setValue(newValue);
        i18next.changeLanguage(newValue.value)
        localStorage.setItem('Language', newValue.value)
    }
    
    const SingleValue = (props) => (
    <RSComponents.SingleValue {...props}>
        <span style={{ marginRight: 8 }}>{props.data.icon}</span>
        {props.data.label}
    </RSComponents.SingleValue>
    );

    const Option = (props) => (
    <RSComponents.Option {...props}>
        <span style={{ marginRight: 8 }}>{props.data.icon}</span>
        {props.data.label}
    </RSComponents.Option>
    );

    const customStyles={
        control: (provided, state)=>({
            ...provided,
            width:'16rem',
            backgroundColor: 'var(--background-primary)',
            
            '&:hover': { backgroundColor: 'var(--background-secondary)',
                         border:'none'   
             },
            border:'none',
            boxShadow: state.isFocused ? '0 0 0 0 transparent' : 'none', 

        }),

        menu: (provided) => ({
            ...provided,
           borderRadius: '0.5rem',

        }),

         singleValue: (provided) => ({
            ...provided,
            color: 'var(--color-secondary)',
            
        }),

        option: (provided, state) => ({
            ...provided,
            backgroundColor: state.isFocused
                ? 'var(--background-dark)'
                : 'var(--background-primary)',
            color: state.isFocused ? 'var(--color-light)' : 'null',
            cursor: 'pointer',
         }),

    }
   
    return (
        <>
             <Select
                  id='s7'
                  value={value}
                  options={Languages}
                  onChange={onLanguageChange}
                  styles={customStyles}
                  isSearchable={false}
                  components={{
                    DropdownIndicator: () => null,
                    IndicatorSeparator: () => null,
                    Option,
                    SingleValue
                    }}
                    
                />
        </>
    )
}
export default I18nDropDown