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
      arrowStyle: string;
    };
  };
  opacity?: number;
}

const ArrowHead: React.FC<{ style: string; angle: number; position: Point; color: string }> = ({ style, angle, position, color }) => {
  if (style === 'none') {
    return null;
  }
  if (style === 'triangle') {
    return <polygon points="0,-4 8,0 0,4" fill={color} transform={`translate(${position.x}, ${position.y}) rotate(${angle})`} />;
  }
  if (style === 'circle') {
    return <circle cx={position.x} cy={position.y} r={4} fill={color} />;
  }
  return <line x1={position.x - 4} y1={position.y} x2={position.x + 4} y2={position.y} stroke={color} strokeWidth={2} transform={`rotate(${angle}, ${position.x}, ${position.y})`} />;
};

export const AnimatedArrow: React.FC<AnimatedArrowProps> = ({ edge, opacity }) => {
  if (edge.path.length < 2) return null;

  const lastPoint = edge.path[edge.path.length - 1];
  const secondLastPoint = edge.path[edge.path.length - 2];
  const angle = Math.atan2(lastPoint.y - secondLastPoint.y, lastPoint.x - secondLastPoint.x) * (180 / Math.PI);

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
      <ArrowHead style={edge.edgeStyle.arrowStyle} angle={angle} position={lastPoint} color={edge.edgeStyle.strokeColor} />
    </svg>
  );
};

