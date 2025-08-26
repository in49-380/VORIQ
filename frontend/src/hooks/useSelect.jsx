import { useContext, createContext } from "react";

export const SelectContext = createContext();

const useSelect = () => {
  return useContext(SelectContext);
};

export default useSelect;
