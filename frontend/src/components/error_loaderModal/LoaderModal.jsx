import Modal from "../Modal";
import Spinner from "../Spinner";
import { useLoader } from '../../hooks/useLoader';



const LoaderModal=()=>{
  const {isLoading, isTimeOutError}= useLoader()

    return (
        <Modal open={!isTimeOutError && isLoading} fullscreen={true} >
                 <Spinner 
                    size='80px'
                    borderWidth='10px'
                    color='blue'
                    speed={1}
                   />
        </Modal>
    )
}

export default LoaderModal