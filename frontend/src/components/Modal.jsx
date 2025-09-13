import * as Dialog from '@radix-ui/react-dialog';

const Modal = ({ open, onOpenChange, children, title, description, fullscreen = false, className }) => {
  
  return (
    <Dialog.Root modal open={open} onOpenChange={onOpenChange}>
      <Dialog.Portal>
        <Dialog.Overlay  
          className="dialog-overlay" 
        />
        <Dialog.Content  
          className={`dialog-content ${
            fullscreen
              ? `fullscreen bg-opacity-90 backdrop-blur-sm`
              : `small `
          }${className}`}

          onInteractOutside={(e) => e.preventDefault()}
        >
          <Dialog.Title className="dialog-title">{title}</Dialog.Title>
          <Dialog.Description className='dialog-description'>{description}</Dialog.Description>
          {children}
        </Dialog.Content>
      </Dialog.Portal>
    </Dialog.Root>
  )
}

export default Modal;
