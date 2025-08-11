import { useEffect, useState} from 'react'
import Modal from '../Modal'
import Button from '../Button'
import KlaroInit from './KlaroInit'

export default function KlaroConsent() {
  const [isVisible, setIsVisible] = useState(false)
  const [manager, setManager] = useState(null)
  // const acceptButtonRef=useRef(null)

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
      title="Cookies & Privacy Settings"
      description="We use cookies to enhance your experience. You can accept, reject, or customize them."
    >
      <div className="flex justify-end space-x-4 mt-6">
        <Button onClick={handleDecline} className="bg-red-500 text-white hover:bg-red-600">
          Decline all
        </Button>
        <Button onClick={handleCustomize} className="bg-gray-300 hover:bg-gray-400">
          Customize
        </Button>
        <Button onClick={handleAccept}
                ref={button=>button&&button.focus()}
                className="bg-blue-600 hover:bg-blue-700">
          Accept all
        </Button>
      </div>
    </Modal>
    </>
  )
}
