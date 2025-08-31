import {FaGithub} from 'react-icons/fa'

const IconButton=({onClick, ref, title})=>{
    return(
        <button
        tabIndex='0'
        onClick={onClick}
        title={title}
        ref={ref}
        aria-label={title}
      >
        <FaGithub size={30}/>
      </button>
    )
}
export default IconButton