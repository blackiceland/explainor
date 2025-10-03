import React, { useState, useEffect } from 'react';
import { AbsoluteFill, useCurrentFrame, useVideoConfig, interpolate, Easing, delayRender, continueRender } from 'remotion';
import { z } from 'zod';
import { loadFont } from '@remotion/google-fonts/Manrope';
import { Icon } from '../components/Icon';
import { AnimatedIcon } from '../components/AnimatedIcon';
import { Shape } from '../components/Shape';
import { Camera } from '../components/Camera';

const font = loadFont();

const timelineEventSchema = z.object({
    elementId: z.string(),
    type: z.enum(["icon", "text", "arrow", "animated-icon", "shape", "group"]),
    asset: z.string().optional(),
    content: z.string().optional(),
    action: z.enum(["appear", "animate", "disappear"]),
    time: z.number(),
    duration: z.number().optional(),
    from: z.object({ x: z.number(), y: z.number() }).optional(),
    to: z.object({ x: z.number(), y: z.number() }).optional(),
    children: z.array(z.string()).optional(),
    props: z.any().optional(),
});

const mainInternalSchema = z.object({
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

interface TimelineNode extends Omit<TimelineEvent, 'children'> {
    children: TimelineNode[];
}

const RenderTimelineEvent: React.FC<{
    event: TimelineNode;
    frame: number;
    fps: number;
    timeline: TimelineEvent[];
    isChild?: boolean;
}> = ({ event, frame, fps, timeline, isChild = false }) => {
    
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
        left: props.x,
        top: props.y,
        transform: isChild ? `scale(${scale})` : `translate(-50%, -50%) scale(${scale})`,
        opacity,
    };
    
    if (event.type === 'group') {
        const groupStyle: React.CSSProperties = {
            ...commonStyle,
            width: props.width,
            height: props.height,
        };
        return (
            <div style={groupStyle}>
                {event.children.map((child) => (
                    <RenderTimelineEvent
                        key={child.elementId}
                        event={child}
                        frame={frame}
                        fps={fps}
                        timeline={timeline}
                        isChild={true}
                    />
                ))}
            </div>
        );
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
                />
            </div>
        );
    }

    if (event.type === 'text') {
        const textContainerStyle: React.CSSProperties = { ...commonStyle };
        if (isChild) {
            textContainerStyle.width = '100%';
            textContainerStyle.height = '100%';
            textContainerStyle.display = 'flex';
            textContainerStyle.alignItems = 'center';
            textContainerStyle.justifyContent = 'center';
        }

        return (
            <div style={textContainerStyle}>
                <div style={{
                    fontSize: props.fontSize || 32,
                    fontWeight: '600',
                    color: '#000000',
                    textAlign: 'center',
                    fontFamily: font.fontFamily,
                }}>
                    {event.content}
                </div>
            </div>
        );
    }

    if (event.type === 'icon') {
        return (
            <div style={{ ...commonStyle, filter: 'url(#realistic-shadow)' }}>
                <Icon asset={event.asset || 'default'} width={96} height={96} strokeWidth={1} />
            </div>
        );
    }

    if (event.type === 'animated-icon') {
        return (
            <div style={{ ...commonStyle, filter: 'url(#realistic-shadow)' }}>
                <AnimatedIcon asset={event.asset || ''} />
            </div>
        );
    }

    if (event.type === 'arrow' && event.from && event.to) {
        const angle = Math.atan2(event.to.y - event.from.y, event.to.x - event.from.x) * (180 / Math.PI);
        const length = Math.sqrt(Math.pow(event.to.x - event.from.x, 2) + Math.pow(event.to.y - event.from.y, 2));

        const animationDuration = (event.duration || 1) * fps;
        const startFrame = event.time * fps;
        const progress = interpolate(frame, [startFrame, startFrame + animationDuration], [0, 1], { extrapolateLeft: 'clamp', extrapolateRight: 'clamp', easing: Easing.inOut(Easing.ease) });
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

export const mainSchema = mainInternalSchema;
export const Main: React.FC<z.infer<typeof mainSchema>> = ({ timeline, canvas, camera }) => {
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

    const buildTree = (timeline: TimelineEvent[]): TimelineNode[] => {
        const elementsMap = new Map<string, TimelineNode>();
        timeline.forEach(event => {
            if (elementsMap.has(event.elementId) && (event.action === 'disappear' || event.action === 'animate')) {
                const existingEvent = elementsMap.get(event.elementId);
                if (existingEvent && existingEvent.action === 'appear') {
                     // Prefer 'appear' event as the base, but merge props if 'animate' has them
                    if (event.action === 'animate') {
                        elementsMap.set(event.elementId, { ...existingEvent, ...event, children: existingEvent.children });
                    }
                    return;
                }
            }
            if (elementsMap.has(event.elementId) && event.action === 'disappear') {
                return;
            }
            elementsMap.set(event.elementId, { ...event, children: [] });
        });

        const rootElements: TimelineNode[] = [];
        const allChildrenIds = new Set<string>();

        timeline.forEach(event => {
            if (event.type === 'group' && event.children) {
                const parentNode = elementsMap.get(event.elementId);
                if (parentNode) {
                    event.children.forEach(childId => {
                        const childNode = elementsMap.get(childId);
                        if (childNode) {
                            parentNode.children.push(childNode);
                            allChildrenIds.add(childId);
                        }
                    });
                }
            }
        });

        elementsMap.forEach(element => {
            if (!allChildrenIds.has(element.elementId)) {
                rootElements.push(element);
            }
        });

        return rootElements;
    };
    
    const rootElements = buildTree(timeline);
    
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
