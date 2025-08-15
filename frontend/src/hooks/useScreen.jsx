import { createContext, useContext } from 'react';

export const ScreenContext = createContext();

export const useScreen = () => {
  return useContext(ScreenContext);
};