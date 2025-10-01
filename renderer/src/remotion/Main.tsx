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

    const style: React.CSSProperties = {
        position: 'absolute',
        ...event.props
    };

    const content = event.type === 'text' ? event.content : `[${event.type}: ${event.asset || event.elementId}]`;

    return <div style={style}>{content}</div>;
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
