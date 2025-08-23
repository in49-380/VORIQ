const Button=({ref, onClick, children, className=''})=>{
   const classOnFocus='focus-visible:outline-2'
    return(
        <button 
        type="button"
        tabIndex='0'
        ref={ref}
        onClick={onClick}
        className={`h-10 px-4 py-2 rounded transition ${classOnFocus} ${className}`} >
        {children}</button>
    )
}
export default Button