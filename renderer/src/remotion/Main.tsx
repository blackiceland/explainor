import React, { useState, useEffect } from 'react';
import { AbsoluteFill, useCurrentFrame, useVideoConfig, interpolate, Easing, delayRender, continueRender, Composition } from 'remotion';
import { z } from 'zod';
import { loadFont } from '@remotion/google-fonts/Manrope';
import { Icon } from '../components/Icon';
import { AnimatedIcon } from '../components/AnimatedIcon';
import { Shape } from '../components/Shape';
import { Camera } from '../components/Camera';

const font = loadFont();

const timelineEventSchema = z.object({
    elementId: z.string(),
    type: z.enum(["icon", "text", "arrow", "animated-icon", "shape", "group", "camera"]),
    asset: z.string().optional(),
    content: z.string().optional(),
    action: z.enum(["appear", "animate", "disappear", "move"]),
    time: z.number(),
    duration: z.number().optional(),
    from: z.object({ x: z.number(), y: z.number() }).optional(),
    to: z.object({ x: z.number(), y: z.number() }).optional(),
    path: z.array(z.object({ x: z.number(), y: z.number() })).optional(),
    children: z.array(z.string()).optional(),
    props: z.any().optional(),
});

export const mainSchema = z.object({
    canvas: z.object({
        width: z.number(),
        height: z.number(),
        backgroundColor: z.string(),
    }),
    totalDuration: z.number(),
    timeline: z.array(timelineEventSchema),
    camera: z.array(z.object({
        type: z.enum(['pan', 'zoom']),
        time: z.number(),
        duration: z.number(),
        to: z.object({
            x: z.number().optional(),
            y: z.number().optional(),
            scale: z.number().optional(),
        }),
    })).optional(),
});


type TimelineEvent = z.infer<typeof timelineEventSchema>;

const RenderTimelineEvent: React.FC<{
    event: TimelineEvent;
    frame: number;
    fps: number;
    timeline: TimelineEvent[];
    isChild?: boolean;
}> = ({ event, frame, fps, timeline, isChild = false }) => {
    
    if (event.type === 'camera') {
        return null;
    }

    const getElementLifecycle = (elementId: string, allEvents: TimelineEvent[]) => {
        const events = allEvents.filter(e => e.elementId === elementId);
        const appearEvent = events.find(e => e.action === 'appear' || e.action === 'animate');
        const disappearEvent = events.find(e => e.action === 'disappear');
        
        return { appearEvent, disappearEvent };
    };

    const { appearEvent, disappearEvent } = getElementLifecycle(event.elementId, timeline);
    
    const appearTime = appearEvent ? appearEvent.time * fps : 0;
    const disappearTime = disappearEvent ? disappearEvent.time * fps : Infinity;

    if (frame < appearTime || frame >= disappearTime) {
        return null;
    }

    const fadeInDuration = 20;
    const fadeOutDuration = 20;
    
    let opacity = 1;
    
    if (frame < appearTime + fadeInDuration) {
        opacity = interpolate(frame, [appearTime, appearTime + fadeInDuration], [0, 1], { extrapolateLeft: 'clamp', extrapolateRight: 'clamp', easing: Easing.out(Easing.ease) });
    }
    
    if (disappearTime !== Infinity && frame >= disappearTime - fadeOutDuration) {
        opacity = interpolate(frame, [disappearTime - fadeOutDuration, disappearTime], [1, 0], { extrapolateLeft: 'clamp', extrapolateRight: 'clamp', easing: Easing.in(Easing.ease) });
    }

    const scale = interpolate(frame, [appearTime, appearTime + fadeInDuration], [0.8, 1], { extrapolateLeft: 'clamp', extrapolateRight: 'clamp', easing: Easing.out(Easing.back(1.5)) });
    
    const props = event.props || {};

    const commonStyle: React.CSSProperties = {
        position: 'absolute',
        opacity,
    };
    
    if (!isChild) {
        commonStyle.left = props.x;
        commonStyle.top = props.y;
        commonStyle.transform = `translate(-50%, -50%) scale(${scale})`;
    }

    if (event.type === 'shape') {
        return (
            <div style={{ ...commonStyle, filter: 'url(#realistic-shadow)' }}>
                <Shape
                    type={props.shapeType || 'rectangle'}
                    width={props.width}
                    height={props.height}
                    fillColor={props.fillColor}
                    strokeColor={props.strokeColor}
                    strokeWidth={props.strokeWidth}
                    radius={props.radius}
                    icon={props.icon}
                    label={props.label}
                />
            </div>
        );
    }
    
    if (event.type === 'arrow' && event.from && event.to) {
        const animationDuration = (event.duration || 1) * fps;
        const startFrame = event.time * fps;
        const progress = interpolate(frame, [startFrame, startFrame + animationDuration], [0, 1], { extrapolateLeft: 'clamp', extrapolateRight: 'clamp', easing: Easing.inOut(Easing.ease) });

        if (event.path && event.path.length > 2) {
            let totalLength = 0;
            const segments: { from: { x: number; y: number }; to: { x: number; y: number }; length: number; startDist: number; endDist: number }[] = [];

            for (let i = 0; i < event.path.length - 1; i++) {
                const from = event.path[i];
                const to = event.path[i + 1];
                const segmentLength = Math.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.y - from.y, 2));
                segments.push({
                    from,
                    to,
                    length: segmentLength,
                    startDist: totalLength,
                    endDist: totalLength + segmentLength,
                });
                totalLength += segmentLength;
            }

            const currentDist = totalLength * progress;

            return (
                <svg
                    style={{ position: 'absolute', left: 0, top: 0, width: '100%', height: '100%', opacity, pointerEvents: 'none' }}
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <defs>
                        <marker
                            id={`arrowhead-${event.elementId}`}
                            markerWidth="10"
                            markerHeight="10"
                            refX="9"
                            refY="3"
                            orient="auto"
                            markerUnits="strokeWidth"
                        >
                            <path d="M0,0 L0,6 L9,3 z" fill="#374151" />
                        </marker>
                    </defs>
                    {segments.map((segment, idx) => {
                        const segmentProgress = Math.max(0, Math.min(1,
                            (currentDist - segment.startDist) / segment.length
                        ));
                        
                        if (segmentProgress <= 0) return null;

                        const endX = segment.from.x + (segment.to.x - segment.from.x) * segmentProgress;
                        const endY = segment.from.y + (segment.to.y - segment.from.y) * segmentProgress;

                        const isLastSegment = idx === segments.length - 1;
                        const showArrowhead = isLastSegment && progress > 0.5;

                        return (
                            <line
                                key={idx}
                                x1={segment.from.x}
                                y1={segment.from.y}
                                x2={endX}
                                y2={endY}
                                stroke="#374151"
                                strokeWidth="3"
                                markerEnd={showArrowhead ? `url(#arrowhead-${event.elementId})` : undefined}
                                filter="drop-shadow(0px 2px 4px rgba(0, 0, 0, 0.15))"
                            />
                        );
                    })}
                </svg>
            );
        } else {
            const angle = Math.atan2(event.to.y - event.from.y, event.to.x - event.from.x) * (180 / Math.PI);
            const length = Math.sqrt(Math.pow(event.to.x - event.from.x, 2) + Math.pow(event.to.y - event.from.y, 2));
            const currentLength = length * progress;

            const arrowContainerStyle: React.CSSProperties = {
                position: 'absolute',
                left: event.from.x,
                top: event.from.y,
                height: 3,
                width: length,
                transform: `rotate(${angle}deg)`,
                transformOrigin: '0 50%',
                opacity,
            };

            const lineStyle: React.CSSProperties = {
                position: 'absolute',
                height: '100%',
                width: `${progress * 100}%`,
                backgroundColor: '#374151',
            };

            const headStyle: React.CSSProperties = {
                position: 'absolute',
                left: currentLength - 12,
                top: -5,
                width: 0,
                height: 0,
                borderStyle: 'solid',
                borderWidth: '6px 0 6px 12px',
                borderColor: 'transparent transparent transparent #374151',
            };

            return (
                <div style={arrowContainerStyle}>
                    <div style={lineStyle} />
                    {progress > 0.5 && <div style={headStyle} />}
                </div>
            );
        }
    }
    
    if (event.type === 'text') {
        const textStyle: React.CSSProperties = {
            ...commonStyle,
            left: props.x,
            top: props.y,
            transform: 'translate(-50%, -50%)',
            fontSize: props.fontSize || 16,
            fontWeight: '600',
            color: '#1F2937',
            textAlign: 'center',
            fontFamily: font.fontFamily,
        };
        return <div style={textStyle}>{event.content}</div>;
    }

    return null;
};

