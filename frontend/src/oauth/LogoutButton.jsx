import useGoogleAuth from "../hooks/useGoogleAuth";
import AuthContext from "./AuthContext";
import { useContext } from "react";
import Button from '../components/Button';

const LogoutButton=()=>{
    const {token}=useContext(AuthContext);
    const {logout}=useGoogleAuth()
    const handleOnClick=()=>{logout()}
    
    return token?(
        <Button onClick={handleOnClick} 
                children={'Logout'} 
                style={'fixed top-4 right-4'}/>
    ):(null)
}
export default LogoutButton