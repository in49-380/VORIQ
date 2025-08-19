import * as Dialog from '@radix-ui/react-dialog';

const Modal = ({ open, onOpenChange, children, title, description, fullscreen = false }) => {
  return (
    <Dialog.Root modal open={open} onOpenChange={onOpenChange}>
      <Dialog.Portal>
        <Dialog.Overlay  
          className="fixed inset-0 bg-black bg-opacity-10 backdrop-blur-sm" 
        />
        <Dialog.Content  
          className={
            fullscreen
              ? 'fixed inset-0 flex flex-col justify-center items-center bg-white bg-opacity-60 backdrop-blur-sm z-50'
              : 'fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[90vw] max-w-md bg-white border-2 border-red-500 rounded-md p-6 shadow-lg text-black text-lg font-bold'
          }
          onInteractOutside={(e) => e.preventDefault()}
        >
          <Dialog.Title className="text-2xl font-bold text-blue-900 text-center mb-2">{title}</Dialog.Title>
          <Dialog.Description>{description}</Dialog.Description>
          {children}
        </Dialog.Content>
      </Dialog.Portal>
    </Dialog.Root>
  )
}

export default Modal;
