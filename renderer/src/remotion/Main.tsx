import React from 'react';
import { AbsoluteFill, useCurrentFrame, useVideoConfig } from 'remotion';
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

    if (timeInSeconds < event.time) {
        return null;
    }

    const props = event.props || {};
    
    if (event.type === 'text') {
        const style: React.CSSProperties = {
            position: 'absolute',
            left: props.x,
            top: props.y,
            fontSize: props.fontSize || 24,
            fontWeight: 'bold',
            color: '#000',
            textAlign: 'center',
            transform: 'translate(-50%, -50%)',
        };
        return <div style={style}>{event.content}</div>;
    }

    if (event.type === 'icon') {
        const style: React.CSSProperties = {
            position: 'absolute',
            left: props.x,
            top: props.y,
            fontSize: props.fontSize || 120,
            transform: 'translate(-50%, -50%)',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: '10px',
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
