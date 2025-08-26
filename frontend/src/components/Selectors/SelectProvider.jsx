
import { useState } from "react";
import {SelectContext} from '../../hooks/useSelect'

export const SelectProvider = ({ children }) => {
  const [analysButtonIsDisabled, setAnalysButtonIsDisabled]=useState(true)
  const [addButtonIsDisabled, setAddButtonIsDisabled]=useState(true)
  const [isNewSelectorSetVisible, setIsNewSelectorSetVisible]=useState(false)
  return (
    <SelectContext.Provider value={{
    analysButtonIsDisabled, setAnalysButtonIsDisabled,
    addButtonIsDisabled, setAddButtonIsDisabled,
    isNewSelectorSetVisible, setIsNewSelectorSetVisible
    }}>
      {children}
    </SelectContext.Provider>
  );
};