import { Composition, registerRoot } from "remotion";
import { Main } from "./remotion/Main";
import { z } from "zod";
import React from "react";

const mainSchema = z.object({
    canvas: z.object({
        width: z.number(),
        height: z.number(),
        backgroundColor: z.string(),
    }),
    totalDuration: z.number(),
    timeline: z.array(z.object({
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
    }))
});

const RemotionRoot: React.FC = () => {
    return (
        <>
            <Composition
                id="Main"
                component={Main}
                durationInFrames={180}
                fps={30}
                width={1280}
                height={720}
                schema={mainSchema}
                defaultProps={{
                    canvas: {
                        width: 1280,
                        height: 720,
                        backgroundColor: "#ffffff"
                    },
                    totalDuration: 6,
                    timeline: []
                }}
                calculateMetadata={({ props }) => {
                    return {
                        durationInFrames: props.totalDuration * 30,
                        width: props.canvas.width,
                        height: props.canvas.height,
                    };
                }}
            />
        </>
    );
};

registerRoot(RemotionRoot);

