import React, { createContext, useState} from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(()=>localStorage.getItem('jwt_token'));

  return (
    <AuthContext.Provider value={{ token, setToken}}>
      {children}
    </AuthContext.Provider>
  );
}

export default AuthContext;