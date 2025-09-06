import Modal from "../Modal";
import Spinner from "../Spinner";
import Speed from "../Speed";
import { useLoader } from '../../hooks/useLoader';



const LoaderModal=()=>{
  const {isLoading, isTimeOutError}= useLoader()

    return (
        <Modal
        open={!isTimeOutError && isLoading} 
        fullscreen={true}
         >
                 {/* <Spinner 
                    id='cust1'
                    size='80px'
                    borderWidth='10px'
                    color='blue'
                    speed={1}
                   /> */}
                  <Speed/>
        </Modal>
    )
}

export default LoaderModal