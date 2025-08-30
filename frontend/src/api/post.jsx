export const getAnalyse=async(payload,{signal})=>{
const url='https://httpbin.org/post'
try{
    const options={
        method: 'POST',
        mode: 'cors',
        credential:'include',
        headers:{
            'accept':'application/json',
            'content-type':'application/json'
        },
        body:payload,
        signal
    }
    const response=await fetch(url, options)
    if (!response.ok) {
        throw new Error('data is not avaible')
    }
    const data=await response.json()
        console.log('data in post', data)
      return {response:data.json, success:'success', save: true}
    }catch(error){
     if (error.name === "AbortError") {
            return { error: 'canceled by TimeError' }
        }
        return {error: error.message || 'Unknown error'}
}
}
