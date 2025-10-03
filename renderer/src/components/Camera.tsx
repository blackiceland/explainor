import React from 'react';
import { AbsoluteFill, Easing, interpolate, useCurrentFrame, useVideoConfig } from 'remotion';

type CameraEvent = {
    type: 'pan' | 'zoom';
    time: number;
    duration: number;
    to: {
        x?: number;
        y?: number;
        scale?: number;
    };
};

type CameraProps = {
    children: React.ReactNode;
    cameraEvents?: CameraEvent[];
};

type CameraState = {
    x: number;
    y: number;
    scale: number;
};

export const Camera: React.FC<CameraProps> = ({ children, cameraEvents = [] }) => {
    const frame = useCurrentFrame();
    const { fps } = useVideoConfig();

    const sortedEvents = [...cameraEvents].sort((a, b) => a.time - b.time);
    const initialState: CameraState = { x: 0, y: 0, scale: 1 };

    const finalState = sortedEvents.reduce(
        (prevState, currentEvent) => {
            const eventStartFrame = currentEvent.time * fps;
            const eventEndFrame = eventStartFrame + currentEvent.duration * fps;

            if (frame < eventStartFrame) {
                return prevState;
            }

            if (frame >= eventEndFrame) {
                return {
                    x: currentEvent.to.x ?? prevState.x,
                    y: currentEvent.to.y ?? prevState.y,
                    scale: currentEvent.to.scale ?? prevState.scale,
                };
            }

            const progress = interpolate(frame, [eventStartFrame, eventEndFrame], [0, 1], {
                extrapolateLeft: 'clamp',
                extrapolateRight: 'clamp',
                easing: Easing.inOut(Easing.cubic),
            });

            return {
                x: interpolate(progress, [0, 1], [prevState.x, currentEvent.to.x ?? prevState.x]),
                y: interpolate(progress, [0, 1], [prevState.y, currentEvent.to.y ?? prevState.y]),
                scale: interpolate(progress, [0, 1], [prevState.scale, currentEvent.to.scale ?? prevState.scale]),
            };
        },
        initialState
    );

    const transform = `scale(${finalState.scale}) translateX(${finalState.x}px) translateY(${finalState.y}px)`;

    return (
        <AbsoluteFill style={{
            transformOrigin: 'center center',
            transform,
        }}>
            {children}
        </AbsoluteFill>
    );
};
