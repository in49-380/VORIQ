import { useState } from 'react';
import { ScreenContext } from '../hooks/useScreen';

export const ScreenProvider = ({ children }) => {
  const [currentScreen, setCurrentScreen] = useState('home');
  return (
    <ScreenContext.Provider value={{ currentScreen, setCurrentScreen }}>
      {children}
    </ScreenContext.Provider>
  );
};
