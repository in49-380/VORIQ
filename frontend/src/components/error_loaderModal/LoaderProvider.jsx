import { useRunApi } from "../../hooks/useRunApi";
import { LoaderContext } from "../../hooks/useLoader";


export const LoaderProvider=({children})=>{
    const apiState=useRunApi()
    return(
        <LoaderContext.Provider value={apiState}>
            {children}
        </LoaderContext.Provider>
    )
}

