export const asyncFunc = async ({signal}) => {
    
   try {
        await new Promise((resolve, reject) => {
        const timeout=setTimeout(()=>{
        resolve()
        }, 25000)
       
      signal?.addEventListener('abort', () => {
        clearTimeout(timeout);
        reject(new Error('canceled by User'));
      });
    })
    
    return {answer: 'successful'}

  } catch (err) {
    return { success: false, answer: err.message };
  }
}

export const abortTest= async({signal})=>{
     signal.addEventListener('abort', () => {
    console.log('Abort event fired');
  });

  return fetch('https://httpbin.org/delay/10', { signal })
    .then((res) => {
        console.log (res)
      return { answer: 'successfuly' };
    })
    .catch(() => {
      return { err: 'canceled by User' };
    });
};
