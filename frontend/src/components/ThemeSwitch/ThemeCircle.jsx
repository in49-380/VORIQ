

const HalfCircle = ({firstColor, secondColor}) => {

  return (
    <svg width="30" height="30" viewBox="0 0 60 60">
      <path d="M30 0 A30 30 0 0 0 30 60 Z" fill={firstColor} />
      <path d="M30 0 A30 30 0 0 1 30 60 Z" fill={secondColor} />
    </svg>
  );
};

export default HalfCircle;
