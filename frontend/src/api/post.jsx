export const getAnalyse=async(payload)=>{
const url='https://httpbin.org/post'
try{
    const options={
        method: 'POST',
        headers:{
            'accept':'application/json',
            'content-type':'application/json'
        },
        body:payload
    }
    const response=await fetch(url, options)
    if (!response.ok) {
        const errorMessage=await response.text()
        console.log('Error Message:', errorMessage)
        throw new Error ('unexpexted error')
    }
    const data=await response.json()
    console.log('data',data.json)
    return data.json
}catch(error){
    console.log(error)
    return null
}
}
