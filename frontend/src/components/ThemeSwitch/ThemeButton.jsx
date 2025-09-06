import Button from "../Button"
import useTheme from "../../hooks/useTheme"
import HalfCircle from "./ThemeCircle"
import {useState } from "react"

const ThemeButton=()=>{
    const {currentTheme, setCurrentTheme, theme}=useTheme() 
    
    const initialNextColor = (() => {
    const currentIndex = theme.findIndex(th => th.name === currentTheme.name)
    const nextIndex = (currentIndex + 1) % theme.length
    return theme[nextIndex].color
    })()    

    const [nextColor, setNextColor] = useState(initialNextColor)
    
    const onThemeClick=()=>{
        let currentIndex=theme.findIndex(th=>th.name===currentTheme.name)
        
        currentIndex = (currentIndex + 1) % theme.length;

        const newTheme=theme[currentIndex]
        setCurrentTheme(newTheme)
        localStorage.setItem('colorTheme', newTheme.name)
        const html = document.documentElement; 
        html.className=newTheme.name 

        const nextIndex=(currentIndex + 1) % theme.length;
        const nextColor=theme[nextIndex].color
        setNextColor(nextColor)
        

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

