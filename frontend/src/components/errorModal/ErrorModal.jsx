import { useTranslation } from 'react-i18next'
import Modal from '../Modal'
import Button from '../Button'

const ErrorModal=()=>{
    const {t}=useTranslation()
    return(
        <Modal
            fullscreen='true'
            title={t('message.title')}
            description={t('message.body')}
        >
            <Button/>
            <Button/>
        </Modal>
        
        // open, onOpenChange,
    )
}

export default ErrorModal;