
const Spinner = ({ 
    size='48px', 
    borderWidth='4px', 
    color='gray',
    speed=1 //RPS
}) => {
    size = parseInt(size);
    borderWidth = parseInt(borderWidth);
    const radius = size /2- borderWidth;
    const cx=size/2
    const cy=size/2
    const startX=cx+radius
    const startY=cy
    const endX=cx
    const endY=cy-radius
    
      
    return (
      <svg
      width={size}
      height={size}
      viewBox={`0 0 ${size} ${size}`}
      style={{
        animation: `rotate ${1 / speed}s linear infinite`,
      }}
    >
      <defs>
        <linearGradient id="gradient">
          <stop offset="0%" stopColor={color} stopOpacity={1} />
          <stop offset="100%" stopColor={color} stopOpacity={0}/>

        </linearGradient>
      </defs>
      <path
        d={`M${startX} ${startY} A${radius} ${radius} 0 1 1 ${endX} ${endY} `}
        fill='none'
        stroke="url(#gradient)"
        strokeWidth={borderWidth}
      /> 
      
      <style>{`
        @keyframes rotate {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }`
          }
      </style>
    </svg>
  );
};
        
export default Spinner