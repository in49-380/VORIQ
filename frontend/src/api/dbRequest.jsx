const BASE_URL = "/fakeDB";

export const requestFromVehicleSelectors =async(url,{signal})=>{
    const method="GET"
    const URL=`${BASE_URL}/${url}`
    try {
        const options={
            method,
            headers:{
                'accept': 'application/json'
            },
            signal
        }
        const response=await fetch (URL, options)
        if (!response.ok){
            throw new Error('data is not avaible')
        }
        const data=await response.json()
        console.log('data in dbRequest', data)
        return {response:data, success:'success', save:false}
        
        } catch(error){
            if (error.name === "AbortError") {
                return { error: 'canceled by TimeError' }
            }
            // console.error(`Error in ${method}`,error)
            return {error: error.message || 'Unknown error'}
        }
    
}