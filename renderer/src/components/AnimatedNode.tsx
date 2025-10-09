import React from 'react';

interface AnimatedNodeProps {
  node: {
    id: string;
    label: string;
    icon: string;
    x: number;
    y: number;
    visualStyle: {
      width: number;
      height: number;
      shape: string;
      backgroundColor: string;
      borderColor: string;
      borderWidth: number;
      borderRadius: number;
    };
  };
  opacity: number;
  scale: number;
}

export const AnimatedNode: React.FC<AnimatedNodeProps> = ({ node, opacity, scale }) => {
  const style = node.visualStyle;

  return (
    <div
      style={{
        position: 'absolute',
        left: node.x - style.width / 2,
        top: node.y - style.height / 2,
        width: style.width,
        height: style.height,
        backgroundColor: style.backgroundColor,
        border: `${style.borderWidth}px solid ${style.borderColor}`,
        borderRadius: style.borderRadius,
        opacity,
        transform: `scale(${scale})`,
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        color: '#f8fafc',
        fontSize: 14,
        fontWeight: 500,
        fontFamily: 'Inter, system-ui, sans-serif',
        boxShadow: '0 4px 12px rgba(0, 0, 0, 0.3)',
        transition: 'all 0.3s ease',
      }}
    >
      <div style={{ fontSize: 24, marginBottom: 8 }}>
        {node.icon}
      </div>
      <div>{node.label}</div>
    </div>
  );
};

