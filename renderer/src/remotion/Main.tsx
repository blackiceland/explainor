import React, { useState, useEffect } from 'react';
import { AbsoluteFill, useCurrentFrame, useVideoConfig, interpolate, Easing, delayRender, continueRender } from 'remotion';
import { z } from 'zod';
import { loadFont } from '@remotion/google-fonts/Manrope';
import { Icon } from '../components/Icon';
import { AnimatedIcon } from '../components/AnimatedIcon';

const font = loadFont();

const timelineEventSchema = z.object({
    elementId: z.string(),
    type: z.enum(["icon", "text", "arrow", "animated-icon"]).optional(),
    asset: z.string().optional(),
    content: z.string().optional(),
    action: z.enum(["appear", "animate", "disappear"]),
    time: z.number(),
    duration: z.number().optional(),
    from: z.object({ x: z.number(), y: z.number() }).optional(),
    to: z.object({ x: z.number(), y: z.number() }).optional(),
    props: z.any().optional(),
});

const mainSchema = z.object({
    canvas: z.object({
        width: z.number(),
        height: z.number(),
        backgroundColor: z.string(),
    }),
    totalDuration: z.number(),
    timeline: z.array(timelineEventSchema)
});


type TimelineEvent = z.infer<typeof timelineEventSchema>;

// Helper to find lifecycle events for an element
const getElementLifecycle = (elementId: string, timeline: TimelineEvent[]) => {
    const events = timeline.filter(e => e.elementId === elementId);
    const appearEvent = events.find(e => e.action === 'appear');
    const disappearEvent = events.find(e => e.action === 'disappear');
    
    return { appearEvent, disappearEvent };
};

const renderElement = (event: TimelineEvent, frame: number, fps: number, allEvents: TimelineEvent[]) => {
    // Only render for 'appear' or 'animate' actions
    if (event.action === 'disappear') {
        return null;
    }

    const timeInSeconds = frame / fps;
    const startFrame = event.time * fps;

    // Get lifecycle for this element
    const { appearEvent, disappearEvent } = getElementLifecycle(event.elementId, allEvents);
    
    // Determine time boundaries
    const appearTime = appearEvent ? appearEvent.time * fps : startFrame;
    const disappearTime = disappearEvent ? disappearEvent.time * fps : Infinity;

    // Don't render if outside lifecycle
    if (frame < appearTime || frame >= disappearTime) {
        return null;
    }

    const props = event.props || {};
    const fadeInDuration = 20;
    const fadeOutDuration = 20;
    
    // Calculate opacity with fade in and fade out
    let opacity = 1;
    
    // Fade in
    if (frame < appearTime + fadeInDuration) {
        opacity = interpolate(
            frame,
            [appearTime, appearTime + fadeInDuration],
            [0, 1],
            {
                extrapolateLeft: 'clamp',
                extrapolateRight: 'clamp',
                easing: Easing.out(Easing.ease),
            }
        );
    }
    
    // Fade out
    if (disappearTime !== Infinity && frame >= disappearTime - fadeOutDuration) {
        opacity = interpolate(
            frame,
            [disappearTime - fadeOutDuration, disappearTime],
            [1, 0],
            {
                extrapolateLeft: 'clamp',
                extrapolateRight: 'clamp',
                easing: Easing.in(Easing.ease),
            }
        );
    }

    const scale = interpolate(
        frame,
        [appearTime, appearTime + fadeInDuration],
        [0.8, 1],
        {
            extrapolateLeft: 'clamp',
            extrapolateRight: 'clamp',
            easing: Easing.out(Easing.back(1.5)),
        }
    );
    
            if (event.type === 'animated-icon') {
                const style: React.CSSProperties = {
                    position: 'absolute',
                    left: props.x,
                    top: props.y,
                    transform: `translate(-50%, -50%) scale(${scale})`,
                    opacity,
                    filter: 'url(#realistic-shadow)',
                };

                return (
                    <div style={style}>
                        <AnimatedIcon asset={event.asset || ''} />
                    </div>
                );
            }

    if (event.type === 'text') {
        const style: React.CSSProperties = {
            position: 'absolute',
            left: props.x,
            top: props.y,
            fontSize: props.fontSize || 32,
            fontWeight: '600',
            color: '#000000',
            textAlign: 'center',
            transform: `translate(-50%, -50%) scale(${scale})`,
            opacity,
            fontFamily: font.fontFamily,
            filter: 'url(#realistic-shadow)',
        };
        return <div style={style}>{event.content}</div>;
    }

            if (event.type === 'icon') {
                const style: React.CSSProperties = {
                    position: 'absolute',
                    left: props.x,
                    top: props.y,
                    transform: `translate(-50%, -50%) scale(${scale})`,
                    opacity,
                    color: '#000000',
                    filter: 'url(#realistic-shadow)',
                };

                return (
                    <div style={style}>
                        <Icon asset={event.asset || 'default'} width={96} height={96} strokeWidth={1} />
                    </div>
                );
            }

    if (event.type === 'arrow' && event.from && event.to) {
        const animationDuration = (event.duration || 1) * fps;
        const endFrame = startFrame + animationDuration;

        const progress = interpolate(
            frame,
            [startFrame, endFrame],
            [0, 1],
            {
                extrapolateLeft: 'clamp',
                extrapolateRight: 'clamp',
                easing: Easing.inOut(Easing.ease),
            }
        );
        
        const angle = Math.atan2(event.to.y - event.from.y, event.to.x - event.from.x) * (180 / Math.PI);
        const length = Math.sqrt(Math.pow(event.to.x - event.from.x, 2) + Math.pow(event.to.y - event.from.y, 2));

        const currentLength = length * progress;

        const arrowContainerStyle: React.CSSProperties = {
            position: 'absolute',
            left: event.from.x,
            top: event.from.y,
            height: 2, 
            width: length,
            transform: `rotate(${angle}deg)`,
            transformOrigin: '0 50%',
            opacity,
        };

        const shadowContainerStyle: React.CSSProperties = {
            ...arrowContainerStyle,
            left: event.from.x + 10,
            top: event.from.y + 10,
            opacity: opacity * 0.25,
            filter: 'blur(5px)',
        };

        const lineStyle: React.CSSProperties = {
            position: 'absolute',
            height: '100%',
            width: `${progress * 100}%`,
            backgroundColor: '#000000',
        };

        const headStyle: React.CSSProperties = {
            position: 'absolute',
            left: currentLength - 10,
            top: -4, 
            width: 0,
            height: 0,
            borderStyle: 'solid',
            borderWidth: '5px 0 5px 10px',
            borderColor: 'transparent transparent transparent #000000',
        };

        return (
            <>
                <div style={shadowContainerStyle}>
                    <div style={lineStyle} />
                    <div style={headStyle} />
                </div>
                <div style={arrowContainerStyle}>
                    <div style={lineStyle} />
                    <div style={headStyle} />
                </div>
            </>
        );
    }

    return null;
};

export const Main: React.FC<z.infer<typeof mainSchema>> = ({ timeline, canvas }) => {
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

    return (
        <AbsoluteFill style={{ backgroundColor: canvas.backgroundColor, fontFamily: font.fontFamily }}>
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
            {timeline.map((event, index) => (
                <React.Fragment key={`${event.elementId}-${index}`}>
                    {renderElement(event, frame, fps, timeline)}
                </React.Fragment>
            ))}
        </AbsoluteFill>
    );
};
