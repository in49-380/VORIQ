import { getAnalyse } from "../api/post"
import Button from "./Button"
import {useTranslation} from 'react-i18next'
import { useLoader } from "../hooks/useLoader"


const ButtonBlock=({brand,model,year,engine,buttonIsDisabled})=>{

    const {t}=useTranslation()
    const {runApi, setSuccessResult}=useLoader()

    
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
        console.log('response in buttonblock', response)
    }


    // const handleOnClickNewSearch=()=>{
        
    // }
    const handleOnClickAnotherCar=()=>{

    }


    return(
        <div className='flex flex-row w-[90vw] items-center justify-around'>
         <Button 
          onClick={!buttonIsDisabled?  handleOnClickStartAnalysis:null}
          buttonIsDisabled={buttonIsDisabled}
          className={`px-4 py-2 rounded-lg font-medium text-white transition-colors duration-200
            ${buttonIsDisabled
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
          onClick={handleOnClickAnotherCar}
          className='bg-green-500 text-white h-auto'
          children={t('buttonBlock.addAnotherCar')}
          />
        </div>
    )
}
export default ButtonBlock