import { Composition, registerRoot } from "remotion";
import { Main, mainSchema } from './remotion/Main';
import { z } from "zod";
import React from "react";

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

const compositionSchema = z.object({
    timeline: mainSchema.shape.timeline,
    canvas: mainSchema.shape.canvas,
    totalDuration: mainSchema.shape.totalDuration,
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

export const RemotionRoot: React.FC = () => {
    return (
        <>
            <Composition
                id="Main"
                component={Main}
                durationInFrames={195}
                fps={30}
                width={1280}
                height={720}
                schema={compositionSchema}
                defaultProps={{
                    totalDuration: 10,
                    canvas: {
                        width: 1280,
                        height: 720,
                        backgroundColor: 'white',
                    },
                    timeline: [],
                    camera: [],
                }}
                calculateMetadata={({ props }) => {
                    return {
                        durationInFrames: Math.ceil(props.totalDuration * 30),
                        fps: 30,
                        width: props.canvas.width,
                        height: props.canvas.height,
                    };
                }}
            />
        </>
    );
};

registerRoot(RemotionRoot);

