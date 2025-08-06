export const sendTokenToBackend = async (token) => {
    console.log("Send Token to back:", token);
    
    await new Promise((resolve) => setTimeout(resolve, 1000));
  
    return {
      success: true,
      token: 'backend-jwt',
    //   user: {
    //     id: 1,
    //     name: 'Mock User',
    //   },
    };
  };