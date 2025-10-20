import { useTranslation } from 'react-i18next'
import Modal from '../Modal'
import Button from '../Button'
import { useLoader } from '../../hooks/useLoader'


const ErrorModal=()=>{
    const {t}=useTranslation()
    const {isTimeOutError, cancel, retry, resultMessage}=useLoader()

    return(
        <Modal
            open={!!isTimeOutError}
            fullscreen={true}
            title={t('errorModal.messageTitle')}
            description={`${t('errorModal.messageBody')} ${resultMessage}`}
        >
            <p className='errorQuestion'>{t('errorModal.messageBodyString2')}</p>
            <div className='errorButtons'>
                <Button id='b10'
                        children={t('errorModal.retry')}
                        onClick={retry}
                        className='retryButton'
                />
                <Button id='b11'
                        children={t('errorModal.cancel')}
                        onClick={cancel}
                        className='abortButton'
                />
            </div>

        </Modal>
        
    )
}

export default ErrorModal;