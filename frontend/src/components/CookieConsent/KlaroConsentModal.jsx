import { useEffect, useState} from 'react'
import Modal from '../Modal'
import Button from '../Button'
import KlaroInit from './KlaroInit'
import {useTranslation} from 'react-i18next'
import I18nDropDown from '../i18n/I18nDropDown'

export default function KlaroConsent() {
  const [isVisible, setIsVisible] = useState(false)
  const [manager, setManager] = useState(null)
  const {t,i18n}=useTranslation()
  // const acceptButtonRef=useRef(null)

  const currentLanguage=i18n.language;
  const fontSize=currentLanguage==='en'?
    'baseChar':'smallChar'

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
      description={t('cookieModal.description')}
    >
      <div className="cookie_modal_container">
        <Button onClick={handleDecline}>
          {t('cookieModal.cookieButton.decline')}
        </Button>
        <Button onClick={handleCustomize}>
        {t('cookieModal.cookieButton.customize')}
        </Button>
        <Button onClick={handleAccept}
                ref={button=>button&&button.focus()}
                className={`${fontSize}`} >
          {t('cookieModal.cookieButton.acceptAll')}
        </Button>
      </div>
      <div className='policy_container'>
        <Button><a href="http://api/meta/terms" target="_blank" rel="noopener noreferrer">Therms of use</a></Button>
        <Button><a href="http://api/meta/privacy" target="_blank" rel="noopener noreferrer">Privacy Policy</a></Button>
        <Button><a href="http://api/meta/cookies" target="_blank" rel="noopener noreferrer">Cookie Policy</a></Button>
      </div>
    </Modal>
    </> 
  )
}
