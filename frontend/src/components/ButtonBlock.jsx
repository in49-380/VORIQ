import { getAnalyse } from "../api/post"
import Button from "./Button"
import {useTranslation} from 'react-i18next'
import { useLoader } from "../hooks/useLoader"


const ButtonBlock=({brand,model,year,engine, onStartAnalysis, onAnotherCar})=>{

    const {t}=useTranslation()
    const {runApi}=useLoader()
    
    const handleOnClickStartAnalysis=async()=>{
        const payload = {
        brandId: brand.value,
        modelId: model.value,
        yearIds: year.value.map(y => y.value),    
        engineIds: engine.value
        };
        const jsonPayload = JSON.stringify(payload);
        const response=await runApi(()=>getAnalyse(jsonPayload))
        onStartAnalysis(response)
    }


    const handleOnClickNewSearch=()=>{
        
    }
    const handleOnClickAnotherCar=()=>{

    }


    return(
        <div className='flex flex-row w-[90vw] items-center justify-around'>
         <Button 
          onClick={handleOnClickStartAnalysis}
          className='bg-green-500 text-white h-auto'
          children={'Start Analysis'}
          />
         {/* <Button 
          onClick={handleOnClickNewSearch}
          className='bg-green-500 text-white h-auto'
          children={'New search'}
          /> */}
         <Button 
          onClick={handleOnClickAnotherCar}
          className='bg-green-500 text-white h-auto'
          children={'Select another car'}
          />
        </div>
    )
}
export default ButtonBlock