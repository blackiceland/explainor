import { Lottie } from '@remotion/lottie';
import React, { useEffect, useState } from 'react';
import serverProcessing from '../assets/server-processing.json';

const animationMap: Record<string, any> = {
    'server-processing': serverProcessing,
};

export const AnimatedIcon: React.FC<{
    asset: string;
}> = ({ asset }) => {
    const [animationData, setAnimationData] = useState<any | null>(null);

    useEffect(() => {
        // Check if it's a built-in animation
        if (animationMap[asset]) {
            setAnimationData(animationMap[asset]);
            return;
        }

        // Otherwise, try to fetch it as a URL
        fetch(asset)
            .then((res) => res.json())
            .then(setAnimationData)
            .catch((err) => {
                console.error("Failed to load Lottie animation:", err);
            });
    }, [asset]);

    if (!animationData) {
        return null;
    }

    return (
        <div style={{ width: 150, height: 150, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Lottie animationData={animationData} />
        </div>
    );
};
