import { createContext, useContext } from "react";

export const LoaderContext=createContext()

export const useLoader=()=>{
    return useContext(LoaderContext)
}