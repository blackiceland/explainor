import React from 'react';
import { AbsoluteFill, useCurrentFrame, useVideoConfig, interpolate, Easing } from 'remotion';
import { z } from 'zod';

const timelineEventSchema = z.object({
    elementId: z.string(),
    type: z.enum(["icon", "text", "arrow"]),
    asset: z.string().optional(),
    content: z.string().optional(),
    action: z.enum(["appear", "animate"]),
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

const renderElement = (event: TimelineEvent, frame: number, fps: number) => {
    const timeInSeconds = frame / fps;
    const startFrame = event.time * fps;

    if (timeInSeconds < event.time) {
        return null;
    }

    const props = event.props || {};
    const fadeInDuration = 20;
    
    const opacity = interpolate(
        frame,
        [startFrame, startFrame + fadeInDuration],
        [0, 1],
        {
            extrapolateLeft: 'clamp',
            extrapolateRight: 'clamp',
            easing: Easing.out(Easing.ease),
        }
    );

    const scale = interpolate(
        frame,
        [startFrame, startFrame + fadeInDuration],
        [0.8, 1],
        {
            extrapolateLeft: 'clamp',
            extrapolateRight: 'clamp',
            easing: Easing.out(Easing.back(1.5)),
        }
    );
    
    if (event.type === 'text') {
        const style: React.CSSProperties = {
            position: 'absolute',
            left: props.x,
            top: props.y,
            fontSize: props.fontSize || 24,
            fontWeight: 'bold',
            color: '#000',
            textAlign: 'center',
            transform: `translate(-50%, -50%) scale(${scale})`,
            opacity,
        };
        return <div style={style}>{event.content}</div>;
    }

    if (event.type === 'icon') {
        const style: React.CSSProperties = {
            position: 'absolute',
            left: props.x,
            top: props.y,
            fontSize: props.fontSize || 120,
            transform: `translate(-50%, -50%) scale(${scale})`,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: '10px',
            opacity,
        };

        const iconMap: Record<string, string> = {
            'laptop': '💻',
            'computer': '🖥️',
            'server': '🗄️',
            'database': '💾',
            'cloud': '☁️',
        };

        const icon = iconMap[event.asset || ''] || '📦';

        return (
            <div style={style}>
                <div style={{ fontSize: props.fontSize || 120 }}>{icon}</div>
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

        const currentX = interpolate(progress, [0, 1], [event.from.x, event.to.x]);
        const currentY = interpolate(progress, [0, 1], [event.from.y, event.to.y]);

        const angle = Math.atan2(event.to.y - event.from.y, event.to.x - event.from.x);
        const length = Math.sqrt(
            Math.pow(event.to.x - event.from.x, 2) + 
            Math.pow(event.to.y - event.from.y, 2)
        );
        const currentLength = length * progress;

        const arrowStyle: React.CSSProperties = {
            position: 'absolute',
            left: event.from.x,
            top: event.from.y,
            width: currentLength,
            height: 4,
            backgroundColor: '#007bff',
            transformOrigin: '0 50%',
            transform: `rotate(${angle}rad)`,
            opacity,
        };

        const arrowHeadStyle: React.CSSProperties = {
            position: 'absolute',
            left: currentX - 10,
            top: currentY - 10,
            fontSize: 20,
            opacity: progress > 0.8 ? opacity : 0,
            transition: 'opacity 0.2s',
        };

        return (
            <>
                <div style={arrowStyle} />
                <div style={arrowHeadStyle}>▶</div>
            </>
        );
    }

    return null;
};

export const Main: React.FC<z.infer<typeof mainSchema>> = ({ canvas, timeline }) => {
    const frame = useCurrentFrame();
    const { fps } = useVideoConfig();

    return (
        <AbsoluteFill style={{ backgroundColor: canvas.backgroundColor }}>
            {timeline.map((event) => (
                <React.Fragment key={event.elementId}>
                    {renderElement(event, frame, fps)}
                </React.Fragment>
            ))}
        </AbsoluteFill>
    );
};
