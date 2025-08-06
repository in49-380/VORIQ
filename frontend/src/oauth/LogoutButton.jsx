import useGoogleAuth from "./useGoogleAuth";
import AuthContext from "./AuthContext";
import { useContext } from "react";

const LogoutButton=()=>{
    const {token}=useContext(AuthContext);
    const {logout}=useGoogleAuth()
    const handleOnClick=()=>{logout()}
    
    return token?(
        <button onClick={handleOnClick}
         className="fixed top-4 right-4 px-4 py-2 bg-red-600 text-white rounded 
         hover:bg-red-700 transition" >
         Logout</button>
    ):(null)
}
export default LogoutButton