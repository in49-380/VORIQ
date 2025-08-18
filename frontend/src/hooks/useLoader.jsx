import { useContext } from "react"
import LoaderContext from "../components/loader/LoaderContext"

export const useLoader=()=>useContext(LoaderContext)