export const Main: React.FC<z.infer<typeof mainSchema>> = ({ timeline, canvas, totalDuration, camera }) => {
    const frame = useCurrentFrame();
    const { fps } = useVideoConfig();

    const [handle] = useState(() => delayRender());
    const [fontLoaded, setFontLoaded] = useState(false);

    useEffect(() => {
        font.waitUntilDone().then(() => {
            setFontLoaded(true);
            continueRender(handle);
        });
    }, [handle]);

    if (!fontLoaded) {
        return null;
    }
    
    const rootElements = timeline.filter(e => e.type !== 'group' && !timeline.some(p => p.type === 'group' && p.children?.includes(e.elementId)));

    return (
        <AbsoluteFill style={{ backgroundColor: canvas.backgroundColor, fontFamily: font.fontFamily }}>
            <Camera cameraEvents={camera}>
                <svg width="0" height="0" style={{ position: 'absolute' }}>
                    <defs>
                        <filter id="realistic-shadow" x="-100%" y="-100%" width="300%" height="300%">
                            <feGaussianBlur in="SourceAlpha" stdDeviation="4" result="blur1"/>
                            <feOffset in="blur1" dx="10" dy="10" result="offsetBlur1"/>
                            <feComponentTransfer in="offsetBlur1" result="shadow1">
                                <feFuncA type="linear" slope="0.2"/>
                            </feComponentTransfer>
                            
                            <feGaussianBlur in="SourceAlpha" stdDeviation="8" result="blur2"/>
                            <feOffset in="blur2" dx="15" dy="15" result="offsetBlur2"/>
                            <feComponentTransfer in="offsetBlur2" result="shadow2">
                                <feFuncA type="linear" slope="0.15"/>
                            </feComponentTransfer>
    
                            <feMerge>
                                <feMergeNode in="shadow2"/>
                                <feMergeNode in="shadow1"/>
                                <feMergeNode in="SourceGraphic"/>
                            </feMerge>
                        </filter>
                    </defs>
                </svg>
                {rootElements.map((event) => (
                    <RenderTimelineEvent
                        key={event.elementId}
                        event={event}
                        frame={frame}
                        fps={fps}
                        timeline={timeline}
                    />
                ))}
            </Camera>
        </AbsoluteFill>
    );
};
