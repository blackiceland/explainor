import React from 'react';

type ShapeProps = {
    type: 'rectangle' | 'circle';
    width: number;
    height: number;
    fillColor?: string;
    strokeColor?: string;
    strokeWidth?: number;
    radius?: number; 
};

export const Shape: React.FC<ShapeProps> = ({
    type,
    width,
    height,
    fillColor = 'transparent',
    strokeColor = '#000000',
    strokeWidth = 1,
    radius = 8,
}) => {
    const commonProps = {
        fill: fillColor,
        stroke: strokeColor,
        strokeWidth: strokeWidth,
    };

    if (type === 'rectangle') {
        return (
            <svg width={width} height={height} style={{ overflow: 'visible' }}>
                <rect 
                    width={width} 
                    height={height} 
                    rx={radius} 
                    {...commonProps} 
                />
            </svg>
        );
    }

    if (type === 'circle') {
        return (
            <svg width={width} height={height} style={{ overflow: 'visible' }}>
                <ellipse 
                    cx={width / 2} 
                    cy={height / 2} 
                    rx={width / 2} 
                    ry={height / 2} 
                    {...commonProps} 
                />
            </svg>
        );
    }

    return null;
};
