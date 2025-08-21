import { useContext } from "react";
import ErrorContext from '../components/errorModal/ErrorContext'

export const useError=()=>useContext(ErrorContext)
