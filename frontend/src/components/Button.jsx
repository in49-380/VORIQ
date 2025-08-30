const Button=({ref, onClick, children, className='', id})=>{
   const classOnFocus='focus-visible:outline-2'
    return(
        <button 
        type="button"
        id={id}
        tabIndex='0'
        ref={ref}
        onClick={onClick}
        className={`h-10 px-4 py-2 rounded transition ${classOnFocus} ${className}`} >
        {children}</button>
    )
}
export default Button