import { getAnalyse } from "../../api/post"
import Button from "../Button"
import {useTranslation} from 'react-i18next'
import { useLoader } from "../../hooks/useLoader"
import useSelect from "../../hooks/useSelect"


const ButtonBlock=({brand,model,year,engine, setCurrentStep})=>{

    const {t}=useTranslation()
    const {runApi, setSuccessResult}=useLoader()
    const {analysButtonIsDisabled}=useSelect()

    const goToStep=(s)=>{setCurrentStep(s)}
    
    const handleOnClickStartAnalysis=async()=>{
        setSuccessResult(null)
        const payload = {
            brandId: brand.value,
            modelId: model.value,
            yearIds: year.value,    
            engineIds: engine.value
        };
            console.log('year.value', year.value, 'model.value', model.value)

        const jsonPayload = JSON.stringify(payload);
        const response=await runApi((opt)=>getAnalyse(jsonPayload, opt))
        console.log('response in buttonblock', response)
        goToStep(6)
    }

    const handleOnClickNewSearch=()=>{
        
    }

    const dis_en= analysButtonIsDisabled
         ? 'disabled_button'
         : 'enabled_button'

    return(
        <div className='analyze_button_container'>
         <Button 
          id='b5'
          onClick={!analysButtonIsDisabled?  handleOnClickStartAnalysis:null}
          className={`${dis_en} transition-colors duration-200`}
          children={t('buttonBlock.analyze')}
          />
         <Button
          id='b6' 
          onClick={handleOnClickNewSearch}
          className={`${dis_en} h-auto`}
          children={'New search'}
          />
        
        </div> 
    )
}
export default ButtonBlock