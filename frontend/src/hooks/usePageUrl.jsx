import { useEffect } from "react"

const usePageUrl=(url)=>{
    useEffect(()=>{
        window.history.pushState({},'',url)
    },[url])
}
export default usePageUrl