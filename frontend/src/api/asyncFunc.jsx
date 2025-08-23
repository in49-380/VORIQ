// export const asyncFunc = async ({signal, clearAllTimeOut}) => {
    
// return new Promise((resolve, reject) => {
//     const timeout = setTimeout(() => resolve({ answer: 'successful' }),25000);

//     signal.addEventListener('abort', () => {
//       clearTimeout(timeout);
//       clearAllTimeOut?.();
//       reject(new Error('canceled by user'));
//     });
//   })
//   .catch(err => ({ success: false, answer: 'canceled by user', error: err }));
// };

export const asyncFunc = async ({signal}) => {
    
   try {
        await new Promise((resolve, reject) => {
          
          const timeout=setTimeout(()=>{
          resolve()
        }, 5000)
       
      signal?.addEventListener('abort', () => {
        clearTimeout(timeout);
        reject(new Error('canceled by TimeOutError'));
      });
    })
    return {answer: 'successful'}

  } catch (err) {
    return { success: false, answer: err.message };
  } 

}


export const abortTest= async({signal})=>{
    
  return fetch('https://httpbin.org/delay/5',{signal})
    .then(() => {
      return { res: 'successful' };
    })
    .catch(() => {
      console.log(signal)
      return { err: 'canceled by TimeError' };
    });

};

export const asyncHttpErrorTest = async ({ signal }) => {
  // Симулируем fetch с ошибкой
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      reject({ error: { message: 'Not Found', code: 404 } }); // 404 ошибка
    }, 1000);

    signal.addEventListener('abort', () => reject({ error: { message: 'Aborted', code: 'ABORT' } }));
  });
};

export const asyncRandomError = () => {
  return new Promise((resolve) => {
    const fail = Math.random() < 0.5;
    const codeError=Math.floor(Math.random() * 200) + 400;

    setTimeout(() => {
      if (fail) {
        resolve({ success: false, error: { message: 'Random error occurred', code: codeError } });
      } else {
        resolve({ success:"saccessful" });
      }
    }, 1500);
  });
};
