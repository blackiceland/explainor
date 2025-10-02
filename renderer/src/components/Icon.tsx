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
    className?: string;
}> = ({ asset, className }) => {
    const IconComponent = iconMap[asset] || iconMap.default;
    return <IconComponent className={className} />;
};
