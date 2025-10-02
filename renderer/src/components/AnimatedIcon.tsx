import { Lottie } from '@remotion/lottie';
import React, { useEffect, useState } from 'react';

export const AnimatedIcon: React.FC<{
    asset: string;
}> = ({ asset }) => {
    const [animationData, setAnimationData] = useState<any | null>(null);

    useEffect(() => {
        // Lottie animations are typically hosted online.
        // For this example, we'll assume the 'asset' is a URL to a JSON file.
        fetch(asset)
            .then((res) => res.json())
            .then(setAnimationData)
            .catch((err) => {
                console.error("Failed to load Lottie animation:", err)
            });
    }, [asset]);

    if (!animationData) {
        return null;
    }

    return <Lottie animationData={animationData} style={{width: 150, height: 150}} />;
};
