import { getAnalyse } from "../../api/post"
import Button from "../Button"
import {useTranslation} from 'react-i18next'
import { useLoader } from "../../hooks/useLoader"
import useSelect from "../../hooks/useSelect"


const ButtonBlock=({brand,model,year,engine})=>{

    const {t}=useTranslation()
    const {runApi, setSuccessResult}=useLoader()
    const {analysButtonIsDisabled, setIsNewSelectorSetVisible}=useSelect()
    const {addButtonIsDisabled, setAddButtonIsDisabled}=useSelect()
    
    const handleOnClickStartAnalysis=async()=>{
        setSuccessResult(null)
        const payload = {
        brandId: brand.value,
        modelId: model.value,
        yearIds: year.value.map(y => y.value),    
        engineIds: engine.value
        };
        const jsonPayload = JSON.stringify(payload);
        const response=await runApi((opt)=>getAnalyse(jsonPayload, opt))
        setAddButtonIsDisabled(false)
        console.log('response in buttonblock', response)
    }


    // const handleOnClickNewSearch=()=>{
        
    // }
    const handleOnClickAnotherCar=()=>{
        setIsNewSelectorSetVisible(true)
    }


    return(
        <div className='flex flex-row w-[90vw] items-center justify-around'>
         <Button 
          id='b5'
          onClick={!analysButtonIsDisabled?  handleOnClickStartAnalysis:null}
          className={`px-4 py-2 rounded-lg font-medium text-white transition-colors duration-200
            ${analysButtonIsDisabled
         ? 'bg-green-200 cursor-not-allowed'
         : 'bg-green-500 hover:bg-green-600 active:bg-green-700 cursor-pointer'}`}
          children={t('buttonBlock.analyze')}

          />
         {/* <Button 
          onClick={handleOnClickNewSearch}
          className='bg-green-500 text-white h-auto'
          children={'New search'}
          /> */}
         <Button 
          id='b6'
          onClick={handleOnClickAnotherCar}
         className={`px-4 py-2 rounded-lg font-medium text-white transition-colors duration-200
            ${addButtonIsDisabled
         ? 'bg-green-200 cursor-not-allowed'
         : 'bg-green-500 hover:bg-green-600 active:bg-green-700 cursor-pointer'}`}
          children={t('buttonBlock.addAnotherCar')}
          />
        </div>
    )
}
export default ButtonBlock