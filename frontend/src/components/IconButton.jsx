import {FaGithub} from 'react-icons/fa'

const IconButton=({onClick, ref, title})=>{
    return(
        <button
        tabIndex='0'
        onClick={onClick}
        title={title}
        ref={ref}
        className="p-2 rounded hover:bg-gray-400 active:bg-gray-500 focus:outline-2 "
        aria-label={title}
      >
        <FaGithub className="text-2xl text-gray-700" />
      </button>
    )
}
export default IconButton