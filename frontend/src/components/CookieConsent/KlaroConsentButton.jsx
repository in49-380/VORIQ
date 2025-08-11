import Button from "../Button"
import KlaroInit from "./KlaroInit"
import { useTranslation } from "react-i18next"

const KlaroConsentButton=()=>{
    const {t}=useTranslation()
    return(
        <>
            <KlaroInit/>    
            <Button onClick={()=> window.klaro.show()} 
                    children={t("cookieSettings")}/>
        </>
    )
}
export default KlaroConsentButton