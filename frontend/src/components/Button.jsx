const Button=({ref, onClick, children, className='', id})=>{
    return(
        <button 
        type="button"
        id={id}
        tabIndex='0'
        ref={ref}
        onClick={onClick}
        className={className} >
        {children}</button>
    )
}
export default Button