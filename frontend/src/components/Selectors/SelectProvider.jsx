
import { useState } from "react";
import {SelectContext} from '../../hooks/useSelect'

export const SelectProvider = ({ children }) => {
  const [analysButtonIsDisabled, setAnalysButtonIsDisabled]=useState(true)
  const [isNewSelectorSetVisible, setIsNewSelectorSetVisible]=useState(false)
  const [isNewSearch, setIsNewSearch]=useState(false)
  return (
    <SelectContext.Provider value={{
      analysButtonIsDisabled, setAnalysButtonIsDisabled,
      isNewSelectorSetVisible, setIsNewSelectorSetVisible,
      isNewSearch, setIsNewSearch
    }}>
      {children}
    </SelectContext.Provider>
  );
};