const Speed=()=>{
  const startAngle = -Math.PI;
  const endAngle = 0;  
  const cx =100;                
  const cy = 100;                
  const nLongTicks = 5;               
  const rLongInner = 70;               
  const rLongOuter =85;  

  const nMiddleTicks = 10;               
  const rMiddleInner = 75;               
  const rMiddleOuter = 85;  

  const nShortTicks = 4;               
  const rShortInner = 80;               
  const rShortOuter = 85;  
  
  const basicAngle=(endAngle-startAngle)/(nLongTicks-1);

  const ticks =(nTicks, rInner, rOuter, offset)=>
    Array.from({ length: nTicks}).map((_, i) => {
    
    const angle = startAngle + basicAngle * i+offset;
    const x1 = cx + rInner * Math.cos(angle);
    const y1 = cy + rInner * Math.sin(angle);
    const x2 = cx + rOuter * Math.cos(angle);
    const y2 = cy + rOuter * Math.sin(angle);
    return <line key={i} 
    className="speedTicks"
    x1={x1} y1={y1} x2={x2} y2={y2}
     />;
  });

  const nums=()=>
    [0, 50, 100, 150, 200].map((num, i) => {
  const angle = startAngle + basicAngle * i;
  const x = cx + (rLongOuter - 20) * Math.cos(angle);
  const y = cy + (rLongOuter - 25) * Math.sin(angle);
  const deg = (angle * 180) / Math.PI+90;
  return (
    <text className="speedNums"
      key={i}
      x={x}
      y={y}
      textAnchor="middle"
      alignmentBaseline="middle"
      transform={`rotate(${deg}, ${x}, ${y})`}
    >
      {num}
    </text>
  );
})

    return(
       <div className="speedometer">
        <svg viewBox="0 0 220 120"  className="dial">


       <defs>
        <linearGradient id="arcGradient" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" stopColor="green" />
            <stop offset="50%" stopColor="yellow" />
            <stop offset="100%" stopColor="red" />
        </linearGradient>
       </defs>
    
        <path d="M5 100 A95 95 0 0 1 195 100" 
        stroke="url(#arcGradient)"
        className="halbSircle" />
        <line x1={5} y1={100} x2={5} y2={107} stroke="green" strokeWidth="10" />
        <line x1={195} y1={100} x2={195} y2={107} stroke="red" strokeWidth="10" />

        {ticks(nLongTicks, rLongInner, rLongOuter, 0)} 
        {nums()}
        {ticks(nMiddleTicks, rMiddleInner, rMiddleOuter,basicAngle/2)} 
        {[...Array(4)].map((_, i) => 
          ticks(nShortTicks, rShortInner, rShortOuter,( i+1 )* basicAngle / 10)
        )}
        {[...Array(4)].map((_, i) => 
          ticks(nShortTicks, rShortInner, rShortOuter,( i+1 )* basicAngle / 10+basicAngle/2)
        )}


    
        <line x1="100" y1="100" x2="15" y2="100" 
          className="needle"/>

 
  </svg>
</div>
    )
}
export default Speed;
