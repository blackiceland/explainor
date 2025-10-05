import React from 'react';
import { Icon } from './Icon';

export const Shape: React.FC<{
    type: 'rectangle' | 'circle';
    width: number;
    height: number;
    fillColor?: string;
    strokeColor?: string;
    strokeWidth?: number;
    radius?: number;
    icon?: string;
    label?: string;
}> = ({ type, width, height, fillColor = '#FFFFFF', strokeColor = '#E0E0E0', strokeWidth = 1, radius = 16, icon, label }) => {
    
    const commonStyle: React.CSSProperties = {
        width,
        height,
        backgroundColor: fillColor,
        border: `${strokeWidth}px solid ${strokeColor}`,
        borderRadius: type === 'circle' ? '50%' : radius,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '8px',
        padding: '10px',
        boxSizing: 'border-box',
    };

    return (
        <div style={commonStyle}>
            {icon && <Icon asset={icon} width={48} height={48} strokeWidth={1.5} />}
            {label && (
                <div style={{ fontSize: 20, fontWeight: '600', color: '#1F2937', textAlign: 'center' }}>
                    {label}
                </div>
            )}
        </div>
    );
};
