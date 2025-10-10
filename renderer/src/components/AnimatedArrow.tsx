import React from 'react';

interface Point {
  x: number;
  y: number;
}

interface AnimatedArrowProps {
  edge: {
    id: string;
    path: Point[];
    edgeStyle: {
      strokeColor: string;
    };
  };
  opacity: number;
}

export const AnimatedArrow: React.FC<AnimatedArrowProps> = ({ edge, opacity }) => {
  if (edge.path.length < 2) return null;

  const lastPoint = edge.path[edge.path.length - 1];
  const secondLastPoint = edge.path[edge.path.length - 2];
  
  const angle = Math.atan2(
    lastPoint.y - secondLastPoint.y,
    lastPoint.x - secondLastPoint.x
  ) * (180 / Math.PI);

  return (
    <svg
      style={{
        position: 'absolute',
        left: 0,
        top: 0,
        width: '100%',
        height: '100%',
        pointerEvents: 'none',
        opacity,
      }}
    >
      <polygon
        points="0,-4 8,0 0,4"
        fill={edge.edgeStyle.strokeColor}
        transform={`translate(${lastPoint.x}, ${lastPoint.y}) rotate(${angle})`}
      />
    </svg>
  );
};

