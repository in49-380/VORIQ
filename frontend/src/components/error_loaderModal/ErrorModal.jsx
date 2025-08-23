import { useTranslation } from 'react-i18next'
import Modal from '../Modal'
import Button from '../Button'
import { useLoader } from '../../hooks/useLoader'


const ErrorModal=()=>{
    const {t}=useTranslation()
    const {isTimeOutError, cancel, retry, errorMessage}=useLoader()

    return(
        <Modal
            open={!!isTimeOutError}
            fullscreen={true}
            title={t('errorModal.messageTitle')}
            description={`${t('errorModal.messageBody')} ${errorMessage}`}
            className={'flex-row gap-4 text-red-700'}
        >
            <p>{t('errorModal.messageBodyString2')}</p>
            <div className='flex flex-row gap-4'>
                <Button children={t('errorModal.retry')}
                        onClick={retry}
                        className={'text-red-700'}
                />
                <Button children={t('errorModal.cancel')}
                        onClick={cancel}
                />
            </div>

        </Modal>
        
    )
}

export default ErrorModal;