import Button from "../Button"
import KlaroInit from "./KlaroInit"

const KlaroConsentButton=()=>{
    return(
        <>
            <KlaroInit/>    
            <Button onClick={()=> window.klaro.show()} 
                    children={'Cookies'}/>
        </>
    )
}
export default KlaroConsentButton