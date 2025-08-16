import Modal from "../Modal";
import Spinner from "../Spinner";

const LoaderModal=({isLoading})=>{

    return (
        <Modal
            open={isLoading}
            fullscreen={true}
        >
            <Spinner 
            size='80px'
            borderWidth='10px'
            color='green'
            speed={1}
            />
        </Modal>
    )
}

export default LoaderModal