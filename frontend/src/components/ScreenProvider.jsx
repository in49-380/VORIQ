import { useState } from 'react';
import { ScreenContext } from '../hooks/useScreen';

export const ScreenProvider = ({ children }) => {
  const [currentScreen, setCurrentScreen] = useState('cars');
  return (
    <ScreenContext.Provider value={{ currentScreen, setCurrentScreen }}>
      {children}
    </ScreenContext.Provider>
  );
};
