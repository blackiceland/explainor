import React, {useMemo} from 'react';

interface Point {
  x: number;
  y: number;
}

interface AnimatedEdgeProps {
  edge: {
    id: string;
    from: string;
    to: string;
    label: string | null;
    path: Point[];
    pathLength: number;
    edgeStyle: {
      strokeColor: string;
      strokeWidth: number;
      arrowStyle: string;
      lineStyle?: string;
    };
  };
  opacity?: number;
  strokeDashoffset?: number;
}

export const AnimatedEdge: React.FC<AnimatedEdgeProps> = ({ edge, opacity, strokeDashoffset }) => {
  if (edge.path.length < 2) return null;

  const pathData = useMemo(() => edge.path
    .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
    .join(' '), [edge.path]);

  const dashArray = edge.edgeStyle.lineStyle === 'dashed' ? '10, 10' : undefined;

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
        strokeDasharray={dashArray || edge.pathLength}
        strokeDashoffset={strokeDashoffset}
      />
      {edge.label && (
        <text
          x={edge.path[Math.floor(edge.path.length/2)].x}
          y={edge.path[Math.floor(edge.path.length/2)].y - 10}
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

