import {
    ServerIcon,
    ComputerDesktopIcon,
    CloudIcon,
    CircleStackIcon
} from '@heroicons/react/24/outline';
import React from 'react';

const iconMap: Record<string, React.ElementType> = {
    server: ServerIcon,
    laptop: ComputerDesktopIcon,
    computer: ComputerDesktopIcon,
    cloud: CloudIcon,
    database: CircleStackIcon,
    default: CircleStackIcon,
};

export const Icon: React.FC<{
    asset: string;
    width?: number;
    height?: number;
    strokeWidth?: number;
}> = ({ asset, width = 96, height = 96, strokeWidth = 1 }) => {
    const IconComponent = iconMap[asset] || iconMap.default;
    return <IconComponent style={{ width, height }} strokeWidth={strokeWidth} />;
};
