import {useState } from "react";
import {ThemeContext}  from "../../hooks/useTheme";

export const ThemeProvider=({children})=>{
    const theme=[
      {name: 'light', color:'var(--color-theme-light)'},
      {name: 'dark', color:'var(--color-theme-dark)'},
      {name:'blue', color:'var(--color-theme-blue)'}
    ]
    const savedTheme = localStorage.getItem('colorTheme');
    const [currentTheme, setCurrentTheme] = useState(
    ()=>theme.find(t => t.name === savedTheme) || theme[0]
    );
    const root = document.documentElement;
    root.className = currentTheme.name;

    return(
    <ThemeContext.Provider value={{currentTheme, setCurrentTheme, theme}}>
        {children}
    </ThemeContext.Provider>
    )
}