import { useTranslation } from 'react-i18next'
import { useError } from '../../hooks/useError'
import Modal from '../Modal'
import Button from '../Button'


const ErrorModal=()=>{
    const {t}=useTranslation()
    const isError=useError()
    return(
        <Modal
            onOpen={isError}
            fullscreen='true'
            title={t('errorModal.messageTitle')}
            description={t('errorModal.messageBody')}
        >
            <Button children={t('errorModal.retry')}/>
            <Button children={t('errorModal.cancel')}/>
        </Modal>
        
        // open, onOpenChange,
    )
}

export default ErrorModal;