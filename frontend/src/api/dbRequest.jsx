const BASE_URL = "/fakeDB";

export const requestFromVehicleSelectors =async(url)=>{
    const method="GET"
    const URL=`${BASE_URL}/${url}`
    try {
        const options={
            method,
            headers:{
                'accept': 'application/json'
            }
        }
        const response=await fetch (URL, options)
        if (!response.ok){
            throw new Error('data is not avaible')
        }
        return await response.json()
        
    } catch(error){
        console.error(`Error in ${method}`,error)
        return null
    }
    
}