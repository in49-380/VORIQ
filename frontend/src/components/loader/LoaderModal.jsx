import Button from "../Button";
import Modal from "../Modal";
import Spinner from "../Spinner";
import { useLoader } from "../../hooks/useLoader";

const LoaderModal=({isLoading, isTooLongLoading})=>{
const {waitLonger, abort}=useLoader()

    return (
        <Modal
            open={isLoading||isTooLongLoading}
            fullscreen={true}
        >
            {isTooLongLoading ? (
                <div >
                    <p className="text-green-600 text-2xl">The operation is taking too long</p>
                    <Button 
                    children={'Wait longer'}
                    className="bg-gray-300 text-green-600 font-bold"
                    onClick={waitLonger}    
                    />
                    <Button 
                    children={'Abort'}
                    className="bg-gray-300 text-green-600 font-bold"
                    onClick={abort}    
                    />
                </div>
            ) : (
                <Spinner 
                    size='80px'
                    borderWidth='10px'
                    color='blue'
                    speed={1}
                />
            )}
        </Modal>
    )
}

export default LoaderModal