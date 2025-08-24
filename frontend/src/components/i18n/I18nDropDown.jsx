import DropDown from "../DropDown";
import { useState } from "react";
import i18next from "i18next";

const I18nDropDown=()=>{
    const [value, setValue]=useState(localStorage.getItem('Language')||'en')

    const onLanguageChange=(newValue)=>{
        setValue(newValue);
        i18next.changeLanguage(newValue)
        localStorage.setItem('Language', newValue)
    }
    const Languages=[
        {
            value:'en',
            label:'English'
        },
        {
            value:'de',
            label:'Deutsch'
        },
        {
            value:'uk',
            label:'Українська'
        }
    ]

    const selectClassName = "px-3 py-1.5 rounded-md bg-transparent text-sm text-gray-700 shadow-inner shadow-[inset_2px_2px_5px_rgba(0,0,0,0.3)]  cursor-pointer outline-none transition-shadow duration-200 hover:shadow-[inset_2px_2px_5px_rgba(0,0,0,0.15)] focus:shadow-[inset_2px_2px_5px_rgba(0,0,0,0.3)] focus:border-blue-500"

    return (
        <DropDown
            selectValue={value}
            options={Languages}
            onOptionChange={onLanguageChange}
            className={selectClassName}
        />
    )
}
export default I18nDropDown