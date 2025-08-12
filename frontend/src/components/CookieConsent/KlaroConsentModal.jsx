import { useEffect, useState} from 'react'
import Modal from '../Modal'
import Button from '../Button'
import KlaroInit from './KlaroInit'
import {useTranslation} from 'react-i18next'

export default function KlaroConsent() {
  const [isVisible, setIsVisible] = useState(false)
  const [manager, setManager] = useState(null)
  const {t,i18n}=useTranslation()
  // const acceptButtonRef=useRef(null)

  const currentLanguage=i18n.language;
  const fontSize=currentLanguage==='en'?
    'text-base':'text-sm whitespace-nowrap'

  useEffect(() => {
    const klaroManager = window.klaro?.getManager?.()
    if (klaroManager) {
      setManager(klaroManager)
      if (!klaroManager.confirmed) {
        console.log('Consent not confirmed')
        setIsVisible(true)
      }
    }
  }, [])


  if (!manager) return null

  const handleAccept = () => {
    manager.changeAll(true)
    manager.saveAndApplyConsents()
    setIsVisible(false)
  }

  const handleDecline = () => {
    manager.changeAll(false)
    manager.saveAndApplyConsents()
    setIsVisible(false)
  }


  const handleCustomize = () => {
    setIsVisible(false)
    window.klaro.show()
  }
  

  return (
    <>
    <KlaroInit/>
    <Modal
      open={isVisible}
      onOpenChange={setIsVisible}
      title={t('cookieModal.title')}
      description={t('cookieModal.desctiption')}
    >
      <div className="flex justify-end space-x-4 mt-6">
        <Button onClick={handleDecline} className="bg-red-500 text-white hover:bg-red-600">
          {t('cookieModal.cookieButton.decline')}
        </Button>
        <Button onClick={handleCustomize} className="bg-gray-300 hover:bg-gray-400">
        {t('cookieModal.cookieButton.customize')}
        </Button>
        <Button onClick={handleAccept}
                ref={button=>button&&button.focus()}
                className={`bg-blue-600 hover:bg-blue-700 ${fontSize}`} >
          {t('cookieModal.cookieButton.acceptAll')}
        </Button>
      </div>
    </Modal>
    </> 
  )
}
