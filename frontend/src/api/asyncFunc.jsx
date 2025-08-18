export const asyncFunc = async () => {
    
    await new Promise((resolve) => setTimeout(resolve, 20000));
  
    return {
      success: true,
      answer: 'successful',
      };
  };