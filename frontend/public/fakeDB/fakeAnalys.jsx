const brandAnalyse=(data)=>{
    const result=data.map(item=>({
        value:item.id,
        label:item.name
      }))
    return result  
}

export const modelAnalyse=(data, parentValue)=>{
    const result= data
    .filter(item=>item.brandId===Number(parentValue.value))
    .map(item=>({
      value:item.id,
      label:item.name
    }))
    return result
}

export const yearAnalyse=(data, parentValue)=>{
    const result=data
        .filter(item=>item.modelId===Number(parentValue.value))
        .map (item=>({
        value:item.id,
        label:item.year
        }))
     return result   
}

export const engineAnalyse=(data, parentValue)=>{
    const result=data
    .filter(item=>
            parentValue
            .some(y=>item.yearId===Number(y.value))
            ) 
    .map(item=>({
      value:item.id,
      label:item.engine
    }))
    return result
}

export default brandAnalyse