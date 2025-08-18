export const asyncFunc = async () => {
    
    await new Promise((resolve) => setTimeout(resolve, 39999));
  
    return {
      success: true,
      answer: 'successful',
      };
  };