import React from 'react';

interface Point {
  x: number;
  y: number;
}

interface AnimatedEdgeProps {
  edge: {
    id: string;
    from: string;
    to: string;
    label: string;
    path: Point[];
    edgeStyle: {
      strokeColor: string;
      strokeWidth: number;
      arrowStyle: string;
    };
  };
  opacity: number;
}

export const AnimatedEdge: React.FC<AnimatedEdgeProps> = ({ edge, opacity }) => {
  if (edge.path.length < 2) return null;

  const pathData = edge.path
    .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
    .join(' ');

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
      <path
        d={pathData}
        stroke={edge.edgeStyle.strokeColor}
        strokeWidth={edge.edgeStyle.strokeWidth}
        fill="none"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <polygon
        points="0,-4 8,0 0,4"
        fill={edge.edgeStyle.strokeColor}
        transform={`translate(${lastPoint.x}, ${lastPoint.y}) rotate(${angle})`}
      />
      {edge.label && (
        <text
          x={(edge.path[0].x + lastPoint.x) / 2}
          y={(edge.path[0].y + lastPoint.y) / 2 - 10}
          fill="#94a3b8"
          fontSize="12"
          fontFamily="Inter, system-ui, sans-serif"
          textAnchor="middle"
        >
          {edge.label}
        </text>
      )}
    </svg>
  );
};

