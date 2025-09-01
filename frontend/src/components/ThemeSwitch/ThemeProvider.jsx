import { useState } from "react";
import {ThemeContext}  from "../../hooks/useTheme";

export const ThemeProvider=({children})=>{
      const theme=[
        {name: 'light', color:'var(--color-theme-light)'},
        {name: 'dark', color:'var(--color-theme-dark)'},
        {name:'blue', color:'var(--color-theme-blue)'}
    ]
    const [currentTheme, setCurrentTheme]=useState(theme[0])

    return(
    <ThemeContext.Provider value={{currentTheme, setCurrentTheme, theme}}>
        {children}
    </ThemeContext.Provider>
    )
}