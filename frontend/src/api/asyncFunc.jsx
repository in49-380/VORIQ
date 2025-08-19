export const asyncFunc = async ({signal}) => {
    
   try {
        await new Promise((resolve, reject) => {
        const timeout=setTimeout(()=>{
        resolve()
        }, 500)
       
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

