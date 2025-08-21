import { createContext, useState} from "react";

const ErrorContext=createContext()

const ErrorProvider=({children})=>{
 const {isError, serIsError}=useState(false)   
    return (
        <ErrorContext.Provider value={{isError, serIsError}}>
            {children}
        </ErrorContext.Provider>
    )
}

export default ErrorProvider