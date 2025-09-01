import Button from "../Button"
import useTheme from "../../hooks/useTheme"
import HalfCircle from "./ThemeCircle"
import {useState } from "react"

const ThemeButton=()=>{
    const {currentTheme, setCurrentTheme, theme}=useTheme() 
    const [nextColor, setNextColor]=useState('black') 

    const onThemeClick=()=>{
        let currentIndex=theme.findIndex(th=>th.name===currentTheme.name)
        
        currentIndex = (currentIndex + 1) % theme.length;

        const newTheme=theme[currentIndex]
        setCurrentTheme(newTheme)

        const nextIndex=(currentIndex + 1) % theme.length;
        const nextColor=theme[nextIndex].color
        setNextColor(nextColor)
        
        const html = document.documentElement; 
        html.className=newTheme.name 
    }
  
    return(
       <Button id='b21'
            onClick={onThemeClick}
            className="theme_button"
       >
            <HalfCircle 
                firstColor={currentTheme.color}
                secondColor={nextColor}
            />
       </Button>
    )
}
export default ThemeButton

